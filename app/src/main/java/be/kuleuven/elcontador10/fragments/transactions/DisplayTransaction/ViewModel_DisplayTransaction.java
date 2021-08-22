package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ViewModel_DisplayTransaction extends ViewModel {
    //ChosenUri
    private final MutableLiveData<Bitmap> chosenBitmap = new MutableLiveData<>();
    public LiveData<Bitmap> getChosenBitMap() {
        return chosenBitmap;
    }
    public void selectBitMap(Bitmap bitmap){
        chosenBitmap.setValue(bitmap);
    }
    public void reset(){
        chosenBitmap.setValue(null);
    }



}