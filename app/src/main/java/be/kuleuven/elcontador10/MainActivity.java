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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private boolean isClicked;

    private FloatingActionButton fabNewTransaction, fabNewFutureTransaction,fabNew;
    private TextView textFabNewTransaction,textFabReceivable;
    private LinearLayout coverLayout;
    private RelativeLayout relativeLayout;

    private Animation rotateOpen,rotateClose,popOpen,popClose;

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

        textFabNewTransaction = findViewById(R.id.text_fabNewTransaction);
        textFabReceivable = findViewById(R.id.text_fabReceivable);
        fabNewTransaction = findViewById(R.id.btn_new_TransactionFAB);
        fabNewFutureTransaction = findViewById(R.id.btn_new_ReceivableOrPayable);
        fabNew = findViewById(R.id.btn_newFAB);
        coverLayout = findViewById(R.id.coverLayout);
        relativeLayout = findViewById(R.id.relativeLayout);

        coverLayout.setOnClickListener(v->closeCover());
        fabNew.setOnClickListener(v->fabOpenAnimation());
        fabNewTransaction.setOnClickListener(v -> fabImplement.onTransactionNewClicked());
        fabNewFutureTransaction.setOnClickListener(v -> fabImplement.onScheduledTransactionNewClicked());

        rotateOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close);
        popOpen= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_up_fabs);
        popClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_down_fabs);
        isClicked= false;
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

    // FAB Button

    private FABImplement fabImplement;

    public interface FABImplement {
        void onTransactionNewClicked();
        void onScheduledTransactionNewClicked();
    }

    public void setFabImplement(FABImplement fabImplement) {
        this.fabImplement = fabImplement;
        relativeLayout.setVisibility(fabImplement == null? View.GONE : View.VISIBLE);
        setVisibility(true);
        isClicked = false;
    }

    public void resetFAB() {
        setVisibility(true);
        setAnimation(true);
        isClicked = false;
    }

    private void fabOpenAnimation() {
        setVisibility(isClicked);
        setAnimation(isClicked);
        isClicked = !isClicked;
    }

    private void setAnimation(boolean addButtonClicked) {
        if(!addButtonClicked){
            coverLayout.setVisibility(View.VISIBLE);
            textFabNewTransaction.startAnimation(popOpen);
            textFabReceivable.startAnimation(popOpen);
            fabNewTransaction.startAnimation(popOpen);
            fabNewFutureTransaction.startAnimation(popOpen);
            fabNew.startAnimation(rotateOpen);
        }
        else{
            coverLayout.setVisibility(View.GONE);
            textFabNewTransaction.startAnimation(popClose);
            textFabReceivable.startAnimation(popClose);
            fabNewTransaction.startAnimation(popClose);
            fabNewFutureTransaction.startAnimation(popClose);
            fabNew.startAnimation(rotateClose);
        }
    }

    public void closeCover() {
        if (isClicked) {
            setAnimation(true);
            setVisibility(true);
            isClicked = false;
        }
    }

    private void setVisibility(boolean addButtonClicked) {
        if(!addButtonClicked){
            textFabReceivable.setVisibility(View.VISIBLE);
            textFabNewTransaction.setVisibility(View.VISIBLE);
            fabNewTransaction.setVisibility(View.VISIBLE);
            fabNewFutureTransaction.setVisibility(View.VISIBLE);
        }
        else{
            textFabNewTransaction.setVisibility(View.GONE);
            textFabReceivable.setVisibility(View.GONE);
            fabNewTransaction.setVisibility(View.GONE);
            fabNewFutureTransaction.setVisibility(View.GONE);
        }
    }

    public void setFABVisibility(boolean visible) {
        fabNew.setVisibility((visible)? View.VISIBLE : View.GONE);
    }
}