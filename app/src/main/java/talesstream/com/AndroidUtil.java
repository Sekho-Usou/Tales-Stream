package talesstream.com;

import android.content.Intent;

public class AndroidUtil {
    // Function to pass UserModel data to another activity using Intent
    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("email", userModel.getEmail());
        intent.putExtra("password", userModel.getPassword());
    }
    // Function to retrieve UserModel from an Intent
    public static UserModel getUserModelFromIntent(Intent intent) {
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        return new UserModel(email, password);
    }
}
