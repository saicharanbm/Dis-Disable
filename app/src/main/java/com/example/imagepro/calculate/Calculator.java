package com.example.imagepro.calculate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagepro.DateAndTime;
import com.example.imagepro.Features;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;
import com.example.imagepro.location.CurrentLocation;
import com.example.imagepro.location.SendMessage;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Calculator extends AppCompatActivity {
    public TextView txtScreen;
    public TextToSpeech textToSpeech;
    public TextView txtInput;
    private boolean lastNumeric;

    // Represent that current state is in error or not
    private boolean stateError;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        setNumericOnClickListener();
        setOperatorOnClickListener();
        txtScreen = findViewById(R.id.txtScreen);
        txtInput = findViewById(R.id.txtInput);

        ImageButton button2 = findViewById(R.id.btnSpeak);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(1);
                    Set<String> a=new HashSet<>();
                    a.add("male");//here you can give male if you want to select male voice.
                    Voice v=new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 1, false, a);
                    //textToSpeech.setPitch(100f);
                    textToSpeech.getDefaultVoice();

                    textToSpeech.setVoice(v);
                    textToSpeech.speak("Opening the calculator......  just tap on the screen, and say what you want, to calculate, or say what you want, or click volume up button, to get back to home screen", TextToSpeech.QUEUE_FLUSH, null);

                }
            }

        });
    }


    private void setNumericOnClickListener() {
        // Create a common OnClickListener
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Just append/set the text of clicked button
                Button button = (Button) v;
                if (stateError) {
                    // If current state is Error, replace the error message
                    txtScreen.setText(button.getText());
                    stateError = false;

                } else {
                    // If not, already there is a valid expression so append to it
                    txtScreen.append(button.getText());
                }

                // Set the flag
                lastNumeric = true;

            }
        };
    }

    private void setOperatorOnClickListener() {
        // Create a common OnClickListener for operators

        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the current state is Error do not append the operator
                // If the last input is number only, append the operator
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                }
            }
        };

        // Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");  // Clear the screen
                txtInput.setText("");  // Clear the input
                // Reset all the states and flags
                lastNumeric = false;
                stateError = false;
            }
        });

        findViewById(R.id.btnSpeak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateError) {
                    // If current state is Error, replace the error message
                    txtScreen.setText("Try Again");
                    stateError = false;
                } else {
                    // If not, already there is a valid expression so append to it
                    textToSpeech.stop();
                    promptSpeechInput();
                }
                // Set the flag
                lastNumeric = true;

            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "hello");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void onEqual() {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (lastNumeric && !stateError) {
            // Read the expression
            final String inputNumber = txtInput.getText().toString();
            txtScreen.setText(inputNumber);
            // Create an Expression (A class from exp4j library)
            Expression expression = null;

            try {
                expression = null;
                try {
                    expression = new ExpressionBuilder(inputNumber).build();
                    double result = expression.evaluate();
                    txtScreen.setText(Double.toString(result).replaceAll("\\.0*$", ""));
                    Toast.makeText(Calculator.this, "Answer is", Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("Answer is " + txtScreen.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("tap on the screen and say what you want", TextToSpeech.QUEUE_ADD,null);
                    textToSpeech.setSpeechRate(1f);

                } catch (Exception e) {
                    txtScreen.setText("Error, tap on the screen and say again");
                    textToSpeech.speak("Error, tap on the screen and say again", TextToSpeech.QUEUE_FLUSH, null);


                }
            } catch (ArithmeticException ex) {
                // Display an error message
                txtScreen.setText("Error");
                textToSpeech.speak("Error, tap on the screen and say again", TextToSpeech.QUEUE_FLUSH, null);
                stateError = true;
                lastNumeric = true;

            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    final ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String change = result.get(0);
                    txtInput.setText(result.get(0));
                    // english-lang
                    change = change.replace("zero", "0");
                    change = change.replace(",", "");
                    change = change.replace("[", "");
                    change = change.replace("]", "");
                    change = change.replace("x", "*");
                    change = change.replace("X", "*");
                    change = change.replace("oneplus", "1 +");
                    change = change.replace("add", "+");
                    change = change.replace("sub", "-");
                    change = change.replace("to", "2");
                    change = change.replace(" plus ", "+");
                    change = change.replace("two", "2");
                    change = change.replace(" minus ", "-");
                    change = change.replace(" times ", "*");
                    change = change.replace(" into ", "*");
                    change = change.replace(" in2 ", "*");
                    change = change.replace(" multiply by ", "*");
                    change = change.replace(" divide by ", "/");
                    change = change.replace(" by ", "/");
                    change = change.replace("divide", "/");
                    change = change.replace("equal", "=");
                    change = change.replace("equals", "=");


                    if (change.contains("=")) {
                        change = change.replace("=", "");
                        txtInput.setText(change);
                        onEqual();

                    } else {
                        txtInput.setText(change);
                        onEqual();
                    }
                }

                break;
            }
        }
    }

    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(Calculator.this, Features.class);
        startActivity(intent);

    }

    public boolean onKeyDown(int keyCode, @Nullable KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            textToSpeech.stop();
            Intent intent = new Intent(getApplicationContext(), Features.class);
            startActivity(intent);
        }
        return true;
    }

//    public void onPause () {
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//        }
//        super.onPause();
//    }

}