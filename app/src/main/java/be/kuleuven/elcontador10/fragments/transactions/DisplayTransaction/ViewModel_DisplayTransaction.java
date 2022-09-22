package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.Interfaces.ViewModelCamaraInterface;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;

public class ViewModel_DisplayTransaction extends ViewModel implements ViewModelCamaraInterface {
    FirebaseStorage storage = FirebaseStorage.getInstance();


    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            chosenBitmap.setValue(bitmap);
            isLoading.setValue(false);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }

    };


    public ViewModel_DisplayTransaction() {
        setIsLoading(false);
    }
    // Loading Bar
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public void setIsLoading(boolean value){
        isLoading.setValue(value);
    }
    // transaction used

    private final MutableLiveData<ProcessedTransaction> transaction = new MutableLiveData<>();
    private LiveData<ProcessedTransaction> getTransaction() {
        return transaction;
    }
    public void setTransaction(ProcessedTransaction trans){
        transaction.setValue(trans);
    }
    public void deletePicture(Context context){
        ProcessedTransaction transPic = getTransaction().getValue();
        if(transPic!=null){
            transPic.deleteImageFromFireBase(true,context);
            transaction.setValue(transPic);
            reset();
        }
    }

    //Chosen Bitmap
    private final MutableLiveData<Bitmap> chosenBitmap = new MutableLiveData<>();
    public LiveData<Bitmap> getChosenBitMap() {
        return chosenBitmap;
    }
    public void selectBitMap(String imageName, Context context) {
            isLoading.setValue(true);
            StringBuilder downloadUrl = new StringBuilder();
            downloadUrl.append(Caching.INSTANCE.getChosenAccountId());
            downloadUrl.append("/");
            downloadUrl.append(imageName);
            storage.getReference()
                    .child(downloadUrl.toString())
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(target))
                    .addOnFailureListener(exception -> Toast.makeText(context, "Error loading photo.", Toast.LENGTH_SHORT).show());
    }

    //ChosenImage
    private final MutableLiveData<ImageFireBase> chosenImage = new MutableLiveData<>();
    @Override
    public LiveData<ImageFireBase> getChosenImage() {
        return chosenImage;
    }
    @Override
    public void selectImage(ImageFireBase imageInput){
        chosenImage.setValue(imageInput);
    }
    @Override
    public void resetImage(){ chosenImage.setValue(null); }


    public void reset(){
        chosenBitmap.setValue(null);
        isLoading.setValue(false);
        resetImage();
        Picasso.get().cancelRequest(target);
    }

}