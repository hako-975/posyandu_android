package com.hakolab.posyandu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewNik, textViewNamaLengkap, textViewRole, textViewTitleRole, textViewHapusSemuaAntrian;

    private Button hapus_semua_antrian;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        progressDialog = new ProgressDialog(this);
        textViewNik = findViewById(R.id.textViewNik);
        textViewNamaLengkap = findViewById(R.id.textViewNamaLengkap);
        textViewRole = findViewById(R.id.textViewRole);
        textViewTitleRole = findViewById(R.id.textViewTitleRole);
        textViewHapusSemuaAntrian = findViewById(R.id.hapusSemuaAntrianTextView);
        hapus_semua_antrian = findViewById(R.id.hapus_semua_antrian_button);

        hapus_semua_antrian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Anda ingin menghapus semua Antrian?");
                builder.setPositiveButton("Hapus Semua", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hapusSemuaAntrian();
                    }
                });
                builder.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform action when "No" button is clicked
                        // You can add your code here
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        textViewNik.setText(SharedPrefManager.getInstance(this).getNik());
        textViewNamaLengkap.setText(SharedPrefManager.getInstance(this).getNamaLengkap());
        textViewRole.setText(SharedPrefManager.getInstance(this).getRole());
        if (!SharedPrefManager.getInstance(this).getRole().equals("Administrator")) {
            textViewRole.setVisibility(View.GONE);
            textViewTitleRole.setVisibility(View.GONE);
            hapus_semua_antrian.setVisibility(View.GONE);
            textViewHapusSemuaAntrian.setVisibility(View.GONE);
        }
    }

    private void hapusSemuaAntrian() {
        progressDialog.setMessage("Buat Antrian");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DELETE_ALL_ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
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