package talesstream.com.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import talesstream.com.AndroidUtil;
import talesstream.com.MainActivity;
import talesstream.com.R;
import talesstream.com.UserModel;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //for status bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && getIntent().getExtras() != null) {
            // User is logged in, and we have notification data
            String userId = getIntent().getExtras().getString("userId");

            if (userId != null) {
                firebaseFirestore.collection("users").document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    UserModel model = documentSnapshot.toObject(UserModel.class);

                                    // Navigate to MainActivity
                                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(mainIntent);

                                    // Navigate to ChatActivity
                                    Intent chatIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                    AndroidUtil.passUserModelAsIntent(chatIntent, model);
                                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(chatIntent);
                                    finish();
                                } else {
                                    navigateToLogin();
                                }
                            } else {
                                navigateToLogin();
                            }
                        });
            } else {
                navigateToLogin();
            }

        } else {
            // If user is not logged in, delay transition to the login screen
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (currentUser != null) {
                    // User is logged in, navigate to MainActivity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    // Navigate to LoginActivity if not logged in
                    navigateToLogin();
                }
                finish();
            }, 1000); // 1 second delay
        }
    }

    // Navigate to the LoginActivity
    private void navigateToLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
