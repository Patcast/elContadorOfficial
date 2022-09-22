package be.kuleuven.elcontador10.fragments.transactions.Categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.EmojiCategory;

public class ViewModelCategory extends ViewModel {
    //ChosenCategory
    private final MutableLiveData<EmojiCategory> chosenCategory = new MutableLiveData<>();
    public LiveData<EmojiCategory> getChosenCategory() {
        return chosenCategory;
    }
    public void selectCategory(EmojiCategory categoryInput){
        chosenCategory.setValue(categoryInput);
    }
    public void resetCategory(){
        chosenCategory.setValue(null);
    }
}
