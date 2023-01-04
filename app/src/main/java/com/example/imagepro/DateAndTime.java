package com.example.imagepro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DateAndTime extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private TextView format7;
    float x1,x2,y1,y2;
    private String finalDateTime;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_and_time);

        String dateTime = null;
        String Time = null;
        Calendar calendar = null;
        SimpleDateFormat simpleDateFormat,simpleDate;
        format7 = (TextView) findViewById(R.id.format7);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
        }
        simpleDate = new SimpleDateFormat("KK:mm");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Time = simpleDate.format(calendar.getTime()).toString();
        }
        simpleDateFormat = new SimpleDateFormat("'date is' dd-LLLL-yyyy 'and time is' KK:mm aaa ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateTime = simpleDateFormat.format(calendar.getTime()).toString();
        }
        format7.setText(Time);
        format7.getText().toString();

        finalDateTime = dateTime;
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
                    textToSpeech.speak(finalDateTime, TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("swipe left to listen again and swipe right to return back to main menu", TextToSpeech.QUEUE_ADD, null);

                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(DateAndTime.this, Features.class);
        startActivity(intent);

    }
    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 > x2) {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textToSpeech.stop();
                            Intent intent = new Intent(DateAndTime.this, Features.class);
                            startActivity(intent);

                        }
                    }, 1000);

                }

                if(x1<x2) {
                    textToSpeech.stop();
                    textToSpeech.speak(finalDateTime, TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("swipe left to listen again and swipe right to return back in main menu", TextToSpeech.QUEUE_ADD, null);
                }
        }

        return false;
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}