package be.kuleuven.elcontador10.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;

    private TextView header;

    private StakeholderLoggedIn loggedIn;

    /*
    When activity first made from log in, select home fragment and take in the account parcel
    Set up the listener for the navigation bar
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBottomMenu();

        Bundle i = this.getIntent().getExtras();
        loggedIn = i.getParcelable("Account");

        header = findViewById(R.id.lblToolbarHeading);
    }

    ///This is all the code required for the bottom Navigation Menu
    private void setBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_menu_main);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }

    public void setTitle(String title) {
        header.setText(title);
    }

    public StakeholderLoggedIn getLoggedIn() {
        return loggedIn;
    }
}