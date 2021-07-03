package be.kuleuven.elcontador10.activities;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
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
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;

public class MainActivity extends FragmentActivity implements CachingObserver {
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

        Caching.INSTANCE.setAllData(this);
        Caching.INSTANCE.attachCaching(this);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getRoles() {
        Caching.INSTANCE.notifyAllObservers();
        return roles;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Caching.INSTANCE.detach(this);
    }

    /// Implementation of CachingObserver *********
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