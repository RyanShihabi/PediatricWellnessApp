package com.ryanshihabi.choc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ryanshihabi.choc.databinding.ActivitySignInBinding;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 120;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        ActivitySignInBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){
            Log.d("SignInActivity", "There is currently no current user");
        }

        binding.signIn.setOnClickListener(view -> SignIn());
    }

    private void SignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Reauthenticate user to bypass token expiration

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if(task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseWithGoogle(account.getIdToken());
                    Log.d("SignInActivity", "Grabbing token");
                } catch (ApiException e) {
                    Log.d("SignInActivity", "onActivityResult: " + e);
                }
            }else{
                assert exception != null;
                Log.w("SignInActivity", exception.toString());
            }
        }
    }

    private void firebaseWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;
                        DocumentReference docRef = db.collection("users").document(user.getUid());

                        docRef.get().addOnCompleteListener(task1 -> {
                            DocumentSnapshot doc = task1.getResult();
                            if(doc.getString("treatment") == null || Objects.equals(doc.getString("treatment"), "")){
                                Log.d("SignInActivity", "onComplete: user is not fully registered");

                                Log.d("SignInActivity", "onComplete: Added UID and name to db");

                                Intent intent = new Intent(SignInActivity.this, ThemeActivity.class);
                                startActivity(intent);
                            }else{
                                Log.d("SignInActivity", "onComplete: User is fully registered");

                                Intent intent = new Intent(SignInActivity.this, BoardActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        });
                        // Sign in success
                        Log.d("SignInActivity", "signInWithCredential:success");
                    } else {
                        // Sign in fails
                        Log.w("SignInActivity", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
