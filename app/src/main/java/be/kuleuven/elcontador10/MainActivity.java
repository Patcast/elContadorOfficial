package be.kuleuven.elcontador10;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import be.kuleuven.elcontador10.background.Caching;

public class MainActivity extends AppCompatActivity {
    public interface TopMenuHandler {
        void onToolbarTitleClick();
    }
    private TabLayout tabLayout;
    private BottomNavigationView bottomMenu;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private TextView  stakeHolderInitialReceivables,stakeHolderInitialPayables,stakeHolderSumOfTransactions,txtTitlePayables,txtTitleReceivables,txtMonthlySummary, txtStakeholder, labelStakeholder;
    private ConstraintLayout stakeholderDetails;
    private SharedPreferences.Editor editor;
    private static final String SAVED_EMAIL_KEY = "email_key";
    //public TopMenuHandler currentTopMenuHandler;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("loggedInDetails",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        stakeholderDetails = findViewById(R.id.stakeHolderDetails);
        //stakeHolderBalance = findViewById(R.id.txtViewBalance);
        stakeHolderInitialPayables=findViewById(R.id.textInitialPayables);
        stakeHolderInitialReceivables =findViewById(R.id.textInitialRecivables);
        stakeHolderSumOfTransactions =findViewById(R.id.textSumOfCash);
        txtMonthlySummary=findViewById(R.id.txt_montly_summary);
        txtTitlePayables =findViewById(R.id.textTitlePayables);
        txtTitleReceivables =findViewById(R.id.textTitleReceivables);
        txtStakeholder = findViewById(R.id.textStakeholder);
        labelStakeholder = findViewById(R.id.labelStakeholder);
        Caching.INSTANCE.setContext(this);
        setSupportActionBar(toolbar);

        bottomMenu = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomMenu,navController);
        //toolbar.setOnClickListener(view -> {if(currentTopMenuHandler!=null)currentTopMenuHandler.onToolbarTitleClick();});
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                getMenuInflater().inflate(R.menu.top_three_buttons_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
    }

   /* public void setCurrentMenuClicker(TopMenuHandler currentTopMenuHandler) {
        this.currentTopMenuHandler = currentTopMenuHandler;
    }*/
    public void saveLoggedInState(String email){
        editor.putString(SAVED_EMAIL_KEY,email);
        editor.commit();
    }
    public String returnSavedLoggedEmail(){
        return sharedPreferences.getString(SAVED_EMAIL_KEY,null);
    }
    public void signOut(){
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Caching.INSTANCE.signOut();
    }

    public void displayToolBar(Boolean display){
        int visibility = (display)? View.VISIBLE :View.INVISIBLE;
        toolbar.setVisibility(visibility);
    }
    public void displayTabLayout(Boolean display){
        int visibility = (display)? View.VISIBLE :View.GONE;
        tabLayout.setVisibility(visibility);
    }
    public void displayBottomNavigationMenu(Boolean display){
        int visibility = (display)? View.VISIBLE :View.GONE;
        bottomMenu.setVisibility(visibility);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void displayStakeHolderDetails(boolean display, String sumOfTrans, String initialReceivables, String initialPayables, String stakeholderID) {
        if (display) {
            stakeholderDetails.setVisibility(View.VISIBLE);
            txtMonthlySummary.setVisibility(View.VISIBLE);
            stakeHolderSumOfTransactions.setText(sumOfTrans);

            if (initialReceivables==null || initialReceivables.equals("$0.00")) {
                txtTitleReceivables.setVisibility(View.GONE);
                stakeHolderInitialReceivables.setVisibility(View.GONE);
            } else {
                txtTitleReceivables.setVisibility(View.VISIBLE);
                stakeHolderInitialReceivables.setVisibility(View.VISIBLE);
                stakeHolderInitialReceivables.setText(initialReceivables);
            }

            if (initialPayables==null||initialPayables.equals("$0.00")) {
                txtTitlePayables.setVisibility(View.GONE);
                stakeHolderInitialPayables.setVisibility(View.GONE);
            } else {
                txtTitlePayables.setVisibility(View.VISIBLE);
                stakeHolderInitialPayables.setVisibility(View.VISIBLE);
                stakeHolderInitialPayables.setText(initialPayables);
            }

            if (stakeholderID == null) {
                labelStakeholder.setVisibility(View.GONE);
                txtStakeholder.setVisibility(View.GONE);
            } else {
                labelStakeholder.setVisibility(View.VISIBLE);
                txtStakeholder.setVisibility(View.VISIBLE);
                txtStakeholder.setText(Caching.INSTANCE.getStakeholderName(stakeholderID));
            }
        } else displayStakeHolderDetails(false);
    }

    public void displayStakeHolderDetails(boolean display) {
        if (display) stakeholderDetails.setVisibility(View.VISIBLE);
        else {
            stakeholderDetails.setVisibility(View.GONE);
            txtMonthlySummary.setVisibility(View.GONE);
        }
    }

    public void setHeaderText(String title) {
        toolbar.setTitle(title);
    }
    public TabLayout getTabLayout() {
        return tabLayout;
    }
}