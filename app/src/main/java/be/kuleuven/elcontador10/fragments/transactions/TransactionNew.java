package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.interfaces.TransactionsNewInterface;


public class TransactionNew extends Fragment implements TransactionsNewInterface {

    private static final String[] stakeHolders = new String[]{"Carlos","Mauricio","Tomas","Juan","Patricio","Alexandria","Yonathan"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_new, container, false);
    }
}