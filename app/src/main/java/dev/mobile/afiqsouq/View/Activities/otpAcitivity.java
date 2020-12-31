package dev.mobile.afiqsouq.View.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;

import java.util.Random;

import dev.mobile.afiqsouq.Models.Recived_Sign_up;
import dev.mobile.afiqsouq.Models.SignUpResp;
import dev.mobile.afiqsouq.R;
import dev.mobile.afiqsouq.Utils.Constants;
import dev.mobile.afiqsouq.Utils.SharedPrefManager;
import dev.mobile.afiqsouq.View.Home_Activity;
import dev.mobile.afiqsouq.services.RetrofitClient;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class otpAcitivity extends AppCompatActivity {

    PinView pinView;
    MaterialButton verifyBtn;
    SignUpResp user_model;
    String ph_num;
    TextView errorTv;

    SpinKitView spinKitView;
    String verification_code = "000" ;
    int IS_RECOVER = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_acitivity);
        pinView = findViewById(R.id.pinView_verificationn_OTP);
        verifyBtn = findViewById(R.id.button_verification_verify);
        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.GONE);
        errorTv = findViewById(R.id.error) ;
        user_model = (SignUpResp) getIntent().getSerializableExtra("MODELS");
        if (user_model != null) {
            ph_num = user_model.getBilling().getPhone();
            sendOtp(ph_num);
        }

        findViewById(R.id.imageview_verification_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.resendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_model = (SignUpResp) getIntent().getSerializableExtra("MODELS");
                if (user_model != null) {
                    Toasty.success(getApplicationContext() ,"OTP Send " , Toasty.LENGTH_LONG).show();
                    ph_num = user_model.getBilling().getPhone();
                    sendOtp(ph_num);
                }
            }
        });


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredPin = "000" ;
               try{
                    enteredPin = pinView.getText().toString() ;
               }
               catch (Exception r ){
                   Toasty.error(getApplicationContext() , "Try Again Some Error With Otp !!" , 1).show();
               }

                //Toasty.success(getApplicationContext() , " " + enteredPin , 1).show();
               if(verification_code.equals(enteredPin)){
                  registerUser(user_model);
               }
               else {
                   Toasty.error(getApplicationContext() , "Wrong Pin" , 1).show();
               }
            }
        });

    }

    private void sendOtp(String ph_num) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        verification_code = getRandomNumberString();
                        String msg = "afiqsouq.com OTP is ";
                        msg += verification_code;

                        // seend the otp to the user
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();
                        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                        RequestBody body = RequestBody.create(mediaType, "");
                        Request request = new Request.Builder()
                                .url("http://66.45.237.70/api.php?username=afiqsouq2021&password=12afiqsouq3434$&number=88"+ ph_num+"&message=" + msg)
                                .method("POST", body)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build();
                        Response response = client.newCall(request).execute();
                       // Log.d("TAG", "response Code: " + response.code());
                       // Log.d("TAG" , "response code " + response.body());

                    }

                    catch (Throwable t ) {
                        Log.d("TAG", "error: " +t.getMessage());
                    }

                }
            }
        });




    }





    public void registerUser(SignUpResp user_model) {  // register here
        spinKitView.setVisibility(View.VISIBLE);
        String authHeader = "Basic " + Base64.encodeToString(Constants.BASE.getBytes(), Base64.NO_WRAP);

        Call<Recived_Sign_up> call = RetrofitClient.getInstance().getApi().
                postUserRegister(authHeader, user_model);

        call.enqueue(new Callback<Recived_Sign_up>() {
            @Override
            public void onResponse(Call<Recived_Sign_up> call, retrofit2.Response<Recived_Sign_up> response) {
                if (response.code() == 201) {


                    Recived_Sign_up model = response.body();
                    SignUpResp.Shipping shippingMOdel = model.getShipping();


                    //  String id, String name, String email, String adress, String country, String ph, String state

                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                            model.getId() + "",
                            model.getUsername(),
                            model.getEmail(),
                            shippingMOdel.getAddress1(),
                            shippingMOdel.getCountry(),
                            model.getBilling().getPhone(),
                            shippingMOdel.getState()

                    );

                    SharedPrefManager.getInstance(getApplicationContext())
                            .saveUser(model.getEmail());

                    spinKitView.setVisibility(View.GONE);

                    // saved  the data
                    Intent p = new Intent(getApplicationContext(), Home_Activity.class);
                    p.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(p);
                    finish();


                } else {
                    spinKitView.setVisibility(View.GONE);
                    Constants.PrintMsg("Error : CODE  " + response.code());
                    if (response.code() == 400) {
                        Toasty.error(getApplicationContext(), "An account is already registered with your email address or UserName ",
                                2
                        ).show();
                        errorTv.setText("There is User With Same Email or UserName Please Use a Unique One Email Or UserName");

                    }
                }
            }

            @Override
            public void onFailure(Call<Recived_Sign_up> call, Throwable t) {
                Toasty.error(getApplicationContext(), "ERROR MSG : " + t.getMessage() , 1 )
                        .show();
            }
        });
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    @Override
    protected void onStart() {


        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
