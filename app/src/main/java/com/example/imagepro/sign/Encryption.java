package com.example.imagepro.sign;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.imagepro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption extends AppCompatActivity {

    private TextView editText;
    private ListView listView;
    private Button button;

    private DatabaseReference databaseReference;

    private String stringMessage;
    private byte encryptionKey[] = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_letters);

        editText = findViewById(R.id.change_text);
        button = findViewById(R.id.text_speech_button);
        //listView = findViewById(R.id.listView);

        try{
            databaseReference = FirebaseDatabase.getInstance().getReference("Message");

            try {
                cipher = Cipher.getInstance("AES");
                decipher = Cipher.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }

            secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    stringMessage = dataSnapshot.getValue().toString();
                    stringMessage = stringMessage.substring(1,stringMessage.length()-1);

                    String[] stringMessageArray = stringMessage.split(", ");
                    Arrays.sort(stringMessageArray);
                    String[] stringFinal = new String[stringMessageArray.length*2];

                    try {
                        for (int i = 0; i<stringMessageArray.length; i++) {
                            String[] stringKeyValue = stringMessageArray[i].split("=", 2);
                            stringFinal[2 * i] = (String) android.text.format.DateFormat.format("dd-MM-yyyy hh:mm a", Long.parseLong(stringKeyValue[0]));
                            stringFinal[2 * i + 1] = AESDecryptionMethod(stringKeyValue[1]);
                        }


                        //listView.setAdapter(new ArrayAdapter<String>(Encryption.this, android.R.layout.simple_list_item_1, stringFinal));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                databaseReference.child(Long.toString(date.getTime())).setValue(AESEncryptionMethod(editText.getText().toString()));
                editText.setText("hi");
            }
        });

    }

//    public void sendButton(View view){
//
//        Date date = new Date();
//        databaseReference.child(Long.toString(date.getTime())).setValue(AESEncryptionMethod(editText.getText().toString()));
//        editText.setText("");
//
//    }

    private String AESEncryptionMethod(String string){

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String returnString = null;

        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;

        byte[] decryption;

        try {
            decipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }
}
