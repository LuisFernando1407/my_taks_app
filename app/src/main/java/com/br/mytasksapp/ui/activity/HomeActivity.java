package com.br.mytasksapp.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnTaskCompleted;
import com.br.mytasksapp.api.rest.TaskHttp;
import com.br.mytasksapp.model.User;
import com.br.mytasksapp.ui.adapter.DashboardAdapter;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.ui.adapter.NoResultAdapter;
import com.br.mytasksapp.util.Util;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnTaskCompleted, DashboardAdapter.AdapterCallback, SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private RecyclerView recyclerDash;

    private TaskHttp taskHttp;

    private AppCompatButton new_task;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView name;
    private TextView email;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.context = this;

        new_task = findViewById(R.id.new_task);
        mSwipeRefreshLayout = findViewById(R.id.swipe);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.black_app,
                R.color.gray_strong_app);


        new_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TaskActivity.class));
            }
        });

        taskHttp = new TaskHttp(context, this);

        if(Util.getPref("first_access", null).equals("yes")){
            taskHttp.setFCMToken(true);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        user = new Gson().fromJson(Util.getPref("lastUser", null), User.class);

        name = headerLayout.findViewById(R.id.name);
        email = headerLayout.findViewById(R.id.email);

        name.setText(user.getName());
        email.setText(user.getEmail());

        recyclerDash = findViewById(R.id.recycler_dash);

        setFontFamilyInMenu(navigationView);

        /* Execute api */
        taskHttp.getMyTasks(null, true);

    }

    private void setFontFamilyInMenu(NavigationView navigationView){

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    Util.applyFontToMenuItem(context, subMenuItem);
                }
            }
            //the method we have create in activity
            Util.applyFontToMenuItem(context, mi);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitAlert();
        }
    }

    private void exitAlert() {
        final AlertDialog alertDialog =  new AlertDialog.Builder(context)
                .setTitle("Atenção")
                .setMessage("Deseja realmente sair?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Util.setApiToken(null);
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    }
                }).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray_app));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update_user) {
            startActivity(new Intent(context, MyDataActivity.class));
        }else if(id == R.id.nav_setting){
            startActivity(new Intent(context, SettingsActivity.class));
        } else if(id == R.id.nav_about){
            Intent intent = new Intent(context, TermsActivity.class);
            intent.putExtra("isAbout", "true");
            startActivity(intent);
        }
        else if (id == R.id.nav_exit) {
            exitAlert();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void taskCompleted(JSONObject results) {
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            JSONArray data = results.getJSONArray("tasks");

            if(data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);

                    tasks.add(new Task(
                            object.getString("_id"),
                            object.getString("user_id"),
                            object.getString("title"),
                            object.getString("description"),
                            object.getString("date"),
                            object.getBoolean("is_notified")
                    ));
                }

                DashboardAdapter dashboardAdapter = new DashboardAdapter(context, tasks);
                recyclerDash.setAdapter(dashboardAdapter);

                RecyclerView.LayoutManager layout = new GridLayoutManager(context, 3);
                recyclerDash.setLayoutManager(layout);
            }else{
                NoResultAdapter noResultAdapter = new NoResultAdapter(context, "Nenhuma tarefa cadastrada", R.drawable.noresult, 100);
                recyclerDash.setAdapter(noResultAdapter);

                RecyclerView.LayoutManager layout = new LinearLayoutManager(context);
                recyclerDash.setLayoutManager(layout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Refresh */
        user = new Gson().fromJson(Util.getPref("lastUser", null), User.class);

        name.setText(Util.limitString(user.getName(), 35, "..."));
        email.setText(user.getEmail());
    }

    @Override
    public void onMethodCallback() {
        taskHttp.getMyTasks(null, true);
    }

    @Override
    public void onRefresh() {
        taskHttp.getMyTasks(mSwipeRefreshLayout, false);
    }
}
