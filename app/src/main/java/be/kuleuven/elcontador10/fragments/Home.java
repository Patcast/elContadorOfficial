package be.kuleuven.elcontador10.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalTime;
import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.RecyclerViewAdapter;
import be.kuleuven.elcontador10.background.database.HomepageManager;
import be.kuleuven.elcontador10.background.interfaces.HomepageInterface;


public class Home extends Fragment implements HomepageInterface {
    private String firstName;
    private TextView header;
    private RecyclerView recyclerView;
    private MainActivity mainActivity;
    private HomepageManager manager;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mainActivity = (MainActivity) getActivity(); // get parent activity
        mainActivity.setTitle(getString(R.string.home));

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       ///// Set Navigation for new Transaction button
        navController = Navigation.findNavController(view);
        FloatingActionButton fabAdd = view.findViewById(R.id.btn_add_Home);
        fabAdd.setOnClickListener(v -> navController.navigate(R.id.action_home_summary_to_newTransaction));
        FloatingActionButton fabSettings = view.findViewById(R.id.btn_settings_Home);
        fabSettings.setOnClickListener(v -> navController.navigate(R.id.action_home_summary_to_settings));
        ///// End

        header = getView().findViewById(R.id.lblHomeGreeting);


        recyclerView = getView().findViewById(R.id.HomeRecycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    fabAdd.setVisibility(View.GONE);
                    fabSettings.setVisibility(View.GONE);
                }
                else {
                    fabAdd.setVisibility(View.VISIBLE);
                    fabSettings.setVisibility(View.VISIBLE);
                }
            }
        });

        manager = HomepageManager.getInstance();
        manager.getRecentTransactions(this);
        manager.getBudget(this);
    }



    // return parent activity
    @Override
    public Context getContext() { return mainActivity; }

    @Override
    public void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(title, description, status, metadata, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayTransaction(String id) {
        HomeDirections.ActionHomeSummaryToTransactionDisplay action =
                HomeDirections.actionHomeSummaryToTransactionDisplay(id);
        navController.navigate(action);
    }

    @Override
    public void displayStakeholder(String id) {
        HomeDirections.ActionHomeSummaryToStakeholderDisply action =
                HomeDirections.actionHomeSummaryToStakeholderDisply(id);
        navController.navigate(action);
    }

    @Override
    public void displayBudget(double budget) {
        TextView lblBudget = requireView().findViewById(R.id.lblBudget);

        String budgetText = getString(R.string.budget, budget);
        lblBudget.setText(budgetText);
    }
}