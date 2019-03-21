package com.br.mytasksapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.util.Mask;

import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {

    private static final int DRAWABLE_RIGHT = 2;

    private EditText name;
    private EditText description;
    private EditText date;
    private EditText hour;

    private AppCompatButton salve;

    private Context context;

    private Calendar c = Calendar.getInstance();

    private int mYear = c.get(Calendar.YEAR);
    private int mMonth = c.get(Calendar.MONTH);
    private int mDay = c.get(Calendar.DAY_OF_MONTH);

    private int mHour = c.get(Calendar.HOUR_OF_DAY);
    private int mMinute = c.get(Calendar.MINUTE);

    private String uid;

    private Toolbar toolbar;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        salve = findViewById(R.id.salve);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        date.addTextChangedListener(Mask.insert(date, Mask.MaskType.BIRTHDAY));

        hour = findViewById(R.id.hour);
        hour.addTextChangedListener(Mask.insert(hour, Mask.MaskType.HOUR));

        this.context = this;

        setClickDateTouchDrawable();
        setClickHourTouchDrawable();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uid = bundle.getString("uid");
            title.setText("Visualizar Tarefa");
        }

    }

    private void getDetailsTaskById(String uid){

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickDateTouchDrawable(){
        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (date.getRight() - date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        dialogDate();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickHourTouchDrawable(){
        hour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (hour.getRight() - hour.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        dialogHour();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void dialogDate(){
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int newMonth = (month+1);

                String setZeroMonth = newMonth >= 10 ? String.valueOf(newMonth) : "0" + newMonth;
                String dateFormat = dayOfMonth + "/" + setZeroMonth + "/" + year;

                date.setText(dateFormat);
            }
        }, mYear, mMonth, mDay).show();
    }


    private void dialogHour(){
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourFormat = hourOfDay + ":" + minute;
                hour.setText(hourFormat);
            }
        }, mHour, mMinute, true).show();
    }

}