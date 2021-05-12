package be.kuleuven.elcontador10.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Cashing;
import be.kuleuven.elcontador10.background.interfaces.CashingObserver;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.model.StakeHolder;
import be.kuleuven.elcontador10.model.TransactionType;

public class MainActivity extends AppCompatActivity implements CashingObserver {
    private TextView header;

    private StakeholderLoggedIn loggedIn;
    private ArrayList <String> roles = new ArrayList<>();

    /*
    When activity first made from log in, select home fragment and take in the account parcel
    Set up the listener for the navigation bar
    */

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBottomMenu();

        Cashing.INSTANCE.setAllData(this);
        Cashing.INSTANCE.attachChasing(this);

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

    public ArrayList<String> getRoles() {
        return roles;
    }

    /// Implementation of CashingObserver *********
    @Override
    public void notifyRoles(List<String> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }
    @Override
    public void notifyCategories(List<TransactionType> transTypes) {
    }
    @Override
    public void notifyStakeHolders(List<StakeHolder> stakeHolders) {

    }


}