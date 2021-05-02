package be.kuleuven.elcontador10.background.adapters;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsFilterInterface;

public class CategorySpinnerAdapter implements AdapterView.OnItemSelectedListener {
    private MainActivity mainActivity;
    private TransactionsFilterInterface filterInterface;

    public CategorySpinnerAdapter(MainActivity mainActivity, TransactionsFilterInterface filterInterface) {
        this.mainActivity = mainActivity;
        this.filterInterface = filterInterface;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // Category
                filterInterface.category_All();
                break;
            case 1: // Rent
                filterInterface.category_Rent();
                break;
            case 2: // Others
                filterInterface.category_Others();
                break;
            case 3:
                filterInterface.category_Salary();
                break;
            case 4:
                filterInterface.category_Toilets();
                break;
            case 5:
                filterInterface.category_Purchases();
                break;
            case 6:
                filterInterface.category_Deposits();
                break;
            default:
                Toast.makeText(mainActivity, "Error on case", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        filterInterface.category_All();
    }
}
