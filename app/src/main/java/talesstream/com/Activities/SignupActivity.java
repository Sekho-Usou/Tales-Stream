package talesstream.com.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import talesstream.com.R;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText signupEmail, signupPassword, signupRePassword;
    private MaterialButton signupBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ImageView signupBack;
    private ProgressBar signupProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // For status bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupRePassword = findViewById(R.id.signupRePassword);
        signupBack = findViewById(R.id.signupBack);
        signupBtn = findViewById(R.id.signupBtn);
        signupProgressBar = findViewById(R.id.progressBar1);

        signupBack.setOnClickListener(v -> {
            // Finish the current activity and go back to the previous one
            finish();
        });

        signupBtn.setOnClickListener(v -> {
            String email = signupEmail.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();
            String rePassword = signupRePassword.getText().toString().trim();

            // Input validations
            if (TextUtils.isEmpty(email)) {
                signupEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                signupPassword.setError("Password is required");
                return;
            }

            if (password.length() < 6) {
                signupPassword.setError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(rePassword)) {
                signupRePassword.setError("Passwords do not match");
                return;
            }

            // Show the progress bar when the operation starts
            signupProgressBar.setVisibility(View.VISIBLE);

            // Register user in Firebase
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // Hide the progress bar regardless of the outcome
                        signupProgressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Send verification email
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                    if (verificationTask.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Verification email sent. Please verify and log in.", Toast.LENGTH_SHORT).show();

                                        // Store user details in Firestore after verification
                                        user.reload().addOnCompleteListener(reloadTask -> {
                                            if (user.isEmailVerified()) {
                                                storeUserInFirestore(user.getEmail(), password);
                                            }
                                        });

                                        // Go to LoginActivity after sign-up
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // Handle errors
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                signupPassword.setError("Weak password");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                signupEmail.setError("Invalid email");
                            } catch (FirebaseAuthUserCollisionException e) {
                                signupEmail.setError("Account already exists");
                            } catch (Exception e) {
                                Toast.makeText(SignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        // Navigate to LoginActivity when the "Login now" text is clicked
        TextView loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Store user in Firestore database
    private void storeUserInFirestore(String email, String password) {
        User user = new User(email, password);  // Create User model
        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show());
    }

    // User model class
    public static class User {
        public String email;
        public String password;

        public User() {}

        public User(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
