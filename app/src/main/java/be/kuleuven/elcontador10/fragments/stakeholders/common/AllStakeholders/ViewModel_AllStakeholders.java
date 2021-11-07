package be.kuleuven.elcontador10.fragments.stakeholders.common.AllStakeholders;




import android.os.Build;


import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;


import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ViewModel_AllStakeholders extends ViewModel {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ViewModel_AllStakeholders() {
        requestGroupOFStakeHolders(Caching.INSTANCE.getChosenAccountId());
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<StakeHolder>> stakeholderList = new MutableLiveData<>();
    public LiveData<List<StakeHolder>> getStakeholdersList() {
        return stakeholderList;
    }
    public void setStakeholdersList(List<StakeHolder> inputStakeholders){
        stakeholderList.setValue(inputStakeholders);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestGroupOFStakeHolders(String chosenAccountId){
        String stakeHoldersUrl = "/accounts/"+chosenAccountId+"/stakeHolders";
        Query getStakeHolders = db.collection(stakeHoldersUrl)
                .orderBy("name", Query.Direction.ASCENDING);

        getStakeHolders.addSnapshotListener((value, e) -> {
                    if (e != null) {
                        return;
                    }
                    List<StakeHolder> list = new ArrayList<>();
                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        StakeHolder myStakeHolder =  doc.toObject(StakeHolder.class);
                        myStakeHolder.setId(doc.getId());
                        list.add(myStakeHolder);
                    }
                    setStakeholdersList(list);
                });
    }


}