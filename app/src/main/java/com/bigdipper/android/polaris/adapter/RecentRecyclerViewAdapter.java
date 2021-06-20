package com.bigdipper.android.polaris.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.entity.FavoriteData;
import com.bigdipper.android.polaris.entity.RecentData;
import com.bigdipper.android.polaris.ui.NavigationActivity;

import java.util.ArrayList;

public class RecentRecyclerViewAdapter extends RecyclerView.Adapter<RecentRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<RecentData> items;

    public RecentRecyclerViewAdapter(Context context, ArrayList<RecentData> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent, parent, false);
        return new RecentRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentData recent = items.get(position);

        try {
            holder.tvName.setText(recent.getSearchName());
            holder.tvAddress.setText(recent.getAddress());
            holder.tvDate.setText(recent.getTodayDate());

            holder.tvName.setSelected(true);
            holder.tvAddress.setSelected(true);
            holder.tvDate.setSelected(true);

            holder.btnStartNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), NavigationActivity.class);
                    intent.putExtra("destLati", items.get(position).getLatitude());
                    intent.putExtra("destLong", items.get(position).getLongitude());
                    intent.putExtra("destName", items.get(position).getSearchName());
                    context.startActivity(intent);
                }
            });

        } catch (NullPointerException e) {
            Log.e("error ", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;
        TextView tvDate;
        Button btnStartNavigation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_recent_name);
            tvAddress = itemView.findViewById(R.id.tv_recent_address);
            tvDate = itemView.findViewById(R.id.tv_recent_date);
            btnStartNavigation = itemView.findViewById(R.id.btn_recent_start);
        }
    }

    public RecentData getItem(int position) {
        return items.get(position);
    }
}
