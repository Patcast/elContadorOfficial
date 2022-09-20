package be.kuleuven.elcontador10.fragments.property;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.Property;

public class PropertyViewModel extends ViewModel {
    private static final String TAG = "StakeHolder List VM";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Property selectedProperty;

    public void setSelectedProperty(Property property){
        selectedProperty = property;
        requestListForProperty();
    }

    private final MutableLiveData<List<ProcessedTransaction>> listOfPropertiesTrans = new MutableLiveData<>();
    public LiveData<List<ProcessedTransaction>> getListOfPropertiesTrans () {
        return listOfPropertiesTrans ;
    }
    public void selectListOfPropertyTrans(List<ProcessedTransaction> input){
        listOfPropertiesTrans.setValue(input);
    }
    public void reset(){
        listOfPropertiesTrans.setValue(null);
    }

    public void requestListForProperty() {
        String urlGetAccountTransactions = "/accounts/"+ Caching.INSTANCE.getChosenAccountId()+"/transactions";
        Query stakeTransQuery = db.collection(urlGetAccountTransactions).
                whereEqualTo("idOfProperty", selectedProperty.getId()).
                orderBy("dueDate", Query.Direction.DESCENDING);
        stakeTransQuery.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            List<ProcessedTransaction> newListStakeHolderTrans = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                ProcessedTransaction transaction =  doc.toObject(ProcessedTransaction.class);
                newListStakeHolderTrans.add(transaction);
            }
            selectListOfPropertyTrans(newListStakeHolderTrans);
        });
    }

}