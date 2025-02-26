package talesstream.com.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import talesstream.com.MainActivity;
import talesstream.com.R;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private MaterialButton loginBtn;
    private FirebaseAuth firebaseAuth;
    private TextView signupText, forgotPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // For status bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        // Adjust layout for keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        loginEmail = findViewById(R.id.loginEmailBtn);
        loginPassword = findViewById(R.id.loginPasswordBtn);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar1);
        signupText = findViewById(R.id.signupBtn);
        forgotPassword = findViewById(R.id.forgotPassword);

        // Hide the progress bar initially
        progressBar.setVisibility(View.GONE);

        // Forgot Password
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPassword1Activity.class);
            startActivity(intent);
        });

        // Signup click
        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // When login button is pressed
        loginBtn.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            // Validate input fields
            if (TextUtils.isEmpty(email)) {
                loginEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                loginPassword.setError("Password is required");
                return;
            }

            // Show ProgressBar and hide login button while login is in progress
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            // Attempt to sign in
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);

                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (user != null) {
                                Toast.makeText(LoginActivity.this, "Please verify your email. Check your inbox.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMessage = "Login failed";
                            if (task.getException() instanceof FirebaseAuthException) {
                                errorMessage = mapFirebaseAuthError(((FirebaseAuthException) task.getException()).getErrorCode());
                            }
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    /**
     * Map FirebaseAuth error codes to user-friendly messages.
     */
    private String mapFirebaseAuthError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                return "The email address is invalid.";
            case "ERROR_USER_NOT_FOUND":
                return "No account found with this email.";
            case "ERROR_WRONG_PASSWORD":
                return "Incorrect password. Try again.";
            default:
                return "An unexpected error occurred.";
        }
    }
}
