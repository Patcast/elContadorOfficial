package be.kuleuven.elcontador10.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.LogIn;
import be.kuleuven.elcontador10.activities.MainActivity;

public class Settings extends Fragment {
    private MainActivity mainActivity;
    private Button logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        return  inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.hideButtons();
        logout = getView().findViewById(R.id.btnLogOut);
        logout.setOnClickListener(this::onLogOut_CLicked);
    }

    public void onLogOut_CLicked(View view) {
        Toast.makeText(mainActivity, "Logging out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(mainActivity, LogIn.class));
        mainActivity.finish();
    }
}