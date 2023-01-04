package com.example.imagepro.expression;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.imagepro.DateAndTime;
import com.example.imagepro.Features;
import com.example.imagepro.MainActivity;
import com.example.imagepro.R;
import com.example.imagepro.calculate.Calculator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG="MainActivity";

    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private static TextToSpeech textToSpeech;
    private TextView textView;
    private Button button;
    private int count=-1;
    float x1, x2;

    private facialExpressionRecognition facialExpressionRecognition;
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);
        textView=findViewById(R.id.text1);
        //button=findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                count=0;
//            }
//        });

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        try{
            int inputSize=48;
            facialExpressionRecognition=new facialExpressionRecognition(getAssets(),CameraActivity.this,"model300.tflite",inputSize);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

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
//                    Voice voiceobj = new Voice("it-it-x-kda#male_2-local",
//                            Locale.getDefault(), 2, 0, false, null);

                   textToSpeech.setVoice(v);

                    textToSpeech.speak("You are in expression detection slide, swipe left to detect the expression of people around you. or swipe right to get back to main menu. ", TextToSpeech.QUEUE_FLUSH, null);

                }
            }

        });
       // Collator testApp;
//        textToSpeech= new TextToSpeech(testApp.getInstance().getApplicationContext(), this, "com.google.android.tts");
//        Set<String> a=new HashSet<>();
//        a.add("male");//here you can give male if you want to select male voice.
//        Voice v=new Voice("en-us-x-sfg#male_2-local",new Locale("en","US"),400,200,true,a);
//        textToSpeech.setVoice(v);
//        textToSpeech.setSpeechRate(0.8f);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);
    }
    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        mRgba=facialExpressionRecognition.recognizeImage(mRgba);
       // int count=0;
        String[] expression;
        String res1;
        if(count==0){
            expression=(facialExpressionRecognition.getres());
            int length=facialExpressionRecognition.getlength();
            if(length==0) {
                res1 = "There is no people in the scene";
            }
            else if(length==1) {
                 res1 = "person in the Scene is "+expression[0];
            }
            else{
                 res1 = "Total number of people in the scene is "+length+" out of this";
                 int Surprise=0;
                int Fear=0;
                int Angry=0;
                int Neutral=0;
                int Disgust=0;
                int Sad=0;
                int Happy=0;

                for(int i=0;i<length;i++) {
                     if(expression[i].equals("Surprise")){
                         Surprise++;
                     }
                     else if(expression[i].equals("Fear")){
                         Fear++;
                     }
                     else if(expression[i].equals("Angry")){
                         Angry++;
                     }
                     else if(expression[i].equals("Neutral")){
                         Neutral++;
                     }
                     else if(expression[i].equals("Disgust")){
                         Disgust++;
                     }
                     else if(expression[i].equals("Sad")){
                         Sad++;
                     }
                     else
                         Happy++;
                }
                 if(Surprise==1){
                     res1 += "1 person is Surprised";
                 }
                 else if(Surprise>1){
                     res1 += Surprise+" people are Surprised";
                 }
                if(Fear==1){
                    res1 += "1 person is in Fear";
                }
                else if(Fear>1){
                    res1 += Fear+" people are in Fear";
                }
                if(Angry==1){
                    res1 += "1 person is Angry";
                }
                else if(Angry>1){
                    res1 += Angry+" people are Angry";
                }
                if(Neutral==1){
                    res1 += "1 person is Neutral";
                }
                else if(Neutral>1){
                    res1 += Neutral+" people are Neutral";
                }
                if(Disgust==1){
                    res1 += "1 person is Disgust";
                }
                else if(Disgust>1){
                    res1 += Disgust+" people are Disgust";
                }
                if(Sad==1){
                    res1 += "1 person is Sad";
                }
                else if(Sad>1){
                    res1 += Sad+" people are Sad";
                }
                if(Happy==1){
                    res1 += "1 person is Happy";
                }
                else if(Happy>1){
                    res1 += Happy+" people are Happy";
                }
            }
            //final String res=get_emotion_text(expression);


            textView.setText(res1);
            textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);


            count=-1;

            //textToSpeech.speak(res, TextToSpeech.QUEUE_FLUSH, null);

        }





        return mRgba;

    }
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(CameraActivity.this, Features.class);
        startActivity(intent);

    }
    public boolean onTouchEvent(MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                //y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                //y2 = touchEvent.getY();
                if (x1 < x2) {
                    textToSpeech.stop();
                    count=0;


                }
                if (x1 > x2) {
                    textToSpeech.stop();
                    Intent intent = new Intent(CameraActivity.this, Features.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                }


                break;
        }

        return false;
    }
//    private String get_emotion_text(float emotion_v) {
//        String val="";
//
//        if(emotion_v>=0 && emotion_v<1){
//            //textToSpeech.speak("Surprise", TextToSpeech.QUEUE_FLUSH, null);
//            val="Surprise";
//        }
//        else if(emotion_v>=1 && emotion_v<1.8){
//           // textToSpeech.speak("Frar", TextToSpeech.QUEUE_FLUSH, null);
//            val="Fear";
//        }
//        else if(emotion_v>=1.8 && emotion_v<2.8){
//            //textToSpeech.speak("Angry", TextToSpeech.QUEUE_FLUSH, null);
//            val="Angry";
//        }
//        else if(emotion_v>=2.8 && emotion_v<3.8){
//            //textToSpeech.speak("Neutral", TextToSpeech.QUEUE_FLUSH, null);
//            val="Neutral";
//        }
//        else if(emotion_v>=3.8 && emotion_v<4.8){
//           // textToSpeech.speak("Sad", TextToSpeech.QUEUE_FLUSH, null);
//            val="Sad";
//        }
//        else if(emotion_v>=4.8 && emotion_v<5.8){
//            //textToSpeech.speak("Disgust", TextToSpeech.QUEUE_FLUSH, null);
//            val="Disgust";
//        }
//        else{
//           // textToSpeech.speak("Happy", TextToSpeech.QUEUE_FLUSH, null);
//            val="Happy";
//        }
//        return val;
//    }

}