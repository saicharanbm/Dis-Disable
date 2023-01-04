package com.example.imagepro.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagepro.DateAndTime;
import com.example.imagepro.Features;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CurrentLocation extends AppCompatActivity{

    //Button button_location;
    FusedLocationProviderClient fusedLocationProviderClient;
    private int first =0,loc=0;
    float x1, x2;
    private final static int REQUEST_CODE=100;
    private TextView textView_location;
    private LocationManager locationManager;
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        textToSpeech = new TextToSpeech(CurrentLocation.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.setSpeechRate(1);
                    Set<String> a=new HashSet<>();
                    a.add("male");//here you can give male if you want to select male voice.
                    Voice v=new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 1, false, a);
                    textToSpeech.getDefaultVoice();
                    textToSpeech.setVoice(v);
                   // textToSpeech.speak("wait for a minute we are getting you your current location", TextToSpeech.QUEUE_FLUSH, null);

                   // textToSpeech.speak("we have got your location. swipe left to listen to it or swipe right to return back to main menu", TextToSpeech.QUEUE_ADD, null);
                }
            }
        });

        textView_location = findViewById(R.id.text_location);
       // button_location = findViewById(R.id.button_location);
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(CurrentLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CurrentLocation.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        if(first==0) {
            getLastLocation();
            first++;
        }

//        button_location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                //create method

//            }
//        });

//        if(loc==0) {
//            loc++;
//            textToSpeech.speak("we have got your location. swipe left to listen to it or swipe right to return back in main menu", TextToSpeech.QUEUE_ADD, null);
//        }


    }

    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(CurrentLocation.this, Features.class);
        startActivity(intent);

    }
    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== getPackageManager().PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location !=null){
                                Geocoder geocoder=new Geocoder(CurrentLocation.this, Locale.getDefault());
                                try {
                                    List<Address> addresses=geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String address = addresses.get(0).getAddressLine(0);

                                    textView_location.setText(address);
                                    //textToSpeech.speak("Yo " , TextToSpeech.QUEUE_FLUSH, null);
                                    if(loc==0) {
                                        loc++;
                                        textToSpeech.speak("we have got your location. swipe left to listen to it or swipe right to return back to main menu", TextToSpeech.QUEUE_ADD, null);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
        else{
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(CurrentLocation.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else{
                Toast.makeText(this,"Permission Required",Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @SuppressLint("MissingPermission")
//    private void getLocation() {
//
//        try {
//            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5, CurrentLocation.this);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
//        try {
//            Geocoder geocoder = new Geocoder(CurrentLocation.this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//            String address = addresses.get(0).getAddressLine(0);
//
//            textView_location.setText(address);
//            //textToSpeech.speak("Yo " , TextToSpeech.QUEUE_FLUSH, null);
//            if(loc==0) {
//                loc++;
//                textToSpeech.speak("we have got your location. swipe left to listen to it or swipe right to return back in main menu", TextToSpeech.QUEUE_ADD, null);
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    textToSpeech.stop();
                    Intent i = new Intent(CurrentLocation.this, SendMessage.class);
                    i.putExtra("key",textView_location.getText().toString());
                    startActivity(i);
                }
                return true;

            default:
                return super.dispatchKeyEvent(event);
        }
    }
    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 < x2) {
                    if(textView_location.getText().toString().isEmpty()){
                        textToSpeech.speak("Please turn on location", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else {
                        textToSpeech.speak("Your current location is " +  textView_location.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("swipe left to listen again, swipe right to return back to main menu or click on volume up button to share your location with others.", TextToSpeech.QUEUE_ADD, null);

                    }

                }
                if (x1 > x2) {
                    textToSpeech.stop();
                    Intent i = new Intent(CurrentLocation.this, Features.class);
                    startActivity(i);

                }
        }
        return false;
    }



}