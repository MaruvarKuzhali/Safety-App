package com.example.safety;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.ImageView;
import android.widget.Toast;
//import android.permission.CALL_PHONE;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;

import java.security.Provider;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    Button b1, b2;
    ImageView imageView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseHandler myDB;
    private final int REQUEST_CHECK_CODE = 8989;
    private LocationSettingsRequest.Builder builder;
    String x = "", y = "";
    private static final int REQUEST_LOCATION = 1;


    LocationManager locationManager;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        imageView = findViewById(R.id.chat);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        myDB = new DatabaseHandler(this);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.emergency_alarm);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS();
        } else {
            startTrack();
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        b2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mp.start();
                Toast.makeText(getApplicationContext(), "PANIC BUTTON STARTED", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                startActivity(intent);
            }
        });
    }


    private void loadData() {
        ArrayList<String> thelist = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "no content to show.", Toast.LENGTH_SHORT).show();
        } else {
            String msg = "I NEED HELP.MY LOCATION IS LATITUDE:" + x + " AND LONGITUDE:" + y +"\n ";

            msg += "http://maps.google.com/?q=" + x + "," + y;
            String number = "";
            while (data.moveToNext()) {
                thelist.add(data.getString(1));
                number = number + data.getString(1) + (data.isLast() ? "" : ";");
                call();

            }
            if (!thelist.isEmpty()) {
                sendSms(number, msg, true);
            }
        }

    }

    private void sendSms(String number, String msg, boolean b) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);

        smsIntent.putExtra("sms_body", msg);
        //smsIntent.setType("vnd.android-dir/mms-sms");
        startActivity(smsIntent);
    }

    private void call() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:9842928762"));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(i);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }

    }

    private void startTrack() {

        if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity2.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        x = String.valueOf(lat);
                        y = String.valueOf(lon);
                    }
                }
            });
            /*Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(locationGPS!=null){
                double lat = locationGPS.getLatitude();
                double lon = locationGPS.getLongitude();
                x = String.valueOf(lat);
                y = String.valueOf( lon );
            }*/

        }

    }
    private void onGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( "Enable GPS" ).setCancelable( false ).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ) );

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}