package com.example.smartpost;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment


        implements TimePickerDialog.OnTimeSetListener {
    TextView textView;


    public TimePickerFragment(TextView text){
        this.textView=text;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Time t = new Time(hourOfDay,minute,0);
        Format formatter;
        formatter = new SimpleDateFormat("HH:mm");
        String time = formatter.format(t);
        textView.setText(time);
    }
}
