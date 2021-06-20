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
import com.bigdipper.android.polaris.ui.NavigationActivity;

import java.util.ArrayList;

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<FavoriteData> items;

    public FavoriteRecyclerViewAdapter(Context context, ArrayList<FavoriteData> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public FavoriteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteData favorite = items.get(position);

        try {
            holder.tvNumber.setText(favorite.getNumber());
            holder.tvName.setText(favorite.getSearchName());
            holder.tvAddress.setText(favorite.getAddress());

            holder.tvName.setSelected(true);
            holder.tvAddress.setSelected(true);

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
        TextView tvNumber;
        TextView tvName;
        TextView tvAddress;
        Button btnStartNavigation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tv_favorite_number);
            tvName = itemView.findViewById(R.id.tv_favorite_name);
            tvAddress = itemView.findViewById(R.id.tv_favorite_address);
            btnStartNavigation = itemView.findViewById(R.id.btn_favorite_start);
        }
    }

    public FavoriteData getItem(int position) {
        return items.get(position);
    }
}
