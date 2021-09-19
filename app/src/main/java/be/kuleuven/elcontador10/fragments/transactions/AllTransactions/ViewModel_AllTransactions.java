package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;

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


    private final MutableLiveData<List<Transaction>> monthlyListOfTransactions = new MutableLiveData<>();
    public LiveData<List<Transaction>> getMonthlyListOfTransactions() {
        return monthlyListOfTransactions;
    }
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
                List<Transaction> listTrans = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    Transaction myTransaction =  doc.toObject(Transaction.class);
                    myTransaction.setId( doc.getId());
                    listTrans.add(myTransaction);
                }
                monthlyListOfTransactions.setValue(listTrans);
            });
    }





    public void resetAll(){
        chosenTypesOfTransactions.setValue(null);
    }
}
