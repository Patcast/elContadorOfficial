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

import be.kuleuven.elcontador10.background.model.Property;


public class PropertyListViewModel extends ViewModel {
    private static final String TAG = "Properties List VM";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<Property>> listOfProperties = new MutableLiveData<>();
    public LiveData<List<Property>> getListOfProperties() {
        return listOfProperties;
    }
    public void selectSProperties(List<Property> input){
        listOfProperties.setValue(input);
    }
    public void reset(){
        listOfProperties.setValue(null);
    }

    public PropertyListViewModel() {
        requestListOfProperties();
    }

    public void requestListOfProperties() {

        String urlGetAccountTransactions = "/accounts/"+ Caching.INSTANCE.getChosenAccountId()+"/properties";
        Query propertiesFromOneAccount = db.collection(urlGetAccountTransactions).orderBy("name", Query.Direction.ASCENDING);

        propertiesFromOneAccount.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            List<Property> newListProperties = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                Property myProperty =  doc.toObject(Property.class);
                myProperty.setId(doc.getId());
                newListProperties.add(myProperty);
            }
            selectSProperties(newListProperties);
        });
    }


}
