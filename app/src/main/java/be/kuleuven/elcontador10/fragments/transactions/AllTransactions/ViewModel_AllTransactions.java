package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public class ViewModel_AllTransactions extends ViewModel {
    private static final String TAG = "All Transactions VM";
    //ChosenTypesOfTransactions
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    private final MutableLiveData<HashMap<String,Boolean>> chosenTypesOfTransactions = new MutableLiveData<>();
    public LiveData<HashMap<String,Boolean>> getChosenTypesOfTransactions() {
        return chosenTypesOfTransactions;
    }
    public void setTypesOfTransactions(HashMap<String,Boolean> selectedTypes){
        chosenTypesOfTransactions.setValue(selectedTypes);
    }

    private final MutableLiveData<List<ProcessedTransaction>> monthlyListOfTransactions = new MutableLiveData<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectMonthlyList(int month, int year) throws ParseException {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateEnd = dateFormat.parse("01/"+(month+1)+"/"+year);
            assert dateEnd != null;
            Timestamp dateSelectedTop = new Timestamp(dateEnd);

            Date dateBottom = dateFormat.parse("01/"+month+"/"+year);
            assert dateBottom != null;
            Timestamp dateSelectedBottom = new Timestamp(dateBottom);

            String urlGetAccountTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
            Query transactionsFromOneAccount = db.
                    collection(urlGetAccountTransactions).
                    whereGreaterThanOrEqualTo("date", dateSelectedBottom).
                    whereLessThan("date", dateSelectedTop).
                    orderBy("date", Query.Direction.DESCENDING);
            transactionsFromOneAccount.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<ProcessedTransaction> listTrans = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    ProcessedTransaction myTransaction =  doc.toObject(ProcessedTransaction.class);
                    myTransaction.setId( doc.getId());
                    listTrans.add(myTransaction);
                }
                monthlyListOfTransactions.setValue(listTrans);
                setListOfTransactions();

            });
    }

    private final MutableLiveData<List<ScheduledTransaction>> monthlyListOfScheduleTransactions = new MutableLiveData<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectScheduleTransactions(int month, int year) throws ParseException {

        db.collectionGroup("subcontracts").addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            List<ScheduledTransaction> listTransSchedule = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                ScheduledTransaction myTransaction =  doc.toObject(ScheduledTransaction.class);
                myTransaction.setId(doc.getId());
                listTransSchedule.add(myTransaction);
            }
            monthlyListOfScheduleTransactions.setValue(listTransSchedule);
            setListOfTransactions();
        });


    }

    private final MutableLiveData<List<TransactionInterface>> allChosenTransactions = new MutableLiveData<>();
    public LiveData<List<TransactionInterface>> getAllChosenTransactions() {
        return allChosenTransactions;
    }
    public void setListOfTransactions(){
       List<TransactionInterface> listAllTransactions = new ArrayList<>();
        /*if(getChosenTypesOfTransactions().getValue().get("transaction"))
        {listAllTransactions.addAll(monthlyListOfTransactions.getValue());
        }*/
        if(getChosenTypesOfTransactions().getValue().get("receivable"))
        {listAllTransactions.addAll(monthlyListOfScheduleTransactions.getValue());
        }
        else{
            listAllTransactions.clear();
        }
        allChosenTransactions.setValue(listAllTransactions);
    }




    public void resetAll(){
        chosenTypesOfTransactions.setValue(null);
    }
}
