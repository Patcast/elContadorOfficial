package be.kuleuven.elcontador10.fragments.transactions.Categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import be.kuleuven.elcontador10.background.model.EmojiCategory;

public class ViewModelCategory extends ViewModel {

    public ViewModelCategory() {
    }

    private final MutableLiveData<List<EmojiCategory>> listOfCategories = new MutableLiveData<>();
    public LiveData<List<EmojiCategory>> getListOfCategories() {
        return listOfCategories;
    }
    public void selectCategories(List<EmojiCategory> input){
        listOfCategories.setValue(input);
    }
    public void reset(){
        listOfCategories.setValue(null);
    }
    private void requestCategories(){

    }
}
