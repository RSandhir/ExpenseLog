package com.rsandhir.expenselog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class pocketmoney extends AppCompatActivity {

    Button sav_btn;
    EditText p_moneyet;
    int totalPmoney;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        int preTotal = sharedPreferences.getInt("PocketMoney", 0);
        if (preTotal > 0) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("Total", preTotal);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_pocketmoney);

        p_moneyet=findViewById(R.id.p_money);
        sav_btn=findViewById(R.id.save_btn);

        sav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPmoney= Integer.parseInt(p_moneyet.getText().toString());
                //Store SharedPref
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("PocketMoney", totalPmoney);
                editor.commit();
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("Total",totalPmoney);
                startActivity(i);
                finish();
            }
        });
    }
}