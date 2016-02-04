package com.digitalturbine.dtpaysdkdemo;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity  {

    TextView text1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.text1);


    }


    public void showAppiaDemo(View v){
        startActivity(new Intent(this,AppiaActivity.class));
    }


    public void showDTpayDemo(View v){
        startActivity(new Intent(this,DTPayActivity.class));
    }




}
