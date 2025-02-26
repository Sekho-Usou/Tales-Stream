package talesstream.com.Activities;

import android.content.Intent; // Add this import
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import talesstream.com.R;

public class ForgotPassword1Activity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button continueButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password1);

        // For status bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find views by ID
        emailEditText = findViewById(R.id.F1Email);
        continueButton = findViewById(R.id.F1continueBtn);
        progressBar = findViewById(R.id.progressBar);
        backbtn = findViewById(R.id.f1back_btn);

        // Back button click
        backbtn.setOnClickListener(v -> finish());

        // Set click listener for Continue button
        continueButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            // Check if the email is empty
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required");
                return;
            }
            // Show progress bar while sending the password reset email
            progressBar.setVisibility(View.VISIBLE);

            // Send password reset email
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword1Activity.this, "Reset email sent to your email", Toast.LENGTH_LONG).show();

                    // Redirect to login page after email is sent
                    Intent intent = new Intent(ForgotPassword1Activity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Optional: close the current activity so it won't stay in the stack
                } else {
                    Toast.makeText(ForgotPassword1Activity.this, "Failed to send reset email", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
