package org.ufam.gather4u.utils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CalendarView;

import org.ufam.gather4u.R;

public class CalenderView extends Activity {

    private String strDate = "";

    public String getStrDate(){
        return strDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendar);

        final CalendarView calendarView = (CalendarView) findViewById(R.id.cv_data);

        // quando selecionado alguma data diferente da padrão
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                //intent.putExtra("dataLongMiliseconds", (Long) calendarView.getDate());
                strDate = dayOfMonth + 1 + "/" + month + year;
            }
        });
    }
}
