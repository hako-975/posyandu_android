package com.example.posyandu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextNik, editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private TextView registerNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        editTextNik = findViewById(R.id.nik);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan tunggu...");

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        registerNow = findViewById(R.id.registerNow);
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void userLogin() {
        final String nik = editTextNik.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                        obj.getString("nik"),
                                        obj.getString("nama_lengkap"),
                                        obj.getString("role")
                                );
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nik", nik);
                params.put("password", password);

                return  params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}