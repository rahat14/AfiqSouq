package dev.mobile.afiqsouq.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpResp  implements Serializable {
    @SerializedName("email")
    @Expose
    private String email;
     @SerializedName("password")
     @Expose
     private String password ;


    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("billing")
    @Expose
    private Billing billing;
    @SerializedName("shipping")
    @Expose
    private Shipping shipping;

    public String getEmail() {
        return email;
    }

     public SignUpResp(String email, String password, String firstName, String lastName, String username, Billing billing, Shipping shipping) {
         this.email = email;
         this.password = password;
         this.firstName = firstName;
         this.lastName = lastName;
         this.username = username;
         this.billing = billing;
         this.shipping = shipping;
     }

     public String getPassword() {
         return password;
     }

     public void setPassword(String password) {
         this.password = password;
     }

     public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

     public static class Shipping  implements  Serializable{

         @SerializedName("first_name")
         @Expose
         private String firstName;
         @SerializedName("last_name")
         @Expose
         private String lastName;
         @SerializedName("company")
         @Expose
         private String company;
         @SerializedName("address_1")
         @Expose
         private String address1;
         @SerializedName("address_2")
         @Expose
         private String address2;
         @SerializedName("city")
         @Expose
         private String city;
         @SerializedName("state")
         @Expose
         private String state;
         @SerializedName("postcode")
         @Expose
         private String postcode;
         @SerializedName("country")
         @Expose
         private String country;

         public String getFirstName() {
             return firstName;
         }

         public Shipping(String firstName, String lastName, String company, String address1, String address2, String city, String state, String postcode, String country) {
             this.firstName = firstName;
             this.lastName = lastName;
             this.company = company;
             this.address1 = address1;
             this.address2 = address2;
             this.city = city;
             this.state = state;
             this.postcode = postcode;
             this.country = country;
         }

         public void setFirstName(String firstName) {
             this.firstName = firstName;
         }

         public String getLastName() {
             return lastName;
         }

         public void setLastName(String lastName) {
             this.lastName = lastName;
         }

         public String getCompany() {
             return company;
         }

         public void setCompany(String company) {
             this.company = company;
         }

         public String getAddress1() {
             return address1;
         }

         public void setAddress1(String address1) {
             this.address1 = address1;
         }

         public String getAddress2() {
             return address2;
         }

         public void setAddress2(String address2) {
             this.address2 = address2;
         }

         public String getCity() {
             return city;
         }

         public void setCity(String city) {
             this.city = city;
         }

         public String getState() {
             return state;
         }

         public void setState(String state) {
             this.state = state;
         }

         public String getPostcode() {
             return postcode;
         }

         public void setPostcode(String postcode) {
             this.postcode = postcode;
         }

         public String getCountry() {
             return country;
         }

         public void setCountry(String country) {
             this.country = country;
         }

     }
     public static class Billing implements  Serializable {

         @SerializedName("first_name")
         @Expose
         private String firstName;
         @SerializedName("last_name")
         @Expose
         private String lastName;
         @SerializedName("company")
         @Expose
         private String company;
         @SerializedName("address_1")
         @Expose
         private String address1;
         @SerializedName("address_2")
         @Expose
         private String address2;
         @SerializedName("city")
         @Expose
         private String city;
         @SerializedName("state")
         @Expose
         private String state;
         @SerializedName("postcode")
         @Expose
         private String postcode;
         @SerializedName("country")
         @Expose
         private String country;
         @SerializedName("email")
         @Expose
         private String email;
         @SerializedName("phone")
         @Expose
         private String phone;

         public String getFirstName() {
             return firstName;
         }

         public void setFirstName(String firstName) {
             this.firstName = firstName;
         }

         public String getLastName() {
             return lastName;
         }

         public Billing(String firstName, String lastName, String company, String address1, String address2, String city, String state, String postcode, String country, String email, String phone) {
             this.firstName = firstName;
             this.lastName = lastName;
             this.company = company;
             this.address1 = address1;
             this.address2 = address2;
             this.city = city;
             this.state = state;
             this.postcode = postcode;
             this.country = country;
             this.email = email;
             this.phone = phone;
         }

         public void setLastName(String lastName) {
             this.lastName = lastName;
         }

         public String getCompany() {
             return company;
         }

         public void setCompany(String company) {
             this.company = company;
         }

         public String getAddress1() {
             return address1;
         }

         public void setAddress1(String address1) {
             this.address1 = address1;
         }

         public String getAddress2() {
             return address2;
         }

         public void setAddress2(String address2) {
             this.address2 = address2;
         }

         public String getCity() {
             return city;
         }

         public void setCity(String city) {
             this.city = city;
         }

         public String getState() {
             return state;
         }

         public void setState(String state) {
             this.state = state;
         }

         public String getPostcode() {
             return postcode;
         }

         public void setPostcode(String postcode) {
             this.postcode = postcode;
         }

         public String getCountry() {
             return country;
         }

         public void setCountry(String country) {
             this.country = country;
         }

         public String getEmail() {
             return email;
         }

         public void setEmail(String email) {
             this.email = email;
         }

         public String getPhone() {
             return phone;
         }

         public void setPhone(String phone) {
             this.phone = phone;
         }

     }
}