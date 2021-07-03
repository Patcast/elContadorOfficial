package be.kuleuven.elcontador10.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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


public class LogIn extends AppCompatActivity {

    //private Button logOutButton;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 9001;
    private final String TAG = "SignInActivity";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_log_in);
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.server_client_id)).
                requestEmail().
                build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        ///Buttons
        signInButton = findViewById(R.id.btn_sign_in_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(v -> signIn());
        logOutButton = findViewById(R.id.btn_sign_out);
        logOutButton.setOnClickListener(v -> signOut());
        logOutButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            signInButton.setVisibility(View.INVISIBLE);
            updateAfterSignedIn(currentUser);/// maybe we can pass the fire base user
        }
    }


    ///// Sign in process///////////////
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkIfUserRegistered(user, MainActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void updateAfterSignedIn(FirebaseUser account) {
        if (account != null) {
            signInButton.setVisibility(View.INVISIBLE);
            StringBuilder logInMessage = new StringBuilder("You are logged in with : ");
            logInMessage.append(account.getDisplayName());
            statusText.setText(logInMessage);
            statusText.setTextSize(10);
            logOutButton.setVisibility(View.VISIBLE);

        }
    }

    ///// Sign out process///////////////
    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> updateAfterLogOut());
    }

    private void updateAfterLogOut() {
        FirebaseAuth.getInstance().signOut();
        signInButton.setVisibility(View.VISIBLE);
        statusText.setText("Welcome to Lalaland");
        statusText.setTextSize(40);
        logOutButton.setVisibility(View.INVISIBLE);
    }

    ///// validate for registration and authorization ///////////////
    private void regInFireStore(FirebaseUser currentUser) {
        if (currentUser != null) {
            DocumentReference docRef = db.collection("stakeholders").document(currentUser.getUid());
            Map<String, Object> stakeholder = new HashMap<>();
            if (currentUser.getDisplayName() != (null))
                stakeholder.put("name", currentUser.getDisplayName());
            if (currentUser.getPhoneNumber() != (null))
                stakeholder.put("phone", currentUser.getPhoneNumber());
            stakeholder.put("email", currentUser.getEmail());
            stakeholder.put("authorized", false);


            db.collection("stakeholders").document(currentUser.getUid())
                    .set(stakeholder)
                    .addOnSuccessListener(success -> Toast.makeText(this, getText(R.string.succesful_registration), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(noSuccess -> Toast.makeText(this, getText(R.string.unsuccesful_registration), Toast.LENGTH_SHORT).show());
        }
    }

    private void checkIfUserRegistered(FirebaseUser currentUser, Context currentContext) {
        DocumentReference docRef = db.collection("stakeholders").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("authorized") != null && (boolean) document.get("authorized")) {
                            updateAfterSignedIn(currentUser);
                        } else {
                            signOut();
                            Toast.makeText(currentContext, getText(R.string.Access_denied) + " " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        regInFireStore(currentUser);
                        //signOut();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    //signOut();
                }
            }
        });
    }
}
