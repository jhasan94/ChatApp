package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private EditText mphoneNumber,mcode;
    private Button msend;

    String mverificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        userIsLoggedIn();

        mphoneNumber = findViewById(R.id.phoneNumber);
        mcode = findViewById(R.id.code);
        msend = findViewById(R.id.send);





        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mverificationId != null)
                    verifyPhoneNumberWithCode();
                else
                    startPhoneNumberverification();
            }
        });




        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mverificationId = verificationId;
                msend.setText("Verify code");

            }
        };
    }

    private void verifyPhoneNumberWithCode(){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mverificationId,mcode.getText().toString());
        signInWithPhoneAuthCredential(credential);

    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    userIsLoggedIn();
            }
        });
    }



    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
            finish();
            return;
        }

    }



    private void startPhoneNumberverification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mphoneNumber.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks

        );
    }
}
