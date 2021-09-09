package com.ryanshihabi.choc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ryanshihabi.choc.databinding.ActivityBoardBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardActivity extends AppCompatActivity {
    private List<cardItem> cards;
    private String treatment;
    private RecyclerView recycler;
    public String theme;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityBoardBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_board);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        String UID = user.getUid();

        DocumentReference docRef = db.collection("users").document(UID);

        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    String name = (String) document.get("name");
                    theme = (String) document.get("theme");
                    treatment = (String) document.get("treatment");

                    recycler = binding.recyView;
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    recycler.setLayoutManager(layoutManager);

                    initializeCards(treatment);

                    cardAdapter adapter = new cardAdapter(cards);
                    recycler.setAdapter(adapter);

                    Log.d("BoardActivity", "Getting theme: " + theme);
                    assert name != null;
                    if(name.length() < 8){
                        binding.textViewBoard.setText(name);
                        binding.textViewBoard.append("'s Adventure");
                    }else{
                        binding.textViewBoard.setText(R.string.adventureTitle);
                    }
                    Log.d("BoardActivity", name);
                    Log.d("BoardActivity", String.format("%s", document.get("progress")));
                    Log.d("BoardActivity", (String)document.get("theme"));
                    Log.d("BoardActivity", UID);
                }else{
                    Log.d("BoardActivity", "onCreate: Document not found");
                }
            }else{
                Log.d("BoardActivity", Objects.requireNonNull(task.getException()).toString());
            }
        });

        binding.settingsButton.setOnClickListener(view -> {
            PopupMenu dropDown = new PopupMenu(BoardActivity.this, view);
            MenuInflater inflater = dropDown.getMenuInflater();
            dropDown.setOnMenuItemClickListener(item -> {
                switch(item.getItemId()){
                    case R.id.changeTheme:
                        Intent changeThemeIntent = new Intent(BoardActivity.this, ThemeActivity.class);
                        startActivity(changeThemeIntent);
                        finish();
                        return true;
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        Intent signOutIntent = new Intent(BoardActivity.this, SignInActivity.class);
                        startActivity(signOutIntent);
                        finish();
                        return true;
                    default:
                        return false;
                }
            });
            inflater.inflate(R.menu.menu, dropDown.getMenu());
            dropDown.show();
        });
    }

    //Retrieve videos from firebase cloud
    //store in same collection
    //iterate through videos in collection and attach to a card
    //cards are now dynamic

    public void initializeCards(String treatment){
        cards = new ArrayList<>();
        switch(treatment){
            case "BMT":
                Log.d("BoardActivity", "initializeCards: BMT");
                cards.add(new cardItem("Title 1", R.drawable.jungle_icon));
                cards.add(new cardItem("Title 2", R.drawable.jungle_icon));
                cards.add(new cardItem("Title 3", R.drawable.jungle_icon));
                cards.add(new cardItem("Title 4", R.drawable.jungle_icon));
                cards.add(new cardItem("Title 5", R.drawable.jungle_icon));
                cards.add(new cardItem("Title 6", R.drawable.jungle_icon));
                break;
            case "Other":
                Log.d("BoardActivity", "initializeCards: Other");
                cards.add(new cardItem("Title 1", R.drawable.dinosaur));
                cards.add(new cardItem("Title 2", R.drawable.dinosaur));
                cards.add(new cardItem("Title 3", R.drawable.dinosaur));
                cards.add(new cardItem("Title 4", R.drawable.dinosaur));
                cards.add(new cardItem("Title 5", R.drawable.dinosaur));
                break;
            default:
                Log.d("BoardActivity", "initializeCards: No treatment detected");
                cards.add(new cardItem("Title 1", R.drawable.dinosaur));
                cards.add(new cardItem("Title 2", R.drawable.dinosaur));
                cards.add(new cardItem("Title 3", R.drawable.dinosaur));
                cards.add(new cardItem("Title 4", R.drawable.dinosaur));
                cards.add(new cardItem("Title 5", R.drawable.dinosaur));
                break;
        }
    }

    public void initializeTheme(String theme){
        switch(theme){
            case "Jungle":
                Log.d("BoardActivity", "initializeTheme: jungle");
//                setTheme();
                break;
            case "Dinosaur":
                Log.d("BoardActivity", "initializeTheme: dinosaur");
//                setTheme();
                break;
            default:
                Log.d("BoardActivity", "initializeTheme: No theme detected");
                break;
        }
    }

}
