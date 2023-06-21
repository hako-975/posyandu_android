package com.hakolab.posyandu;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private Context context;
    private List<Antrian> dataList;
    private ProgressDialog progressDialog;

    public DataAdapter(Context context, List<Antrian> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Antrian item = dataList.get(position);

        holder.noAntrianTextView.setText("No. Antrian: " + item.getNoAntrian());
        holder.namaLengkapTextView.setText(item.getNamaLengkap());
        holder.statusAntrianTextView.setText(item.getStatusAntrian());

        String status = item.getStatusAntrian();
        int textColor;
        if (status.equals("Dibatalkan")) {
            textColor = ContextCompat.getColor(context, R.color.red);
        } else if (status.equals("Pending")) {
            textColor = ContextCompat.getColor(context, R.color.yellow);
        } else if (status.equals("Selesai")) {
            textColor = ContextCompat.getColor(context, R.color.blue);
        } else {
            textColor = ContextCompat.getColor(context, R.color.black);
        }

        holder.statusAntrianTextView.setTextColor(textColor);

        if (SharedPrefManager.getInstance(context).getRole().equals("Administrator")) {
            if (item.getStatusAntrian().equals("Dibatalkan")) {
                holder.batalkan_button.setVisibility(View.GONE);
                holder.selesaikan_button.setVisibility(View.VISIBLE);
                holder.pendingkan_button.setVisibility(View.VISIBLE);
            }

            if (item.getStatusAntrian().equals("Selesai")) {
                holder.selesaikan_button.setVisibility(View.GONE);
                holder.batalkan_button.setVisibility(View.VISIBLE);
                holder.pendingkan_button.setVisibility(View.VISIBLE);
            }

            if (item.getStatusAntrian().equals("Pending")) {
                holder.pendingkan_button.setVisibility(View.GONE);
                holder.batalkan_button.setVisibility(View.VISIBLE);
                holder.selesaikan_button.setVisibility(View.VISIBLE);
            }
        } else {
            holder.batalkan_button.setVisibility(View.GONE);
            holder.selesaikan_button.setVisibility(View.GONE);
            holder.pendingkan_button.setVisibility(View.GONE);
            if (item.getStatusAntrian().equals("Pending")) {
                if (SharedPrefManager.getInstance(context).getNik().equals(item.getNik())) {
                    holder.batalkan_button.setVisibility(View.VISIBLE);
                }
            } else {
                holder.batalkan_button.setVisibility(View.GONE);
            }
        }


        holder.pendingkan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Anda ingin Pendingkan Antrian?");
                builder.setPositiveButton("Ya, Pendingkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pendingkanAntrian(item.getNoAntrian());
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

        holder.batalkan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Anda ingin membatalkan Antrian?");
                builder.setPositiveButton("Ya, Batalkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        batalkanAntrian(item.getNoAntrian());
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

        holder.selesaikan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Anda ingin menyelesaikan Antrian?");
                builder.setPositiveButton("Ya, Selesaikan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selesaikanAntrian(item.getNoAntrian());
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

    }

    private void pendingkanAntrian(String no_antrian)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Pendingkan Antrian");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PENDINGKAN_ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // kode 0 nik sudah ada antrian pending
                            // kode 1 berhasil
                            // kode 2 gagal
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("no_antrian", no_antrian);
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void selesaikanAntrian(String no_antrian)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Selesaikan Antrian");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SELESAIKAN_ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // kode 0 nik sudah ada antrian pending
                            // kode 1 berhasil
                            // kode 2 gagal
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("no_antrian", no_antrian);
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void batalkanAntrian(String no_antrian)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Membatalkan Antrian");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_BATALKAN_ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // kode 0 nik sudah ada antrian pending
                            // kode 1 berhasil
                            // kode 2 gagal
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("no_antrian", no_antrian);
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noAntrianTextView, namaLengkapTextView, statusAntrianTextView;
        Button selesaikan_button, batalkan_button, pendingkan_button;


        public ViewHolder(View itemView) {
            super(itemView);

            noAntrianTextView = itemView.findViewById(R.id.noAntrianTextView);
            namaLengkapTextView = itemView.findViewById(R.id.namaLengkapTextView);
            statusAntrianTextView = itemView.findViewById(R.id.statusAntrianTextView);
            batalkan_button = itemView.findViewById(R.id.batalkan_antrian_button);
            selesaikan_button = itemView.findViewById(R.id.selesaikan_antrian_button);
            pendingkan_button = itemView.findViewById(R.id.pendingkan_antrian_button);
        }
    }
}
