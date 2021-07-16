package be.kuleuven.elcontador10.background.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> arrayOfFragments = new ArrayList<>();

    public ViewPagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return arrayOfFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayOfFragments.size();
    }
    public void addFragment (Fragment fragment){
        arrayOfFragments.add(fragment);
    }
}
