package com.hakolab.posyandu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Antrian> dataList;

    private FloatingActionButton add_antrian_button;

    private ProgressDialog progressDialog;

    private String nik;

    private Timer timer;

    ImageView emptyImageView;
    TextView no_data;

    private boolean userScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        nik = SharedPrefManager.getInstance(this).getNik();

        progressDialog = new ProgressDialog(this);
        emptyImageView = findViewById(R.id.emptyImageView);
        no_data = findViewById(R.id.no_data);
        add_antrian_button = findViewById(R.id.add_antrian_button);
        add_antrian_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAntrian();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();

        fetchData();


        // Schedule the timer task to fetch data periodically
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the user has scrolled down
                if (!userScrolled) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fetchData();
                        }
                    });
                }
            }
        }, 0, 2000); // Fetch data every 2 seconds

        // Get reference to your RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Add a scroll listener to detect user scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Check if the user has manually scrolled to a position other than the top
                userScrolled = recyclerView.computeVerticalScrollOffset() > 0;
            }
        });
    }

    private void addAntrian() {
        progressDialog.setMessage("Buat Antrian");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // kode 0 nik sudah ada antrian pending
                            // kode 1 berhasil
                            // kode 2 gagal
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nik", nik);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void fetchData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GET_ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);

                            // Clear the existing data list before adding new items
                            dataList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject antrian = array.getJSONObject(i);
                                dataList.add(new Antrian(
                                        antrian.getString("no_antrian"),
                                        antrian.getString("nama_lengkap"),
                                        antrian.getString("status_antrian"),
                                        antrian.getString("nik")
                                ));
                            }

                            DataAdapter adapter = new DataAdapter(MainActivity.this, dataList);
                            recyclerView.setAdapter(adapter);

                            // Check if dataList is empty
                            if (dataList.isEmpty()) {
                                emptyImageView.setVisibility(View.VISIBLE);
                                no_data.setVisibility(View.VISIBLE);
                            } else {
                                emptyImageView.setVisibility(View.GONE);
                                no_data.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the timer task to stop fetching data
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
