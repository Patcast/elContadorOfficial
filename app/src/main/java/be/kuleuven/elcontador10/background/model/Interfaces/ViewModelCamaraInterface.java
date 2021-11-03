package be.kuleuven.elcontador10.background.model.Interfaces;

import androidx.lifecycle.LiveData;

import be.kuleuven.elcontador10.background.model.ImageFireBase;

public interface ViewModelCamaraInterface {
    void selectImage(ImageFireBase imageInput);
    LiveData<ImageFireBase> getChosenImage();
    void resetImage();
}
