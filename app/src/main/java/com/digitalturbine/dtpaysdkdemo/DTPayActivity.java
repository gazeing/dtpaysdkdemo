package com.digitalturbine.dtpaysdkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalturbine.dtpaysdk.DTPayClientBuilder;
import com.digitalturbine.dtpaysdk.dtpay.BillingStatusResponse;
import com.digitalturbine.dtpaysdk.dtpay.DTPayClient;
import com.digitalturbine.dtpaysdk.dtpay.DTPayResponseListner;
import com.digitalturbine.dtpaysdk.dtpay.PurchaseResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DTPayActivity extends AppCompatActivity implements DTPayResponseListner {
    @Bind(R.id.textView)
    TextView text_res;

    @Bind(R.id.textView3)
    TextView text_reference;



    @Bind(R.id.imageView)
    ImageView imageView;

    String reference = null;
    DTPayClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtpay);
        ButterKnife.bind(this);
        initDTPay();
    }

    private void initDTPay() {
        client = new DTPayClientBuilder(this)
                .setDTPayResponseListner(this)
                .build();
    }


    public void makePurchase(View v) {
        client.markPurchase(client.getVirtualGoods("Magic Sword", 792));
        text_reference.setText("...");
    }

    public void checkBillingStatus(View v) {
        if (reference != null) {
            client.checkBillingStatus(reference);
        }
    }

    @Override
    public void OnPurchaseResponse(PurchaseResponse response) {
        if (response != null) {
            this.reference = response.reference;
            text_reference.setText("Reference:\n"+this.reference);
        }

    }

    @Override
    public void OnBillingResponse(BillingStatusResponse response) {
        if (response != null) {
            text_res.setText(BillingStatusResponse.SuccessStatus.values()[response.ResponseCode - 1].toString());
            if (response.ResponseCode == 1)
                imageView.setVisibility(View.VISIBLE);
            else
                imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void OnError(String Error) {

    }
}
