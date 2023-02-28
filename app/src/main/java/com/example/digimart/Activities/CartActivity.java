package com.example.digimart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.digimart.Adapters.CartAdapter;
import com.example.digimart.Models.Product;
import com.example.digimart.databinding.ActivityCartBinding;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding activityCartBinding;
    CartAdapter cartAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartBinding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(activityCartBinding.getRoot());


        products = new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();

        for (Map.Entry<Item, Integer> itemIntegerMap : cart.getAllItemsWithQty().entrySet())
        {

            Product product = (Product) itemIntegerMap.getKey();
            int quantity = itemIntegerMap.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }



        cartAdapter = new CartAdapter(this, products, new CartAdapter.CartUpdateListener() {
            @Override
            public void onQuantityChanged() {
                activityCartBinding.subtotal.setText(String.format("PKR %.2f",cart.getTotalPrice()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        activityCartBinding.cartList.setLayoutManager(layoutManager);
        activityCartBinding.cartList.addItemDecoration(dividerItemDecoration);
        activityCartBinding.cartList.setAdapter(cartAdapter);

        activityCartBinding.subtotal.setText(String.format("PKR %.2f",cart.getTotalPrice()));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        activityCartBinding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });



        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Shopping Cart</font>"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}