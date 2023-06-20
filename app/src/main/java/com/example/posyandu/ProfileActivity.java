package com.example.posyandu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewNik, textViewNamaLengkap, textViewRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewNik = findViewById(R.id.textViewNik);
        textViewNamaLengkap = findViewById(R.id.textViewNamaLengkap);
        textViewRole = findViewById(R.id.textViewRole);

        textViewNik.setText(SharedPrefManager.getInstance(this).getNik());
        textViewNamaLengkap.setText(SharedPrefManager.getInstance(this).getNamaLengkap());
        textViewRole.setText(SharedPrefManager.getInstance(this).getRole());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuMain) {
            startActivity(new Intent(this, MainActivity.class));
        }

        if (item.getItemId() == R.id.menuSettings) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (item.getItemId() == R.id.menuLogout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        return true;
    }
}