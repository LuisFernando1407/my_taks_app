package com.br.mytasksapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnTaskCompleted;
import com.br.mytasksapp.api.rest.TaskHttp;
import com.br.mytasksapp.util.Mask;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class TaskActivity extends AppCompatActivity implements OnTaskCompleted {

    private static final int DRAWABLE_RIGHT = 2;

    private ConstraintLayout root;

    @NotEmpty(messageId = R.string.title)
    private EditText name;

    private EditText description;

    @NotEmpty(messageId = R.string.date)
    private EditText date;

    @NotEmpty(messageId = R.string.hour)
    private EditText hour;

    private AppCompatButton salve;
    private AppCompatButton cancel;

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

    private TaskHttp taskHttp;

    private CheckBox isNotified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        salve = findViewById(R.id.salve);

        isNotified = findViewById(R.id.isNotified);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        root = findViewById(R.id.root);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        date.addTextChangedListener(Mask.insert(date, Mask.MaskType.BIRTHDAY));

        hour = findViewById(R.id.hour);
        hour.addTextChangedListener(Mask.insert(hour, Mask.MaskType.HOUR));

        cancel = findViewById(R.id.cancel);

        this.context = this;

        taskHttp = new TaskHttp(context, this);

        setClickDateTouchDrawable();
        setClickHourTouchDrawable();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uid = bundle.getString("uid");

            title.setText("Visualizar Tarefa");

            salve.setText("Editar");

            taskHttp.getTaskById(uid);

        }

        salve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(salve.getText().toString().equalsIgnoreCase("editar")){
                    enableFields(true);
                    salve.setText("Salvar");
                    cancel.setVisibility(View.VISIBLE);
                }else{
                    validate();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableFields(false);
                salve.setText("Editar");
                cancel.setVisibility(View.GONE);
            }
        });
    }

    private void validate() {
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(context, true));

        if (isValid) {
            JSONObject params = new JSONObject();

            String dateNow = date.getText().toString() + " " + hour.getText().toString();
            String dateFinal = Util.convertDateFormat(dateNow, "dd/MM/yyyy HH:mm", "yyyy-MM-dd HH:mm");

            try {
                params.put("title", name.getText().toString());
                params.put("description", description.getText().toString().isEmpty() ? null : description.getText().toString());
                params.put("date", dateFinal);
                params.put("is_notified", !isNotified.isChecked());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(salve.getText().toString().equalsIgnoreCase("salvar")){
                taskHttp.updateTask(uid, params);
            }else{
                taskHttp.registerTask(params);
            }
        }
    }

    private void enableFields(boolean status){
        name.setEnabled(status);
        description.setEnabled(status);
        date.setEnabled(status);
        hour.setEnabled(status);
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
                String setZeroDay = dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;
                String dateFormat = setZeroDay + "/" + setZeroMonth + "/" + year;
                String dateFinal = dateFormat + 1; //+1 spacing

                date.setText(dateFinal);
            }
        }, mYear, mMonth, mDay).show();
    }

    private void dialogHour(){
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String setZeroOfHourDay = hourOfDay >= 10 ? String.valueOf(hourOfDay) : "0" + hourOfDay;
                String setZeroOfMinute = minute >= 10 ? String.valueOf(minute) : "0" + minute;
                String hourFormat = setZeroOfHourDay + ":" + setZeroOfMinute;
                String hourFinal = hourFormat + 1; //+1 spacing

                hour.setText(hourFinal);
            }
        }, mHour, mMinute, true).show();
    }

    @Override
    public void taskCompleted(JSONObject results) {
        try {
            JSONObject task = results.getJSONObject("task");
            name.setText(task.getString("title"));
            name.clearFocus();

            String descriptionText = !task.getString("description").equalsIgnoreCase("null") ?
                    task.getString("description") : "";

            description.setText(descriptionText);
            description.clearFocus();

            String dateFinal = Util.convertDateFormat(task.getString("date"), "yyyy-MM-dd HH:mm", "dd/MM/yyyy") + 1; //+1 spacing

            date.setText(dateFinal);
            date.clearFocus();

            String hourFinal = Util.convertDateFormat(task.getString("date"), "yyyy-MM-dd HH:mm", "HH:mm") + 1; // +1 spacing

            hour.setText(hourFinal);
            hour.clearFocus();

            boolean isNotifiedBool = task.getBoolean("is_notified");

            isNotified.setChecked(!isNotifiedBool);

            if(isNotifiedBool){
                snackMessage();
            }

            /* State init */
            enableFields(false);
            salve.setText("Editar");
            cancel.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void snackMessage(){
        Snackbar.make(root, "Tarefa já notificada", Snackbar.LENGTH_LONG).show();
    }

}