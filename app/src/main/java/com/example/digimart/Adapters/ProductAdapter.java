package com.example.digimart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digimart.Activities.ProductDetailsActivity;
import com.example.digimart.Models.Product;
import com.example.digimart.R;
import com.example.digimart.databinding.ItemProductsBinding;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImg())
                .into(holder.itemProductsBinding.imageView);
        holder.itemProductsBinding.textView.setText(product.getName());
        holder.itemProductsBinding.price.setText("PKR " + product.getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("title", product.getName());
                intent.putExtra("image", product.getImg());
                intent.putExtra("id", product.getId());
                intent.putExtra("price", product.getPrice());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        ItemProductsBinding itemProductsBinding;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            itemProductsBinding = ItemProductsBinding.bind(itemView);

        }
    }

}
