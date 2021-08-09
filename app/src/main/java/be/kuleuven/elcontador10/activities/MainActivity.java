package be.kuleuven.elcontador10.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    Toolbar toolbar;

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        Caching.INSTANCE.setContext(this);
        setSupportActionBar(toolbar);
    }
    public void displayToolBar(Boolean display){
        int visibility = (display)? View.VISIBLE :View.INVISIBLE;
        toolbar.setVisibility(visibility);
    }
    public void displayTabLayout(Boolean display){
        int visibility = (display)? View.VISIBLE :View.GONE;
        tabLayout.setVisibility(visibility);
    }
    public void setHeaderText(String title) {
        toolbar.setTitle(title);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_three_buttons_menu,menu);
        return true;
    }
}