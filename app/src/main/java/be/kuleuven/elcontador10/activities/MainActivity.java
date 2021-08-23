package be.kuleuven.elcontador10.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;

public class MainActivity extends AppCompatActivity {


    public interface TopMenuHandler {
        void onBottomSheetClick();
        void onDeleteClick();
        void onEditingClick();
        void onAddClick();
        void onSearchClick(SearchView searchView);
        void onFilterClick();
    }
    SearchView searchView;
    private Menu topRightMenu;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private TextView stakeHolderBalance, stakeholderRole;
    private ConstraintLayout stakeholderDetails;
    SharedPreferences.Editor editor;
    private static final String SAVED_EMAIL_KEY = "email_key";
    TopMenuHandler currentTopMenuHandler;


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
        stakeHolderBalance = findViewById(R.id.txtViewBalance);
        stakeholderRole = findViewById(R.id.txtViewRole);

        Caching.INSTANCE.setContext(this);
        setSupportActionBar(toolbar);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    public void setCurrentMenuClicker(TopMenuHandler currentTopMenuHandler) {
        this.currentTopMenuHandler = currentTopMenuHandler;
    }
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

    public void displayStakeHolderDetails(boolean display, String balance, String role) {
        if (display) {
            stakeholderDetails.setVisibility(View.VISIBLE);
            stakeHolderBalance.setText(balance);
            stakeholderRole.setText(role);
        } else stakeholderDetails.setVisibility(View.GONE);
    }

    public void displayStakeholderDetails(boolean display) {
        if (display) stakeholderDetails.setVisibility(View.VISIBLE);
        else stakeholderDetails.setVisibility(View.GONE);
    }

    public void setHeaderText(String title) {
        toolbar.setTitle(title);
    }
    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        topRightMenu = menu;
        getMenuInflater().inflate(R.menu.top_three_buttons_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView = (SearchView) item.getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_bottom_sheet:
                currentTopMenuHandler.onBottomSheetClick();
                return true;
            case R.id.menu_delete:
                currentTopMenuHandler.onDeleteClick();
                return true;
            case R.id.menu_edit:
                currentTopMenuHandler.onEditingClick();
                return true;
            case R.id.menu_add:
                currentTopMenuHandler.onAddClick();
                return true;
            case R.id.menu_search:
                currentTopMenuHandler.onSearchClick(searchView);
                return true;
            case R.id.menu_filter:
                currentTopMenuHandler.onFilterClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void modifyVisibilityOfMenuItem(int itemId,boolean display){
        topRightMenu.findItem(itemId)
                .setVisible(display);
    }
}