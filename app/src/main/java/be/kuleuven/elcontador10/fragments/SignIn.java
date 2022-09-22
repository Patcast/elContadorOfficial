package be.kuleuven.elcontador10.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
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
import be.kuleuven.elcontador10.MainActivity;


public class SignIn extends Fragment {
    private final int RC_SIGN_IN = 9000;
    private final String TAG = "SignInActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NavController navController;
    MainActivity mainActivity;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         mainActivity = (MainActivity) requireActivity();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mainActivity.displayToolBar(false));


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.server_client_id)).
                requestEmail().
                build();
        mGoogleSignInClient= GoogleSignIn.getClient(getContext(), gso);
        SignInButton signInButton = view.findViewById(R.id.btn_sign_in_google);
        signInButton.setOnClickListener(v->signIn());
        navController = Navigation.findNavController(view);
    }

    ///// Sign in process///////////////
    private void signIn() {
        Intent signInIntent =  mGoogleSignInClient.getSignInIntent();
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
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkIfUserRegistered(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }



    ///// validate for registration and authorization ///////////////
    private void regInFireStore(FirebaseUser currentUser){
        if (currentUser !=null ) {
            Map<String, Object> stakeholder = new HashMap<>();
            if (currentUser.getDisplayName() != (null))
                stakeholder.put("name", currentUser.getDisplayName());
            if (currentUser.getPhoneNumber() != (null))
                stakeholder.put("phone", currentUser.getPhoneNumber());
            stakeholder.put("email", currentUser.getEmail());


            db.collection("users").document(currentUser.getEmail())
                    .set(stakeholder)
                    .addOnSuccessListener(success -> Toast.makeText(getContext(), getText(R.string.succesful_registration), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(noSuccess -> Toast.makeText(getContext(), getText(R.string.unsuccesful_registration), Toast.LENGTH_SHORT).show());
        }
    }

    private void checkIfUserRegistered(FirebaseUser currentUser) {
        DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //updateAfterSignedIn(document.toObject(User.class));
                        updateAfterSignedIn(currentUser.getEmail());
                    } else {
                        regInFireStore(currentUser);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateAfterSignedIn(String email){
        mainActivity.saveLoggedInState(email);
        mGoogleSignInClient.signOut();
        navController.popBackStack();
    }


}