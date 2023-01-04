package com.example.imagepro.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import com.example.imagepro.Battery;
import com.example.imagepro.DateAndTime;
import com.example.imagepro.Features;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;
import android.Manifest;
import android.content.pm.PackageManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SendMessage extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    float x1, x2;
    private TextView editTextNumber;
    private TextView editTextMessage;
    private String value;
    private static TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

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
                        textToSpeech.speak("Swipe left and say the Phone Number, and click on volume up button to send your location. or swipe right to return to main menu.", TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });

        ActivityCompat.requestPermissions(SendMessage.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        editTextMessage = findViewById(R.id.editTextTextMultiLine);
        editTextNumber = findViewById(R.id.editTextNumber);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
            //The key argument here must match that used in the other activity
        }
        editTextMessage.setText(value);
    }
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(SendMessage.this, Features.class);
        startActivity(intent);

    }
    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 < x2) {
                    textToSpeech.stop();
                    startVoiceInput();
//                    Intent intent = new Intent(Home.this, MessageReader.class);
//                    startActivity(intent);

                    break;

                    }
                if (x2 < x1) {
                    textToSpeech.stop();
                    Intent i = new Intent(SendMessage.this, Features.class);
                    startActivity(i);

                    break;

                }



        }
        return false;
    }
    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                editTextNumber.setText(result.get(0));


            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    textToSpeech.stop();
                    //TODO
                    String message = editTextMessage.getText().toString();
                    String number = editTextNumber.getText().toString();

                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(number, null, "Hey, my location is: "+message, null, null);
                    //Toast.makeText(getApplicationContext(), "Your location is sent to: "+number, Toast.LENGTH_SHORT).show();
                    String phno="";
                    for(int i=0;i<number.length();i++){
                        if(i%2==0) {
                            phno += number.charAt(i) + ",";
                        }
                        else {
                            phno += number.charAt(i);
                        }
                    }
                    textToSpeech.speak("your location is sent to: " + phno, TextToSpeech.QUEUE_FLUSH, null);

                }
                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                if (action == KeyEvent.ACTION_DOWN) {
//                    //TODO
//                }
//                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}

//    public void sendSMS(View view){
//
//        String message = editTextMessage.getText().toString();
//        String number = editTextNumber.getText().toString();
//
//        SmsManager mySmsManager = SmsManager.getDefault();
//        mySmsManager.sendTextMessage(number,null, message, null, null);
//    }
//}