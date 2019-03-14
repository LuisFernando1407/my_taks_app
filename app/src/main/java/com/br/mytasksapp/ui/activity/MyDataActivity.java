package com.br.mytasksapp.ui.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.br.mytasksapp.R;
import com.br.mytasksapp.ui.adapter.MyDataAdapter;

import java.util.Objects;

public class MyDataActivity extends AppCompatActivity {

    private String[] titles = {"GERAL", "ACESSO"};
    private Context context;

    private TabLayout tabLayout;
    private ViewPager pager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        this.context = this;

        tabLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Typeface font = Typeface.createFromAsset(getAssets(), "montserrat_regular.ttf");

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.item_tab,null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tv.setTypeface(font);
            }
            Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(tv);
        }

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorHeight((int) (4 * getResources().getDisplayMetrics().density));

        fragmentManager = getSupportFragmentManager();

        MyDataAdapter settingsAdapter = new MyDataAdapter(fragmentManager, tabLayout.getTabCount());

        pager.setAdapter(settingsAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}