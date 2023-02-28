package com.example.digimart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.digimart.Models.Product;
import com.example.digimart.R;
import com.example.digimart.Utils.Consts;
import com.example.digimart.databinding.ActivityProductDetailsBinding;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailsActivity extends AppCompatActivity {

    ActivityProductDetailsBinding activityProductDetailsBinding;
    Product currProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProductDetailsBinding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityProductDetailsBinding.getRoot());

        String name = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        int id = getIntent().getIntExtra("id",0);
        double price = getIntent().getDoubleExtra("price",0);

        Cart cart = TinyCartHelper.getCart();


        activityProductDetailsBinding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cart.addItem(currProduct,1);
                activityProductDetailsBinding.addToCartBtn.setEnabled(false);
                activityProductDetailsBinding.addToCartBtn.setText("Added in cart");

            }
        });

        Glide.with(this)
                .load(image)
                .into(activityProductDetailsBinding.productImage);
        activityProductDetailsBinding.productDerscription.setText(name);


        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+name+" </font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart){
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getProductDetails(int id)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = Consts.GET_PRODUCT_DETAILS_URL + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("success"))
                    {
                        JSONObject object = jsonObject.getJSONObject("product");
                        String desc = object.getString("description");
                        activityProductDetailsBinding.productDerscription.setText(Html.fromHtml(desc));


                        currProduct = new Product(
                                object.getString("name"),
                                Consts.PRODUCTS_IMAGE_URL+object.getString("image"),
                                object.getString("status"),
                                object.getDouble("price"),
                                object.getInt("stock"),
                                object.getInt("id"));
                    }
                    else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}