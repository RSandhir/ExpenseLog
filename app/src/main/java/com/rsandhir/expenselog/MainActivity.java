package com.rsandhir.expenselog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    FirebaseAuth mAuth;



    ProgressBar progressBar;
    DatabaseHelper mydb;
    EditText e1, e2;
    FloatingActionButton b1;
    int current_spnd;
    int totalPmoney;
    TextView usr_email;
    String channelId = "notif_channel_id";
    SharedPreferences sharedPreferences;
    NavigationView navigationView;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();


        sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        totalPmoney = sharedPreferences.getInt("PocketMoney", 0);

        progressBar = findViewById(R.id.progress_bar);
        createNotificationChannel();

        current_spnd = sharedPreferences.getInt("curr_spend", 0);
        float progress_percent = (((float) current_spnd) / totalPmoney) * 100;
        Log.d("progressView", progress_percent + " ," + totalPmoney + " ," + current_spnd);
        progressBar.setProgress((int) progress_percent);
        mydb = new DatabaseHelper(this);
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        b1 = findViewById(R.id.b1);


        navigationView = findViewById(R.id.navigationview);
        if (user != null) {
            View headerView = navigationView.getHeaderView(0);
            usr_email = headerView.findViewById(R.id.usr_email);
            usr_email.setText(user.getEmail());
        }


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int spnd_amt = 0;
                String discr;
                try {
                    spnd_amt = Integer.parseInt(e2.getText().toString());
                    discr = e1.getText().toString();
                    current_spnd += spnd_amt;
                    float progress_percent = (((float) current_spnd) / totalPmoney) * 100;
                    progressBar.setProgress((int) progress_percent);
                    createNotification((int) progress_percent);

                    boolean isInserted = mydb.insertdata(discr, spnd_amt);
                    if (isInserted == true)
                        Toast.makeText(MainActivity.this, "Inserted" + progress_percent, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Not Inserted", Toast.LENGTH_LONG).show();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MainActivity.this, "Enter value", Toast.LENGTH_SHORT).show();
                }

            }
        });

        navigationView.setNavigationItemSelectedListener(this);


    }


    public void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence channelName = "Some Channel";
        int importance = NotificationManager.IMPORTANCE_LOW;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void createNotification(int progress) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyId = 1;
        this.current_spnd = progress;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(MainActivity.this, channelId)
                    .setContentTitle("Spendings")
                    .setContentText(progress + "%")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setProgress(totalPmoney, progress, false)
                    .setChannelId(channelId)
                    .setOngoing(true)
                    .build();
        }
        notificationManager.notify(notifyId, notification);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.viewnotes) {
            Toast.makeText(this, "view", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, SpendingsListView.class);
            startActivity(intent);
        } else if (id == R.id.edit_pmoney) {
            Toast.makeText(this, "Enter new value", Toast.LENGTH_LONG).show();
            writeTotal(0);
            Intent i = new Intent(MainActivity.this, pocketmoney.class);
            startActivity(i);
        } else if (id == R.id.share) {
            Toast.makeText(this, "share", Toast.LENGTH_LONG).show();
        } else if (id == R.id.logout) {
            Toast.makeText(this, "logout", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
            sendToLogin();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void writeTotal(int totalPmoney) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PocketMoney", totalPmoney);
        editor.commit();
    }
}
