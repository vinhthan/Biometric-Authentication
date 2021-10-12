package com.example.biometricauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Executor;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class MainActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        biometricAuth();
        clickAuth();
    }

    private void clickAuth() {
        Button btnAuth = findViewById(R.id.btn_auth);
        btnAuth.setOnClickListener(v -> {
            //checkBiometricAvailable();
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private void checkBiometricAvailable() {
        BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(MainActivity.this, "App can authenticate using biometrics", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(MainActivity.this, "No biometric features available on this device", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(MainActivity.this, "Biometric features are currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                final Intent intent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                intent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void biometricAuth() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull @org.jetbrains.annotations.NotNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "auth error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull @org.jetbrains.annotations.NotNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "auth success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "auth failed", Toast.LENGTH_SHORT).show();
            }
        });
        //setup
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Title")
                .setSubtitle("sub title")
                .setNegativeButtonText("cancel")
                .build();
    }

}