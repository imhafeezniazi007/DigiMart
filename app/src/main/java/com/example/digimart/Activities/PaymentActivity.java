package com.example.digimart.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;

import com.example.digimart.R;
import com.example.digimart.Utils.Consts;
import com.example.digimart.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding activityPaymentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPaymentBinding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(activityPaymentBinding.getRoot());

        String orderCode = getIntent().getStringExtra("orderCode");

        activityPaymentBinding.webview.setMixedContentAllowed(true);
        activityPaymentBinding.webview.loadUrl(Consts.PAYMENT_URL + orderCode);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Payment</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}