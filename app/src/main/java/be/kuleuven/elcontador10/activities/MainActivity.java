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
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.fragments.Settings;
import be.kuleuven.elcontador10.fragments.employees.Employees;
import be.kuleuven.elcontador10.fragments.Home;
import be.kuleuven.elcontador10.fragments.shops.Shops;
import be.kuleuven.elcontador10.fragments.tenants.Tenants;
import be.kuleuven.elcontador10.fragments.transactions.Transactions;
import be.kuleuven.elcontador10.fragments.transactions.TransactionsFilter;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;

    private TextView header;

    private FloatingActionButton buttonLeft;
    private FloatingActionButton buttonRight;

    private StakeholderLoggedIn loggedIn;
    private String fragmentName;
    private Fragment selectedFragment;

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
        loggedIn = (StakeholderLoggedIn) i.getParcelable("Account");

        //navigationView = findViewById(R.id.nav_view);
        header = findViewById(R.id.lblToolbarHeading);

        buttonLeft = findViewById(R.id.mainButtonLeft);
        buttonRight = findViewById(R.id.mainButtonRight);

        //homeButtons();
       // selectedFragment = new Home();
        //fragmentName = "Home";
        /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_host,
                selectedFragment, null).commit();

        navigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

         */
    }

    ///This is all the code required for the bottom Navigation Menu
    private void setBottomMenu(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_menu_main);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }

    /*
    Reads the item clicked title and then show the correct fragment
    */
    /*
    private boolean onNavigationItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        selectedFragment = null;

        switch (title) {
            case "Home":
                homeButtons();
                selectedFragment = new Home();
                break;
            case "Transactions":
                viewButtons();
                FilterTransactionsParcel filter = new FilterTransactionsParcel("*", "*",
                        "*", null, null);
                selectedFragment = new Transactions(filter);
                header.setText(R.string.transactions);
                break;
            case "Stores Storage":
                viewButtons();
                selectedFragment = new Shops();
                header.setText(R.string.shops_storage);
                break;
            case "Tenants":
                viewButtons();
                selectedFragment = new Tenants();
                header.setText(R.string.tenants);
                break;
            case "Employees":
                viewButtons();
                selectedFragment = new Employees();
                header.setText(R.string.employees);
                break;
        }

        header.setText(title);
        fragmentName = title;

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_host,
                selectedFragment, null).commit();

        return true;
    }

    /*
    Show the home main buttons: Settings and new transaction
    */
    /*
    public void homeButtons(){
        buttonLeft.setVisibility(View.VISIBLE);
        buttonLeft.setImageResource(R.drawable.icon_settings);
        buttonRight.setVisibility(View.VISIBLE);
        buttonRight.setImageResource(R.drawable.icon_add);
    }

    /*
    * Show the other fragment buttons: delete, add, and filter
    * */
    public void viewButtons(){
        buttonLeft.setVisibility(View.VISIBLE);
        buttonLeft.setImageResource(R.drawable.icon_add);
        buttonRight.setVisibility(View.VISIBLE);
        buttonRight.setImageResource(R.drawable.icon_filter);
    }

    /*
    * Hides all buttons. Used when viewing other fragments that has its own button
     */

    public void hideButtons(){
        buttonLeft.setVisibility(View.GONE);
        buttonRight.setVisibility(View.GONE);
    }

    public StakeholderLoggedIn getLoggedIn() {
        return loggedIn;
    }
/*
    public Fragment getSelectedFragment() { return selectedFragment; }

    public void setSelectedFragment(Fragment selectedFragment, String fragmentName) {
        this.fragmentName = fragmentName;
        this.selectedFragment = selectedFragment;
        header.setText(fragmentName);

        if (fragmentName.equals("Home")) homeButtons();
        else viewButtons();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_host,
                selectedFragment, null).commit();
    }
*/
    public void onLeftButton_Clicked(View view) {
        switch(fragmentName) {
            case "Home":
                // settings button clicked
                selectedFragment = new Settings();
                header.setText(R.string.settings);
                fragmentName = getString(R.string.settings);
                break;
            default:
                Toast.makeText(this, "Nothing to show.", Toast.LENGTH_SHORT).show();
                break;
        }

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_host,
                //selectedFragment, null).commit();
    }

    public void onRightButton_Clicked(View view) {
        switch(fragmentName) {
            case "Home":
                // TODO add transactions clicked
                break;
            case "Transactions":
                selectedFragment = new TransactionsFilter();
                header.setText(R.string.filter_transactions);
                fragmentName = getString(R.string.filter_transactions);
                break;
            default:
                Toast.makeText(this, "Nothing to show.", Toast.LENGTH_SHORT).show();
                break;
        }

       // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_host,
        //        selectedFragment, null).commit();
    }

}