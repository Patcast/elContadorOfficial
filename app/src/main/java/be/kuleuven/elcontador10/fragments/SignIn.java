package be.kuleuven.elcontador10.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;


public class SignIn extends Fragment {
    private EditText edTextIdCompany;
    private final int RC_SIGN_IN = 9001;
    private final String TAG = "SignInActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NavController navController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Caching.INSTANCE.mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.server_client_id)).
                requestEmail().
                build();


        Caching.INSTANCE.mGoogleSignInClient =  GoogleSignIn.getClient(getContext(), gso);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    /*    MainActivity holderActivity = (MainActivity)getActivity();
        holderActivity.displayBottomMenu(false);*/
        ///Buttons
        edTextIdCompany = view.findViewById(R.id.editTextIdCompany);
        SignInButton signInButton = view.findViewById(R.id.btn_sign_in_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(v->signIn());
        navController = Navigation.findNavController(view);
    }

    ///// Sign in process///////////////
    private void signIn() {
        Intent signInIntent =  Caching.INSTANCE.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
            // Signed in successfully, show authenticated UI.
            // updateAfterSignedIn(account);//maybe I can pass fireBase user

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Caching.INSTANCE.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user =  Caching.INSTANCE.mAuth.getCurrentUser();
                            checkIfUserRegistered(user, getContext());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void updateAfterSignedIn(FirebaseUser account,String userId,String idCompany){
        if (account !=null ){
            Caching.INSTANCE.setGlobalAccountId(idCompany);
            Caching.INSTANCE.setLogInUserId(userId);
            navController.navigate(R.id.action_signIn_to_home_summary);
        }
    }
    ///// Sign out process///////////////
    private void signOut() {
        navController.navigate(R.id.signIn);
        FirebaseAuth.getInstance().signOut();
    }

    ///// validate for registration and authorization ///////////////
    private void regInFireStore(FirebaseUser currentUser,String idCompany){
        if (currentUser !=null ) {
            Map<String, Object> stakeholder = new HashMap<>();
            if (currentUser.getDisplayName() != (null))
                stakeholder.put("name", currentUser.getDisplayName());
            if (currentUser.getPhoneNumber() != (null))
                stakeholder.put("phone", currentUser.getPhoneNumber());
            stakeholder.put("email", currentUser.getEmail());
            stakeholder.put("authorized", false);
            stakeholder.put("idOfGlobalAccount",idCompany);


            db.collection("/globalAccounts/"+idCompany+"/stakeHolders").document(currentUser.getUid())
                    .set(stakeholder)
                    .addOnSuccessListener(success -> Toast.makeText(getContext(), getText(R.string.succesful_registration), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(noSuccess -> Toast.makeText(getContext(), getText(R.string.unsuccesful_registration), Toast.LENGTH_SHORT).show());
        }
    }

    private void checkIfUserRegistered(FirebaseUser currentUser, Context currentContext){
        String idCompany =edTextIdCompany.getText().toString();
        DocumentReference docRef =  db.collection("/globalAccounts/"+idCompany+"/stakeHolders").document(currentUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(document.get("authorized")!=null && (boolean)document.get("authorized")) {
                            updateAfterSignedIn(currentUser,document.getId(),idCompany);
                        }
                        else{
                            signOut();
                            Toast.makeText(currentContext,   getText(R.string.Access_denied)+" "+currentUser.getEmail(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        regInFireStore(currentUser,idCompany);
                        signOut();
                        Toast.makeText(currentContext, getText(R.string.askForPermission), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(currentContext,   getText(R.string.askForValidCompanyId)+" "+currentUser.getEmail(), Toast.LENGTH_LONG).show();
                    signOut();
                }
            }
        });
    }
}