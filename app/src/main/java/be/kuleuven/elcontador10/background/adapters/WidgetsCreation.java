package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;



public enum WidgetsCreation implements CachingObserver {
    INSTANCE;
    List<TransactionType> transTypes = new ArrayList<>();
    List<StakeHolder> stakeHolds = new ArrayList<>();

    WidgetsCreation(){
        Caching.INSTANCE.attachCaching(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeSpinnerCat(Context useContext, Spinner useSpin,boolean filter){
        List<String> categories = new ArrayList<>();
        if(filter) categories.add("All");
        categories.addAll(transTypes.stream()
                .map(TransactionType::getCategory)
                .distinct()
                .collect(Collectors.toList()));
        ArrayAdapter adapterSpinnerCat = new ArrayAdapter<>(useContext,android.R.layout.simple_dropdown_item_1line,categories);
        useSpin.setAdapter(adapterSpinnerCat);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeSpinnerSubCat(Context useContext, Spinner useSpin, String chosenCat,boolean filter){
        List<String> subCategories = new ArrayList<>();
        if(filter) subCategories.add("All");
        subCategories.addAll( transTypes.stream()
                .filter(cat->cat.getCategory().equals(chosenCat))
                .map(TransactionType::getSubCategory)
                .distinct().collect(Collectors.toList()));
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(useContext,android.R.layout.simple_dropdown_item_1line,subCategories );
        useSpin.setAdapter(adapterSpinner);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeAutoStake(Context useContext, AutoCompleteTextView autoText, boolean filter){
        //Implements auto-fill stakeholder *********
      List <String> autoStake = new ArrayList<>();
      if(filter) autoStake.add("All");
          autoStake.addAll( stakeHolds.stream()
                                      .map(StakeHolder::getFullNameId)
                                      .distinct()
                                      .collect(Collectors.toList()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(useContext,android.R.layout.simple_list_item_1,autoStake);
        autoText.setAdapter(adapter);
    }




    @Override
    public void notifyRoles(List<String> roles) {

    }

    @Override
    public void notifyCategories(List<TransactionType> transTypes) {
        this.transTypes.clear();
        this.transTypes.addAll(transTypes);
    }
    @Override
    public void notifyStakeHolders(List<StakeHolder> stakeHolders) {
        stakeHolds.clear();
        stakeHolds.addAll(stakeHolders);
    }
}
