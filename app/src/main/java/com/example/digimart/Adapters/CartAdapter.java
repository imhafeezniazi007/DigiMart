package com.example.digimart.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digimart.Models.Product;
import com.example.digimart.R;
import com.example.digimart.databinding.DialogQuantityBinding;
import com.example.digimart.databinding.ItemCartBinding;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartUpdateListener cartUpdateListener;
    Cart cart;

    public interface CartUpdateListener{
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products, CartUpdateListener cartUpdateListener) {
        this.context = context;
        this.products = products;
        this.cartUpdateListener = cartUpdateListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {


        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImg())
                .into(holder.itemCartBinding.image);

        holder.itemCartBinding.name.setText(product.getName());
        holder.itemCartBinding.price.setText("PKR "+product.getPrice());
        holder.itemCartBinding.quantity.setText("QTY "+product.getQuantity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogQuantityBinding dialogQuantityBinding = DialogQuantityBinding
                        .inflate(LayoutInflater.from(context));
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setView(dialogQuantityBinding.getRoot())
                        .create();

                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                dialogQuantityBinding.productName.setText(product.getName());
                dialogQuantityBinding.productStock.setText("Stock: "+product.getStock());
                dialogQuantityBinding.quantity.setText(String.valueOf(product.getQuantity()));

                dialogQuantityBinding.plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int quantity = product.getQuantity();
                        int stock = product.getStock();
                        if (quantity <= stock){
                            quantity++;
                        }
                        else {
                            dialogQuantityBinding.plusBtn.setEnabled(false);
                        }
                        product.setQuantity(quantity);
                        dialogQuantityBinding.quantity.setText(String.valueOf(quantity));

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartUpdateListener.onQuantityChanged();
                    }
                });

                dialogQuantityBinding.minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        if (quantity > 1){
                            quantity--;
                        }
                        else {
                            dialogQuantityBinding.minusBtn.setEnabled(false);
                        }
                        product.setQuantity(quantity);
                        dialogQuantityBinding.quantity.setText(String.valueOf(quantity));

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartUpdateListener.onQuantityChanged();

                    }
                });

                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

        ItemCartBinding itemCartBinding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCartBinding = ItemCartBinding.bind(itemView);

        }
    }
}
