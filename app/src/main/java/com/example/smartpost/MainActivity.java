package com.example.smartpost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView screen,open,closing;
    Spinner spinner;
    BackGround BG;
    // Values for search
    int country = 0;
    double opening, close = 0;
    List<Integer> weekdays = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0));
    ArrayList<Post> posts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BG = BackGround.getInstance();

        open = findViewById(R.id.Open);
        closing = findViewById(R.id.Closing);
        screen  = findViewById(R.id.Screen);
        spinner = findViewById(R.id.spinner);

        open.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setOpening(open.getText().toString());
                updateSpinner(null);
            }
        }); //handles opening update

        closing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setClose(closing.getText().toString());
                updateSpinner(null);
            }
        }); //handles closing update

        final Handler handler = new Handler(); // runs Spinner update every 1 sec
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(BG.isUpdated()==true){updateSpinner(null);}
                handler.postDelayed(this, delay);
            }
        }, delay);

        final WeekdaysPicker widget = (WeekdaysPicker) findViewById(R.id.weekdays); //changes weekdays array.
        widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                List<Integer> SD = widget.getSelectedDays();
                for(int i=2;i<8;i++) {
                    if (SD.contains(i)) {
                        weekdays.set(i-2, 1);
                    } else
                        weekdays.set(i-2, 0);
                }
                if (SD.contains(1)) {
                    weekdays.set(6, 1);
                } else
                    weekdays.set(6, 0);


                updateSpinner(null);
            }
        });



    }




    public void updateSpinner (View v){


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,  getData());
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }


    public ArrayList<String> getData(){

        posts= BG.getPosts(this.weekdays,this.opening,this.close,this.country);
        ArrayList<String> Entries = new ArrayList<String>();

        String temp ="";
        for(Post post : posts){
            temp = temp.concat(post.getName()+" ");
            temp = temp.concat(post.getCity()+" ");
            temp = temp.concat(post.getAddress());
            //System.out.println(temp);
            Entries.add(temp);
            temp  = "";
        }
        //System.out.print(bottles);

        return Entries;
    }



    public void setScreen (View v){
        String s=null;
        int index = spinner.getSelectedItemPosition();
        int id = posts.get(index).getPlace_id();
        s = BG.getInformation(id);
        screen.setText(s);
    }

    public void setOpenTime (View v){

        DialogFragment newFragment = new TimePickerFragment(open);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        //setOpening(open.getText().toString());
        updateSpinner(null);

    }
    public void setOpening(String o) {
        System.out.println(o);
        String s;
        String[] t;
        if(!o.equals("")) {
            t = o.trim().split(":");
            s = (t[0] + "." + t[1]);
            System.out.println(s);
            this.opening = Double.parseDouble(s);
            System.out.println(this.opening);
        }
    }

    public void setClose(String o) {
        System.out.println(o);
        String s;
        String[] t;
        if(!o.equals("")){
            t=o.trim().split(":");
            s=(t[0]+"."+t[1]);
            System.out.println(s);
            this.close = Double.parseDouble(s);
            System.out.println(this.close);
        }
    }

    public void setClosingTime (View v){

        DialogFragment newFragment = new TimePickerFragment(closing);
        newFragment.show(getSupportFragmentManager(), "timePicker");

        updateSpinner(null);
    }

    public void clearTime (View v){
        open.setText("");
        this.close=0;
        this.opening=0;
        closing.setText("");
        updateSpinner(null);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked)
                    country = 0;
                    break;
            case R.id.radioButton2:
                if (checked)
                    country = 1;
                    break;
            case R.id.radioButton3:
                if (checked)
                    country = 2;
                    break;
        }
        updateSpinner(null);
    }



}
