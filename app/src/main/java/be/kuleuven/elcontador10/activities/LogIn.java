package be.kuleuven.elcontador10.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.AccountManager;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.background.interfaces.LogInInterface;

public class LogIn extends AppCompatActivity implements LogInInterface {

    private TextView username;
    private TextView password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        username = findViewById(R.id.txtbxLogInUsername);
        password = findViewById(R.id.txtbxLogInPassword);
        login = findViewById(R.id.btnLogIn);

        password.setOnKeyListener(this::onKey);
    }

    private boolean onKey(View v, int keyCode, KeyEvent event) {
        // when in password TextBox and pressed enter
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            onBtnLogin_Clicked(v);
            return true;
        }
        return false;
    }

    public void onBtnLogin_Clicked(View view) {
        String text_username = username.getText().toString();
        String text_password = password.getText().toString();

        if (text_username.equals("") || text_password.equals(""))
            showToast("Missing input!");
        else {
            AccountManager manager = AccountManager.getInstance();
            manager.Authenticate(this, text_username, text_password);
        }
    }

    @Override
    public Context getContext() { return this; }

    @Override
    public void onLoginSucceed(String username, StakeholderLoggedIn loggedIn, ArrayList<String> roles) {
        showToast("Welcome " + username + "!");
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("Account", loggedIn);
        i.putExtra("Roles", roles);
        startActivity(i);
        finish();
    }

    @Override
    public void onLoginFailed(String reason) {
        showToast(reason);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}