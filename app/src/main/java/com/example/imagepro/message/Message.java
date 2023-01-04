package com.example.imagepro.message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagepro.DateAndTime;
import com.example.imagepro.Features;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Message extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private int num=0,mes=0;
    float x1, x2;
    private TextView editTextNumber;
    private TextView editTextMessage;
    private String value;
    private static TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
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
                    textToSpeech.speak("Click on volume up button and say the Phone Number, and click on volume down button and day the message you want to send.Swipe left to send the message, or swipe right to return to main menu.", TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });

        ActivityCompat.requestPermissions(Message.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        editTextMessage = findViewById(R.id.content);
        editTextNumber = findViewById(R.id.Number);
    }
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(Message.this, Features.class);
        startActivity(intent);

    }
    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 < x2) {//send message

                    String message = editTextMessage.getText().toString();
                    String number = editTextNumber.getText().toString();
                    number = number.replaceAll("\\s", "");
                    if(number.length()!=10){
                        textToSpeech.speak("Please give a valid number.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(message.length()==0){
                        textToSpeech.speak("Message you want to send is empty.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else {
                        SmsManager mySmsManager = SmsManager.getDefault();
                        mySmsManager.sendTextMessage(number, null, message, null, null);
                        //Toast.makeText(getApplicationContext(), "Your your message is sent to: " + number, Toast.LENGTH_SHORT).show();
                       String phno="";
                       for(int i=0;i<number.length();i++){
                           if(i%2==0) {
                               phno += number.charAt(i) + ",";
                           }
                           else {
                               phno += number.charAt(i);
                           }
                       }

                        textToSpeech.speak("your message is sent to: " + phno, TextToSpeech.QUEUE_FLUSH, null);
                        //Toast.makeText(getApplicationContext(), phno, Toast.LENGTH_SHORT).show();
                    }

                    break;

                }
                if (x2 < x1) {
                    textToSpeech.stop();
                    Intent i = new Intent(Message.this, Features.class);
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
                if(num==1) {
                    editTextNumber.setText(result.get(0));
                }
                else if(mes==1){
                    editTextMessage.setText(result.get(0));
                }


            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    textToSpeech.stop();
                    num=1;
                    mes=0;
                    startVoiceInput();


                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    textToSpeech.stop();
                    num=0;
                    mes=1;
                    startVoiceInput();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}