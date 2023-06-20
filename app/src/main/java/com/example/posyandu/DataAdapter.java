package com.example.posyandu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private Context context;
    private List<Antrian> dataList;

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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noAntrianTextView, namaLengkapTextView, statusAntrianTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            noAntrianTextView = itemView.findViewById(R.id.noAntrianTextView);
            namaLengkapTextView = itemView.findViewById(R.id.namaLengkapTextView);
            statusAntrianTextView = itemView.findViewById(R.id.statusAntrianTextView);
        }
    }
}
