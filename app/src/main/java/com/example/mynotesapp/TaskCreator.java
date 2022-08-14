package com.example.mynotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskCreator extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    AlertDialog alertDialog;
    Notes notes;
    EditText task;
    Button setBtn, cancelBtn, viewDate, viewTime;
    int hour, minute;
    Calendar cal = Calendar.getInstance();
    TimePickerDialog timePickerDialog;
    static String halfDay, taskString;
    private int notificationId = 1;
    boolean oldTask = false;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);

        viewTime = findViewById(R.id.viewTime);
        viewDate = findViewById(R.id.viewDate);
        task = findViewById(R.id.task);
        setBtn = findViewById(R.id.setBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        checkBox = findViewById(R.id.completed);

        try{
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            task.setText(notes.getNotes());
            oldTask = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        viewDate.setText(getTodayDate());
        viewTime.setText(getNowTime());

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(TaskCreator.this, AlarmReceiver.class);
                intent1.putExtra("notificationId", notificationId);
                intent1.putExtra("message", taskString);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        TaskCreator.this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT
                );

                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                long alarmStartTime = cal.getTimeInMillis();

                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
                Toast.makeText(TaskCreator.this, "Done!", Toast.LENGTH_SHORT).show();

                if(!oldTask){
                    notes = new Notes();
                }

                setNote(false);

                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = checkBox.isChecked();
                if(checked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TaskCreator.this);

                    builder.setTitle("Did you complete the task?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    setNote(true);

                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkBox.setChecked(false);
                                    alertDialog.dismiss();
                                }
                            });

                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }

        });
    }

    public void initDatePicker(View view){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String month1 = getMonthFormat(month);
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                viewDate.setText(String.format("%s %02d, %04d", month1, day, year));
            }
        };
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void initTimePicker(View view){
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                minute = minutes;
                if(hours > 12){
                    halfDay = "PM";
                    hour = hours-12;
                }else{
                    halfDay = "AM";
                    hour = hours;
                }
                cal.set(Calendar.HOUR_OF_DAY, hours);
                cal.set(Calendar.MINUTE, minutes);
                viewTime.setText(String.format("%02d:%02d %s", hour, minute, halfDay));
            }
        };
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hour, minute, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private String getMonthFormat(int month) {
        if(month == 1){
            return "JAN";
        }
        if(month == 2){
            return "FEB";
        }
        if(month == 3) {
            return "MAR";
        }
        if(month == 4){
            return "APR";
        }
        if(month == 5){
            return "MAY";
        }
        if(month == 6){
            return "JUN";
        }
        if(month == 7){
            return "JUL";
        }
        if(month == 8){
            return "AUG";
        }
        if(month == 9){
            return "SEP";
        }
        if(month == 10){
            return "OCT";
        }
        if(month == 11){
            return "NOV";
        }
        if(month == 12){
            return "DEC";
        }
        return "JAN";

    }

    private String getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    private String getNowTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Date date = new Date();
        return formatter.format(date);
    }

    public void setNote(boolean checkbox){
        taskString = task.getText().toString();

        if(checkbox){
            notes.setTitle(taskString);
            notes.setNotes("Finished");
        }else{
            notes.setNotes(taskString);
            notes.setTitle("Task!");
        }
        notes.setPinned(true);

        Intent intent = new Intent();
        intent.putExtra("note", notes);
        setResult(Activity.RESULT_OK, intent);
    }

}