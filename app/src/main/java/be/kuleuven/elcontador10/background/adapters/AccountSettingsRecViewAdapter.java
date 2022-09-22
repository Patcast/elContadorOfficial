package be.kuleuven.elcontador10.background.adapters;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.AccountSettingsModel;


public class AccountSettingsRecViewAdapter extends RecyclerView.Adapter<AccountSettingsRecViewAdapter.ViewHolder> {
    private final ArrayList<String> listOfUsers = new ArrayList<>();
    private String owner = "";

    private Context context;
    private final String loggedInUser;
    public AccountSettingsRecViewAdapter(Context context,String loggedInUser) {
        this.context = context;
        this.loggedInUser= loggedInUser;
        sharingStatuses = context.getResources().getStringArray(R.array.sharing_status);
    }
    String[] sharingStatuses;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_accounts_participants,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull AccountSettingsRecViewAdapter.ViewHolder holder, int position) {
        String email = listOfUsers.get(position);
        holder.textEmail.setText(email);
        String partLabel = (email.equals(owner))?sharingStatuses[0]:sharingStatuses[1];
        holder.participantsMenu.setText(partLabel,false);
        holder.participantsMenu.setOnItemClickListener((adapterView, view, position1, id) -> {

            if (email.equals(owner)&&loggedInUser.equals(owner)){
                if(position1 != 0) Toast.makeText(context, context.getString(R.string.toast_for_wrong_input_from_owner) , Toast.LENGTH_LONG).show();
                holder.participantsMenu.setText(sharingStatuses[0] ,false);

            }
            else if(loggedInUser.equals(owner)){
                if(position1 != 1){
                    boolean executed = false;
                    AccountSettingsModel ac = new AccountSettingsModel(email,context);
                    if(position1 == 2){
                        executed = ac.deleteAccountUser();
                    }
                    if(position1 == 0){
                        executed = ac.changeOwner();
                    }
                    if (!executed ) holder.participantsMenu.setText(adapterView.getItemAtPosition(1).toString(),false);//holder.participantsMenu.setText(adapterView.getItemAtPosition(position).toString(),false);
                }
            }
            else {
                int pos  = (email.equals(owner))?0:1;
                holder.participantsMenu.setText(adapterView.getItemAtPosition(pos).toString(),false);
                Toast.makeText(context, context.getString(R.string.no_owner_rights) , Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textEmail;
        private AutoCompleteTextView participantsMenu;
        ArrayAdapter<String> adapterItems ;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmail = itemView.findViewById(R.id.textEmailUser);
            participantsMenu = itemView.findViewById(R.id.autMenuParticipant);
            adapterItems = new ArrayAdapter<>(itemView.getContext(), R.layout.list_item, sharingStatuses);
            participantsMenu.setAdapter(adapterItems );

        }
    }

    public void setUsers(ArrayList<String> usersReceived, String ownerReceived) {
        listOfUsers.clear();
        listOfUsers.addAll(usersReceived);
        owner = ownerReceived;
        notifyDataSetChanged();
    }
}