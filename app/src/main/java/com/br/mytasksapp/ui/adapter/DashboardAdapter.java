package com.br.mytasksapp.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.R;
import com.br.mytasksapp.api.BaseJsonHandler;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.ui.activity.HomeActivity;
import com.br.mytasksapp.ui.activity.TaskActivity;
import com.br.mytasksapp.util.Util;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.RecyclerViewHolder> {

    private ArrayList<Task> tasks;

    private AdapterCallback callback;

    private Context context;

    public DashboardAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
        this.callback = ((AdapterCallback) context);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_dash, viewGroup, false);
        return new DashboardAdapter.RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, @SuppressLint("RecyclerView") final int i) {
        final Task task = tasks.get(i);

        recyclerViewHolder.name.setText(Util.limitString(task.getName(), 7, "..."));
        recyclerViewHolder.date.setText(Util.convertDateFormat(task.getDate(), "yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm"));

        recyclerViewHolder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("uid", task.getId());
                context.startActivity(intent);
            }
        });

        recyclerViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert(i, task.getId(), task.getName());
            }
        });
    }

    private void alert(final int position, final String id, String name){
        final AlertDialog alertDialog =  new AlertDialog.Builder(context)
                .setTitle("Atenção!")
                .setMessage("Deseja realmente excluir a tarefa " + name + "?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        removeAtServer(id, position);
                    }
                }).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray_app));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });

        alertDialog.show();
    }

    private void removeAtServer(String uid, final int position){
        AsyncHttpClient client = Util.createAsyncHttpClient();

        client.addHeader("Authorization", Constants.API.TYPE_REQUEST + Util.getApiToken());
        client.delete(context, Constants.API.TASKS + "/" + uid, new BaseJsonHandler(context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                tasks.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, tasks.size());
                callback.onMethodCallback();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface AdapterCallback {
        void onMethodCallback();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private GridLayout info;
        private AppCompatButton delete;

        private RecyclerViewHolder(View itemView) {
            super(itemView);

            info = itemView.findViewById(R.id.info);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            delete = itemView.findViewById(R.id.remove);
        }
    }
}