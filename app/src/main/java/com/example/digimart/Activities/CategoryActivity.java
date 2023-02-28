package com.example.digimart.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.text.Html;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.digimart.Adapters.ProductAdapter;
import com.example.digimart.Models.Product;
import com.example.digimart.R;
import com.example.digimart.Utils.Consts;
import com.example.digimart.databinding.ActivityCategoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {


    ActivityCategoryBinding activityCategoryBinding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCategoryBinding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(activityCategoryBinding.getRoot());

        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);


        int catId = getIntent().getIntExtra("catId", 0);
        String categoryName = getIntent().getStringExtra("categoryName");

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+categoryName+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getProducts(catId);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        activityCategoryBinding.productList.setLayoutManager(layoutManager);
        activityCategoryBinding.productList.setAdapter(productAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProducts(int catId) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Consts.GET_PRODUCTS_URL + "?category_id=" + catId;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")){
                    JSONArray productsArray = object.getJSONArray("products");
                    for(int i =0; i< productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("name"),
                                Consts.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getInt("stock"),
                                childObj.getInt("id")
                        );
                        products.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }
}