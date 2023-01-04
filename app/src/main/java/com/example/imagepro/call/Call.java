package com.example.imagepro.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.example.imagepro.message.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Call extends AppCompatActivity {
    private TextView phoneNo;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    float x1, x2;
    private static TextToSpeech textToSpeech;
    static int PERMISSION_CODE= 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(1);
                    Set<String> a=new HashSet<>();
                    a.add("male");//here you can give male if you want to select male voice.
                    Voice v=new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 1, false, a);
                    textToSpeech.getDefaultVoice();
                    textToSpeech.setVoice(v);
                    textToSpeech.speak("Click on volume up button and say the Phone Number.Swipe left to make a call, or swipe right to return to main menu.", TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });
        phoneNo = findViewById(R.id.editTextPhone);
        if (ContextCompat.checkSelfPermission(Call.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(Call.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

        }
    }
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(Call.this, Features.class);
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
                    String number = phoneNo.getText().toString();
                    number = number.replaceAll("\\s", "");
                    if(number.length()!=10){
                        textToSpeech.speak("Please give a valid number.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else {
                        Intent i = new Intent(Intent.ACTION_CALL);//invoking call Manager
                        i.setData(Uri.parse("tel:"+number));
                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Calling: " + number, Toast.LENGTH_SHORT).show();
                    }

                    break;

                }
                if (x2 < x1) {
                    textToSpeech.stop();
                    Intent i = new Intent(Call.this, Features.class);
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
                    phoneNo.setText(result.get(0));



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
                    startVoiceInput();


                }
                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                if (action == KeyEvent.ACTION_DOWN) {
//                    num=0;
//                    mes=1;
//                    startVoiceInput();
//                }
//                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}