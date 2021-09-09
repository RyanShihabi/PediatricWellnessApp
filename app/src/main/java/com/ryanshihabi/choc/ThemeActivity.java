package com.ryanshihabi.choc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ryanshihabi.choc.databinding.ActivityThemeBinding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ThemeActivity extends AppCompatActivity {
    private ActivityThemeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_theme);
        Bundle extra = getIntent().getExtras();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        String UID = user.getUid();
        String name = user.getDisplayName();

        Log.d("ThemeActivity", String.format("onCreate: Choosing a theme for user %s", UID));

        CollectionReference usersRef = db.collection("users");

        binding.submitThemeButton.setOnClickListener(view -> {
            int selected = binding.themeSelection.getCheckedRadioButtonId();

            RadioButton themeButton = findViewById(selected);

            Intent intent = new Intent(ThemeActivity.this, BoardActivity.class);

            Map<String, Object> data = new HashMap<>();

            data.put("name", name);
            data.put("treatment", "BMT");
            data.put("progress", 1);
            data.put("theme", themeButton.getText());

            usersRef.document(UID).set(data);

            Log.d("ThemeSelection", "selected: " + themeButton.getText());

            startActivity(intent);
            finish();
        });
    }
}
