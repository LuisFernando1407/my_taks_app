package com.br.mytasksapp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.br.mytasksapp.R;
import com.bumptech.glide.Glide;

public class NoResultAdapter  extends RecyclerView.Adapter<NoResultAdapter.NoResultHolder>{
    private String message;
    private Context context;
    private int icon;
    private int marginTop;

    public NoResultAdapter(Context context, String message, int icon, int marginTop){
        this.context = context;
        this.message = message;
        this.icon = icon;
        this.marginTop = marginTop;
    }

    @NonNull
    @Override
    public NoResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_no_result, parent, false);
        return new NoResultAdapter.NoResultHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoResultHolder holder, int position) {
        holder.message.setText(message);

        if(marginTop > 0) {

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.icon.getLayoutParams();

            layoutParams.setMargins(0, marginTop, 0, 0);

            holder.icon.setLayoutParams(layoutParams);
        }

        if(icon != 0) {
            Glide.with(context)
                    .asBitmap()
                    .load(icon)
                    .into(holder.icon);
        }else{
            LinearLayout.LayoutParams layoutParamsMessage = (LinearLayout.LayoutParams) holder.message.getLayoutParams();

            layoutParamsMessage.setMargins(0, marginTop, 0, 0);

            holder.message.setLayoutParams(layoutParamsMessage);

            holder.icon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class NoResultHolder extends RecyclerView.ViewHolder {

        private TextView message;
        private ImageView icon;

        private NoResultHolder(View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.txtMessage);
            icon = itemView.findViewById(R.id.imgIcon);
        }
    }

}