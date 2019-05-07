package com.rsandhir.expenselog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SpendingsListView extends AppCompatActivity {

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    SharedPreferences sharedPreferences;
    ListView lv;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> Description = new ArrayList<String>();
    private ArrayList<String> Amount = new ArrayList<String>();
    ArrayList<String> date = new ArrayList<String>();
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
        date.clear();
        while (cursor.moveToNext()) {
            String fetch_id = cursor.getString(cursor.getColumnIndex("Id"));
            Id.add(fetch_id);
            Description.add(cursor.getString(cursor.getColumnIndex("Description")));
            Amount.add(cursor.getString(cursor.getColumnIndex("Amount")));
            date.add(cursor.getString(cursor.getColumnIndex("Date")));
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
        CustomAdapter ca = new CustomAdapter(SpendingsListView.this, Id, Description, Amount, date, arrChecked);
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
                final String[] arrTempList = new String[arrChecked.size()];
                for (int i = 0; i < Id.size(); i++) {
                    if (arrChecked.get(i) == true) {
                        arrTempList[i] = Id.get(i);
                    }
                }
                databaseHelper.deleteSelected(arrTempList);
                displayData();
                break;
            case R.id.edit_record:
                final String[] arrTempList1 = new String[arrChecked.size()];
                for (int i = 0; i < Id.size(); i++) {
                    if (arrChecked.get(i) == true) {
                        arrTempList1[i] = Id.get(i);
                    }
                }
                Log.d("multiSelect", "" + arrTempList1.length + " " + arrTempList1[0] + " " + arrTempList1[1]);
                if (arrTempList1.length == 1 || (arrTempList1.length == 2 && (arrTempList1[0] == null || arrTempList1[1] == null))) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.edit_alert, null);
                    final EditText editText = dialogView.findViewById(R.id.newamt);
                    dialogBuilder.setView(dialogView)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String newAmt;

                                            newAmt = editText.getText().toString();
                                            Log.d("lolu", "" + newAmt + " " + arrTempList1[0]);
                                            databaseHelper.editSelected(arrTempList1[0], newAmt);
                                            displayData();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                } else
                    Toast.makeText(getApplicationContext(), "Select the entry you want to edit!", Toast.LENGTH_SHORT).show();
                break;
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