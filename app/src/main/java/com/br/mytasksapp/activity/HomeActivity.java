package com.br.mytasksapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.br.mytasksapp.R;
import com.br.mytasksapp.adapter.DashboardAdapter;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.util.FontsOverride;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private RecyclerView recyclerDash;

    /* Randoms */
    private String[] itemsTasks = { "wine", "sniff", "passenger", "fax", "impartial", "protest",
            "channel", "drop", "quirky", "yell", "telephone", "room", "giraffe", "tidy", "wistful", "expansion", "cover",
            "fry", "warm", "rustic", "tongue",};
    private String[] itemsDateTasks = { "21/04/2019 11:50", "25/04/2019 04:35", "06/04/2019 15:19", "15/04/2019 23:35", "22/04/2019 05:21", "19/04/2019 22:40", "21/04/2019 02:25",
            "03/04/2019 10:35", "27/04/2019 04:22", "19/04/2019 18:05", "09/04/2019 16:44", "27/04/2019 03:08", "05/04/2019 20:55",
            "29/04/2019 04:41", "04/04/2019 16:47", "26/04/2019 10:27", "23/04/2019 23:13", "09/04/2019 15:54", "19/04/2019 19:11", "30/04/2019 20:34", "11/04/2019 02:42"
    };

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerDash = findViewById(R.id.recycler_dash);


        DashboardAdapter dashboardAdapter = new DashboardAdapter(context, getTasks());
        recyclerDash.setAdapter(dashboardAdapter);

        RecyclerView.LayoutManager layout = new GridLayoutManager(context, 3);
        recyclerDash.setLayoutManager(layout);

        setFontFamilyInMenu(navigationView);
    }

    private void setFontFamilyInMenu(NavigationView navigationView){

        Menu m = navigationView.getMenu();

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for applying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "montserrat_regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new FontsOverride("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
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
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(context, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update_user) {
            startActivity(new Intent(context, MyDataActivity.class));
        } else if (id == R.id.nav_exit) {
            exitAlert();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList<Task> getTasks(){
        ArrayList<Task> tasks = new ArrayList<>();

        int index = 0;

        for(int i = 0; i < itemsTasks.length; i++) {
            tasks.add(new Task(index++, itemsTasks[i], itemsDateTasks[i]));
        }

        return tasks;
    }
}
