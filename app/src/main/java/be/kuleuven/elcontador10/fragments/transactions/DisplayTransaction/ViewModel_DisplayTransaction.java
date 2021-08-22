package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ViewModel_DisplayTransaction extends ViewModel {
    //ChosenUri
    private final MutableLiveData<Uri> chosenUri = new MutableLiveData<>();
    public LiveData<Uri> getChosenUri() {
        return chosenUri;
    }
    public void selectUri(Uri uri){
        chosenUri.setValue(uri);
    }
    public void reset(){
        chosenUri.setValue(null);
    }



}