package dev.mobile.afiqsouq.View.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dev.mobile.afiqsouq.Adapter.CartListAdapter;
import dev.mobile.afiqsouq.Models.CartDbModel;
import dev.mobile.afiqsouq.Models.CouponResp;
import dev.mobile.afiqsouq.Models.CreateOrderResp;
import dev.mobile.afiqsouq.Models.PrefUserModel;
import dev.mobile.afiqsouq.Models.TaxREsp;
import dev.mobile.afiqsouq.Models.deliveryZoneResp;
import dev.mobile.afiqsouq.R;
import dev.mobile.afiqsouq.Utils.Constants;
import dev.mobile.afiqsouq.Utils.SharedPrefManager;
import dev.mobile.afiqsouq.database.CartDatabase;
import dev.mobile.afiqsouq.services.RetrofitClient;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListPage extends AppCompatActivity {


    public TextView cartNumber, tax_fee, sub_total, deliveryChargeTV, total, discount, paid, product_name, product_price;
    Button checkoutout, apply_couppon;
    RecyclerView rv_shoppingCart;
    List<CartDbModel> cartList = new ArrayList<>();
    List<CartDbModel> orderList = new ArrayList<>();
    List<CreateOrderResp.coupon_lines> coupon_lines = new ArrayList<>();
    EditText coupon_no;
    boolean isCouponUsed = false;
    CartListAdapter adapter;
    double toatalAmount;
    double disCountValue = 0;
    double rate = 0.0;
    String method_title = "Flat rate", method_Id = "flat_rate", deliveryCharge = "50", deliveryChargeWithOutTax = "100";
    DecimalFormat dec = new DecimalFormat("#0.0");
    double delivery_charge = 50;
    AlertDialog alert;
    LinearLayout cartLayout, emptyCartLayout;
    double totalCharge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        //content
        //coupon apply
        coupon_no = findViewById(R.id.textview_shoppingcart_couponNo);
        apply_couppon = findViewById(R.id.button_shoppingcart_applyCoupon);

        rv_shoppingCart = findViewById(R.id.recyclerview_shoppingcart_cartItem); //shows cart item

        sub_total = findViewById(R.id.textview_shoppingcart_subtotal_amount);
        tax_fee = findViewById(R.id.textview_shoppingcart_shippingfee_amount);
        total = findViewById(R.id.textview_shoppingcart_total_amount);
        discount = findViewById(R.id.textview_shoppingcart_discount_amount);
        paid = findViewById(R.id.textview_shoppingcart_tobe_paid_amount);
        checkoutout = findViewById(R.id.button_shoppingcart_checkoutd);
        deliveryChargeTV = findViewById(R.id.deliveryCharge);
        emptyCartLayout = findViewById(R.id.emptyContainer);
        cartLayout = findViewById(R.id.cartContainer);
        cartLayout.setVisibility(View.GONE);
        rate = loadTaxFormCache();
        rv_shoppingCart.setLayoutManager(new LinearLayoutManager(this));


        checkoutout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(getApplicationContext(), SelectDeliveryAddress.class);
                p.putExtra("MODEL", BuildOrderModel());
                p.putExtra("TOTAL", paid.getText().toString());
                startActivity(p);
                //   BuildOrderModel();

            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.button_mycart_shopnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apply_couppon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coupon = coupon_no.getText().toString();

                if (TextUtils.isEmpty(coupon)) {
                    Toasty.warning(getApplicationContext(), "Please Enter a Valid Coupon !!!", 1).show();
                } else {
                    getCoupon(coupon);
                }
            }
        });

    }

    private void getCoupon(String coupon) {
        ProgressDialog dialog = new ProgressDialog(CartListPage.this);
        dialog.setMessage("Checking For Coupon ...");
        String authHeader = "Basic " + Base64.encodeToString(Constants.BASE.getBytes(), Base64.NO_WRAP);
        dialog.setCancelable(false);
        dialog.show();

        Call<List<CouponResp>> getCall = RetrofitClient.getInstance().getApi()
                .getTheCoupon(
                        authHeader, coupon
                );

        getCall.enqueue(new Callback<List<CouponResp>>() {
            @Override
            public void onResponse(Call<List<CouponResp>> call, Response<List<CouponResp>> response) {
                if (response.code() == 200) {
                    // check the list
                    List<CouponResp> couponList = response.body();
                    try {
                        if (couponList.size() > 0) {
                            dialog.dismiss();
                            CouponResp couponModel = couponList.get(0);

                            if (!isCouponUsed) {
                                setUpCoupon(couponModel);
                            }

                        } else {
                            dialog.dismiss();
                            Toasty.error(getApplicationContext(), "Coupon Error : There is no such Coupon", 1).show();

                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        Toasty.error(getApplicationContext(), "Error Msg : " + e.getMessage(), 1).show();

                    }
                } else {
                    dialog.dismiss();
                    Toasty.error(getApplicationContext(), "Error Code : " + response.code(), 1).show();

                }
            }

            @Override
            public void onFailure(Call<List<CouponResp>> call, Throwable t) {
                dialog.dismiss();
                Toasty.error(getApplicationContext(), "Error Msg : " + t.getMessage(), 1).show();
            }
        });
    }

    private void setUpCoupon(CouponResp couponModel) {
        double minTotalTheresHold = Double.parseDouble(couponModel.getMinimumAmount());
        double maxTotalTheresHold = Double.parseDouble(couponModel.getMaximumAmount());

        if (totalCharge < minTotalTheresHold) {
            coupon_no.setText("");
            Toasty.warning(getApplicationContext(), "To Avail this Coupon Please Spend At Least BDT " + minTotalTheresHold, 1).show();
        } else {
            Toasty.success(getApplicationContext(), "Coupon Applied SuccessFully !!!", 1).show();
            isCouponUsed = true;
            discount.setTextColor(Color.RED);
            discount.setText("-" + Constants.BDT_SIGN + couponModel.getAmount());
            disCountValue = Double.parseDouble(couponModel.getAmount());
            totalCharge = Double.parseDouble(paid.getText().toString());
            totalCharge -= Double.parseDouble(couponModel.getAmount());

            paid.setText("" + totalCharge);
            // create model
            coupon_lines.add(new CreateOrderResp.coupon_lines(couponModel.getCode()));
        }

    }


    private void loadAllCartItem() {
        deliveryChargeTV.setText(Constants.BDT_SIGN + deliveryCharge);
        delivery_charge = Double.parseDouble(deliveryCharge);
        // countCartItem();
        CartDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cartList = CartDatabase.getDatabase(getApplicationContext()).dao().fetchAllTodos();

                    /*
                    Handler refresh = new Handler(Looper.getMainLooper());
refresh.post(new Runnable() {
    public void run()
    {
        byeSetup();
    }
})

                     */
                runOnUiThread(() -> {
                    if (cartList != null && !cartList.isEmpty()) // i know its werid but thats r8 cheaking list is popluted
                    {
                        cartLayout.setVisibility(View.VISIBLE);
                        emptyCartLayout.setVisibility(View.GONE);
                        orderList.clear();
                        orderList.addAll(cartList);
                        adapter = new CartListAdapter(cartList, CartListPage.this, delivery_charge, rate);
                        rv_shoppingCart.setAdapter(adapter);
                        // total
                        double totalMoney = calculateTotal(cartList);
                        //setting sub amount
                        sub_total.setText(Constants.BDT_SIGN + Math.round(totalMoney));
                        Log.d("TAG", "run: " + "totall" + totalMoney + ((totalMoney * (rate / 100))));
                        // calculating tax
                        tax_fee.setText(Constants.BDT_SIGN + dec.format(totalMoney * (rate / 100)));
                        totalMoney = totalMoney + ((totalMoney * (rate / 100)));


                        totalCharge = Math.round(totalMoney + delivery_charge);

                        total.setText(Constants.BDT_SIGN + totalCharge);

                        paid.setText("" + (totalCharge + (disCountValue * (-1))));
                    } else {
                        // show  empty layout
                        cartLayout.setVisibility(View.INVISIBLE);
                        emptyCartLayout.setVisibility(View.VISIBLE);

                    }
                });


            }
        });


    }

    private double calculateTotal(List<CartDbModel> todoList) {
        toatalAmount = 0;
        for (CartDbModel item : todoList) {
            toatalAmount = toatalAmount + (item.unit_price * item.qty);
        }
        return toatalAmount;

    }

    private void countCartItem() {

        TextView mybag_item_count_tv;
        mybag_item_count_tv = findViewById(R.id.textview_discover_cartNumber);

//        new AsyncTask<String, Void, List<CartDbModel>>() {
//            @Override
//            protected List<CartDbModel> doInBackground(String... params) {
//                return database.dao().fetchAllTodos();
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            protected void onPostExecute(List<CartDbModel> todoList) {
//
//
//
//
//            }
//        }.execute();

        CartDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                List<CartDbModel> list = CartDatabase.getDatabase(getApplicationContext()).dao().fetchAllTodos();
                if (list != null && !list.isEmpty()) // i know its werid but thats r8 cheaking list is popluted
                {

//                    mybag_item_count_tv.setText(list.size() + "");
                } else {
                    // show  empty layout

//                    mybag_item_count_tv.setText("0");
                }

                //Log.d("TAG", list.size() + "");

            }
        });


    }


    public Double loadTaxFormCache() {
        String country = SharedPrefManager.getInstance(getApplicationContext())
                .getUser().getCountry().toLowerCase();
        String rate = "0.00";
        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = getSharedPreferences("TAX", MODE_PRIVATE);
        String key = "TAX_KEY";
        Gson gson = new Gson();
        String response = shref.getString(key, "");
        List<TaxREsp> lstArrayList = gson.fromJson(response,
                new TypeToken<List<TaxREsp>>() {
                }.getType());

        assert lstArrayList != null;
        for (TaxREsp item : lstArrayList) {

            if (item.getCountry().toLowerCase().equals(country)) {

                rate = item.getRate();
                break;
            }

        }

        return Double.parseDouble(rate);

    }

    public CreateOrderResp BuildOrderModel() {
        // get user
        PrefUserModel userModel = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        //
        //String firstName, String lastName, String address1, String address2, String city, String state, String postcode, String country)
        CreateOrderResp.Shipping shipping = new CreateOrderResp.Shipping(userModel.getName(), userModel.getName(),
                userModel.getDeliveryAddress(), " ", userModel.getState(), userModel.getState(),
                " ", userModel.getCountry());

        // inserting line item
        List<CreateOrderResp.LineItem> itemList = new ArrayList<>();
        for (CartDbModel cartDbModel : cartList) {

            if (cartDbModel.variation_id == 0) {
                CreateOrderResp.LineItem item = new CreateOrderResp.LineItem(cartDbModel.product_id, cartDbModel.qty);
                itemList.add(item);
            } else {

                CreateOrderResp.LineItem item = new CreateOrderResp.LineItem(cartDbModel.product_id, cartDbModel.qty, cartDbModel.variation_id);
                itemList.add(item);
            }

        }

        // shipping lines
        List<CreateOrderResp.ShippingLine> shippingLineList = new ArrayList<>();
        CreateOrderResp.ShippingLine shippingLineModel = new CreateOrderResp.ShippingLine(method_Id, method_title, deliveryChargeWithOutTax + "");
        shippingLineList.add(shippingLineModel);
        // billing model
        //String firstName, String lastName, String address1, String address2, String city, String state, String postcode, String country, String email, String phone
        CreateOrderResp.Billing billingModel = new CreateOrderResp.Billing(
                userModel.getName(), " ",
                userModel.getDeliveryAddress(), " ", userModel.getState(), userModel.getState(),
                " ", userModel.getCountry(), userModel.getMail(), userModel.getPh()
        );
        Log.d("TAG", "BuildOrderModel: " + userModel.getId());
        // create the final model
        //String paymentMethod, String paymentMethodTitle, Boolean setPaid, Billing billing, Shipping shipping, List<LineItem> lineItems, List<ShippingLine> shippingLines

        try {
            if (coupon_lines.size() == 0) {
                coupon_lines = null;
            }
        } catch (Exception r) {
            coupon_lines = null;
        }

        CreateOrderResp orderModel = new CreateOrderResp(Constants.COD, Constants.cashOnDelivery, false,
                billingModel, shipping, itemList, shippingLineList, coupon_lines, userModel.getId());

        //   CreateOrder(orderModel);

        return orderModel;

    }

    @Override
    protected void onStart() {

        // check if  the user  id
        rate = loadTaxFormCache();
        if (SharedPrefManager.getInstance(getApplicationContext()).isUserLoggedIn()) {
            checkForDeliveryCharge();
        } else {
            // user not loagged in
            TriggerDilogue();
        }


        super.onStart();


    }

    private void TriggerDilogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You Are Not Logged In !!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        try {
                            alert.dismiss();
                            finish();
                        } catch (Exception r) {
                            finish();
                        }
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void checkForDeliveryCharge() {
        ProgressDialog dialog = new ProgressDialog(CartListPage.this);
        dialog.setMessage("Getting Delivery Charge");
        dialog.show();
        // check for delivery charge and id title
        String zoneID = "3";
        if (SharedPrefManager.getInstance(getApplicationContext()).getUser().getState().toLowerCase().equals("dhaka")) {

            zoneID = Constants.INSIDE_DHAKA;


        } else {
            // outside of dhaka
            zoneID = Constants.OUTSIDE_DHAKA;

        }

        String authHeader = "Basic " + Base64.encodeToString(Constants.BASE.getBytes(), Base64.NO_WRAP);

        // make the call
        Call<List<deliveryZoneResp>> deliveryZoneRespCall = RetrofitClient.getInstance()
                .getApi()
                .getDeliveryCharge(authHeader, zoneID);

        deliveryZoneRespCall.enqueue(new Callback<List<deliveryZoneResp>>() {
            @Override
            public void onResponse(Call<List<deliveryZoneResp>> call, Response<List<deliveryZoneResp>> response) {

                if (response.code() == 200) {
                    List<deliveryZoneResp> deliveryZoneRespList = response.body();
                    try {
                        method_Id = deliveryZoneRespList.get(0).getMethodId();
                        method_title = deliveryZoneRespList.get(0).getMethodTitle();

                        deliveryCharge = deliveryZoneRespList.get(0).getSettings().getCost().getValue();
                        deliveryChargeWithOutTax = deliveryCharge;

                        //calculate with tax
                        double charge = Double.parseDouble(deliveryCharge);
                        deliveryCharge = String.valueOf(charge + (charge * (rate / 100)));
                        dialog.dismiss();
                        loadAllCartItem();

                    } catch (Exception e) {
                        Toasty.error(getApplicationContext(), "Error " + e.getMessage(), 1).show();
                        deliveryCharge = "10";
                        dialog.dismiss();
                        loadAllCartItem();
                    }

                } else {
                    Toasty.error(getApplicationContext(), "Error " + response.code(), 1).show();
                    deliveryCharge = "10";
                    dialog.dismiss();
                    loadAllCartItem();
                }


            }

            @Override
            public void onFailure(Call<List<deliveryZoneResp>> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Error " + t.getMessage(), 1).show();
                deliveryCharge = "10";
                dialog.dismiss();
                loadAllCartItem();
            }
        });

    }

    public void setPaid(double value) {
        paid.setText("" + (value + (disCountValue * (-1))));
    }
}