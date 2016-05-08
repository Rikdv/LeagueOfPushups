package com.bletori.leagueofpushups;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class PushupsActivity extends AppCompatActivity implements View.OnClickListener {
    int amount;
    TextView tvPushups;
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushups);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pushups");
        Bundle bundle = getIntent().getExtras();
        amount = bundle.getInt("amount");

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.puRelativeLayout1);
        rel.setOnClickListener(this);
        tvPushups = (TextView)findViewById(R.id.puTvAmountPushups);
        current = amount;
        tvPushups.setText("" + current);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onClick(View v) {
        current--;
        if(current != 0){
            tvPushups.setText("" + current);
        }else{
            this.finish();
        }
    }
}
