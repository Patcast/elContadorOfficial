package be.kuleuven.elcontador10.fragments.stakeholders;

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
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class StakeholderViewModel extends ViewModel {
    private static final String TAG = "StakeHolder List VM";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StakeHolder selectedStakeHolder;
/*    StakeholderViewModel(StakeHolder selectedStakeHolder){
        this.selectedStakeHolder=selectedStakeHolder;
        selectListOfStakeHolder();
    }*/
    public void setSelectedStakeholder(StakeHolder stake){
        selectedStakeHolder = stake;
        requestListOfStakeHolder();

    }
    private final MutableLiveData<List<ProcessedTransaction>> listOfStakeHolderTrans = new MutableLiveData<>();
    public LiveData<List<ProcessedTransaction>> getListOfStakeHolderTrans () {
        return listOfStakeHolderTrans ;
    }
    public void selectListOfStakeHolderTrans(List<ProcessedTransaction> input){
        listOfStakeHolderTrans.setValue(input);
    }
    public void reset(){
        listOfStakeHolderTrans .setValue(null);
    }

    public void requestListOfStakeHolder() {

        String urlGetAccountTransactions = "/accounts/"+ Caching.INSTANCE.getChosenAccountId()+"/transactions";
        Query stakeTransQuery = db.collection(urlGetAccountTransactions).whereEqualTo("idOfStakeInt",selectedStakeHolder.getId()).orderBy("dueDate", Query.Direction.ASCENDING);

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
            selectListOfStakeHolderTrans(newListStakeHolderTrans);
        });
    }

}
