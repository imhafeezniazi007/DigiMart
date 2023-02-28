package com.example.digimart.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.digimart.Adapters.CartAdapter;
import com.example.digimart.Models.Product;
import com.example.digimart.Utils.Consts;
import com.example.digimart.databinding.ActivityCheckoutBinding;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding activityCheckoutBinding;
    CartAdapter adapter;
    ArrayList<Product> products;
    double totalPrice = 0;
    final int deliveryCharges = 50;
    ProgressDialog progressDialog;
    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCheckoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(activityCheckoutBinding.getRoot());


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();

        cart = TinyCartHelper.getCart();

        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        adapter = new CartAdapter(this, products, new CartAdapter.CartUpdateListener() {
            @Override
            public void onQuantityChanged() {
                activityCheckoutBinding.subtotal.setText(String.format("PKR %.2f", cart.getTotalPrice()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        activityCheckoutBinding.cartList.setLayoutManager(layoutManager);
        activityCheckoutBinding.cartList.addItemDecoration(itemDecoration);
        activityCheckoutBinding.cartList.setAdapter(adapter);

        activityCheckoutBinding.subtotal.setText(String.format("PKR %.2f", cart.getTotalPrice()));

        totalPrice = cart.getTotalPrice().doubleValue() + deliveryCharges;
        activityCheckoutBinding.total.setText("PKR " + totalPrice);

        activityCheckoutBinding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(activityCheckoutBinding.nameBox.getText()) &&
                        TextUtils.isEmpty(activityCheckoutBinding.phoneBox.getText()) &&
                        TextUtils.isEmpty(activityCheckoutBinding.addressBox.getText()) &&
                        TextUtils.isEmpty(activityCheckoutBinding.dateBox.getText())) {
                    activityCheckoutBinding.nameBox.setError("Required!");
                    activityCheckoutBinding.phoneBox.setError("Required!");
                    activityCheckoutBinding.addressBox.setError("Required!");
                    activityCheckoutBinding.dateBox.setError("Required!");
                    //processOrder();
                } else {
                    //myOrder();
                    processOrder();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void processOrder() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject productOrder = new JSONObject();
        JSONObject dataObject = new JSONObject();
        try {

            productOrder.put("address", activityCheckoutBinding.addressBox.getText().toString());
            productOrder.put("buyer", activityCheckoutBinding.nameBox.getText().toString());
            productOrder.put("comment", activityCheckoutBinding.commentBox.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("phone", activityCheckoutBinding.phoneBox.getText().toString());
            productOrder.put("serial", "cab8c1a4e4421a3b");
            productOrder.put("shipping", "");
            productOrder.put("shipping_location", "");
            productOrder.put("shipping_rate", "0.0");
            productOrder.put("status", "WAITING");
            productOrder.put("tax", deliveryCharges);
            productOrder.put("total_fees", totalPrice);

            JSONArray product_order_detail = new JSONArray();
            for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);

                JSONObject productObj = new JSONObject();
                productObj.put("amount", quantity);
                productObj.put("price_item", product.getPrice());
                productObj.put("product_id", product.getId());
                productObj.put("product_name", product.getName());
                product_order_detail.put(productObj);
            }

            dataObject.put("product_order", productOrder);
            dataObject.put("product_order_detail", product_order_detail);


        } catch (JSONException e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Consts.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("success")) {
                        Toast.makeText(CheckoutActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                        String orderNumber = response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Successful")
                                .setCancelable(false)
                                .setMessage("Your order number is: " + orderNumber)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                                        intent.putExtra("orderCode", orderNumber);
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Failed")
                                .setMessage("Something went wrong, please try again.")
                                .setCancelable(false)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        Toast.makeText(CheckoutActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    Log.e("res", response.toString());
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security", "secure_code");
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}