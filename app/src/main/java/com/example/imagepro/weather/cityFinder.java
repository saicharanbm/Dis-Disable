package com.example.imagepro.weather;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imagepro.DateAndTime;
import com.example.imagepro.Features;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class cityFinder extends AppCompatActivity {
    private TextView city;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static int firstTime = 0;
    private TextView mVoiceInputTv;
    private EditText editText;
    float x1, x2, y1, y2;
    private static TextToSpeech textToSpeech;
    //private DBHandler dbHandler;
    static String Readmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);
        //String city=null;
        editText = findViewById(R.id.searchCity);
        ImageView backButton = findViewById(R.id.backButton);
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
                textToSpeech.speak("Swipe right and say the place name .  Swipe left to go to home Screen. ", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(cityFinder.this, Features.class);
        startActivity(intent);

    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        firstTime = 1;
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    textToSpeech.stop();
                    firstTime = 1;
                    Intent intent = new Intent(cityFinder.this, Features.class);
                    startActivity(intent);

                }
                if (x1 > x2) {
                    textToSpeech.stop();
                    startVoiceInput();
//                    Intent intent = new Intent(Home.this, MessageReader.class);
//                    startActivity(intent);

                    break;
                }


                break;
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
                editText.setText(result.get(0));
                String newCity = editText.getText().toString();
                Intent intent = new Intent(cityFinder.this, weatherHome.class);
                intent.putExtra("City", newCity);
                startActivity(intent);

            }
        }


//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                String newCity = editText.getText().toString();
//                Intent intent = new Intent(cityFinder.this, weatherHome.class);
//                intent.putExtra("City", newCity);
//                startActivity(intent);
//
//
//                return false;
//            }
//        });


    }
}
