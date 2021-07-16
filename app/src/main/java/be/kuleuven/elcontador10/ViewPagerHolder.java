package be.kuleuven.elcontador10;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.fragments.Accounts;
import be.kuleuven.elcontador10.fragments.stakeholders.AllMicroAccounts;


public class ViewPagerHolder extends Fragment {

   ViewPagerAdapter mAdapter;
   ViewPager2 viewPager2;
   MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.transactions));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_holder, container, false);
        viewPager2 =view.findViewById(R.id.viewPagerHolder);
        mAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager(),getLifecycle());
        addFragments(view);
        return view;
    }

    private void addFragments(View view) {
        mAdapter.addFragment(new Accounts());
        mAdapter.addFragment(new AllMicroAccounts());
        viewPager2.setAdapter(mAdapter);
        new TabLayoutMediator(mainActivity.getTabLayout(),viewPager2,(t,p)->{
            switch(p){
                case 0: t.setText("Accounts"); break;
                case 1: t.setText("Micro Accounts"); break;

            }
        }).attach();
    }
}