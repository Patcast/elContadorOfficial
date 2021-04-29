package be.kuleuven.elcontador10.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;
import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.RecyclerViewAdapter;
import be.kuleuven.elcontador10.background.database.HomepageManager;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.interfaces.HomepageInterface;

public class Home extends Fragment implements HomepageInterface {
    private String firstName;
    private TextView header;

    private RecyclerView recyclerView;

    private MainActivity mainActivity;
    private HomepageManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mainActivity = (MainActivity) getActivity(); // get parent activity
        firstName = mainActivity.getLoggedIn().getFirstName();

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        header = getView().findViewById(R.id.lblHomeGreeting);

        //find time of the day for greeting
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        String time_greeting;

        if (hour < 6) time_greeting = "Hello";
        else if (hour < 12) time_greeting = "Good morning";
        else if (hour < 18) time_greeting = "Good afternoon";
        else time_greeting = "Good evening";

        String greeting = getString(R.string.homepage_title, time_greeting, firstName);
        header.setText(greeting);

        recyclerView = getView().findViewById(R.id.HomeRecycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) mainActivity.hideButtons();
                else mainActivity.homeButtons();
            }
        });

        manager = HomepageManager.getInstance();
        manager.getRecentTransactions(this);
    }

    // return parent activity
    @Override
    public Context getContext() { return mainActivity; }

    @Override
    public void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(title, description, status, metadata, this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
    }
}