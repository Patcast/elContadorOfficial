package be.kuleuven.elcontador10.background.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.fragments.accounts.ViewModel_AccountSettings;


public class AccountSettingsRecViewAdapter extends RecyclerView.Adapter<AccountSettingsRecViewAdapter.ViewHolder> {
    private ArrayList<String> listOfUsers = new ArrayList<>();
    private String owner = "";



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_accounts_participants,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountSettingsRecViewAdapter.ViewHolder holder, int position) {
        String email = listOfUsers.get(position);
        holder.textEmail.setText(email);
        String btnLabel = (email.equals(owner))?"Owner":"Editor";
        holder.btnType.setText(btnLabel);
    }

    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textEmail;
        private Button   btnType;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmail = itemView.findViewById(R.id.textEmailUser);
            btnType = itemView.findViewById(R.id.buttonType);
        }
    }

    public void setUsers(ArrayList<String> usersReceived, String ownerReceived) {
        listOfUsers.clear();
        listOfUsers.addAll(usersReceived);
        owner = ownerReceived;
        notifyDataSetChanged();
    }
}