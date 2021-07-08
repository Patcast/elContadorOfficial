package be.kuleuven.elcontador10.activities;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;

public class MainActivity extends FragmentActivity  {
    private TextView header;
    BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        header = findViewById(R.id.lblToolbarHeading);
        setBottomMenu();
    }

    ///This is all the code required for the bottom Navigation Menu
    private void setBottomMenu(){
        bottomNavigationView = findViewById(R.id.bottom_navigation_menu_main);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }
    public void displayBottomMenu(Boolean display){
        int visibility = (display)? View.VISIBLE :View.INVISIBLE;
        bottomNavigationView.setVisibility(visibility);
    }

    public void setTitle(String title) {
        header.setText(title);
    }










}