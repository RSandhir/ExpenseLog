package com.rsandhir.expenselog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class SpendingsListView extends AppCompatActivity {

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    SharedPreferences sharedPreferences;
    ListView lv;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> Description = new ArrayList<String>();
    private ArrayList<String> Amount = new ArrayList<String>();
    ArrayList<Boolean> arrChecked;
    private String[] titlesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spendings_list_view);
        lv = findViewById(R.id.lv);
    }

    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }

    private void displayData() {
        Cursor cursor = databaseHelper.getAllData();
        Id.clear();
        Description.clear();
        Amount.clear();
        while (cursor.moveToNext()) {
            String fetch_id = cursor.getString(cursor.getColumnIndex("Id"));
            Id.add(fetch_id);
            Description.add(cursor.getString(cursor.getColumnIndex("Description")));
            Amount.add(cursor.getString(cursor.getColumnIndex("Amount")));
        }
        //Find total spendings
        int sum = 0;
        for (String i : Amount) {
            sum += Integer.parseInt(i);
        }
        sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("curr_spend", sum);
        editor.commit();
        arrChecked = new ArrayList<Boolean>();
        for (int i = 0; i < Id.size(); i++) {
            arrChecked.add(false);
        }
        CustomAdapter ca = new CustomAdapter(SpendingsListView.this, Id, Description, Amount, arrChecked);
        lv.setAdapter(ca);
        Log.d("CheckArr", "" + arrChecked);
        cursor.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_selected:
                String[] arrTempList = new String[arrChecked.size()];
                for (int i = 0; i < Id.size(); i++) {
                    if (arrChecked.get(i) == true) {
                        arrTempList[i] = Id.get(i);
                    }
                }
                databaseHelper.deleteSelected(arrTempList);
                displayData();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SpendingsListView.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}