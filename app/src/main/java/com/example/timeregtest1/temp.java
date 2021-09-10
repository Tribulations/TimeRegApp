/*
package com.example.timeregtest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;
import com.example.timeregtest1.selectedDate.DateSelectedActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
*/
/*import com.example.timeregtest1.TimeRegister.TimeRegisterActivity2;*//*





public class MainActivity extends AppCompatActivity
{
    public static final String CURRENT_YEAR_KEY = "current_year";
    public static final String CURRENT_MONTH_KEY = "current_month";
    public static final String CURRENT_DAY_KEY = "current_day";

    private CalendarView calendarView;
    private BottomNavigationView bottomNavigationView;



    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private ArrayList<DateReg> allDateRegs = new ArrayList<>();

    private int y = 0, m = 0, d = 0;

    private Bundle bundle = new Bundle();

    private Calendar today = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toggle for the navdrawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, )

        calendarView = findViewById(R.id.calendarView);
        bottomNavigationView = findViewById(R.id.bottomNavView);







        y = today.get(Calendar.YEAR);
        m = today.get(Calendar.MONTH);
        d = today.get(Calendar.DAY_OF_MONTH);




        initFragmentTransaction(y, m, d);



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {


                y = year;
                m = month;
                d = dayOfMonth;

                initFragmentTransaction(year, month, dayOfMonth);


            }
        });

        initBottomNavView();



    }

    private void initFragmentTransaction(int y, int m, int d)
    {
        final DateSelectedFragment dateSelectedFragment = new DateSelectedFragment();

        bundle.putInt(CURRENT_YEAR_KEY, y);
        bundle.putInt(CURRENT_MONTH_KEY, m);
        bundle.putInt(CURRENT_DAY_KEY, d);
        dateSelectedFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFragmentLayout, dateSelectedFragment);
        transaction.commit();
    }

    private void initBottomNavView()
    {
        bottomNavigationView.setSelectedItemId(R.id.calendar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.companies:
                        // TODO: 2021-08-09 Use livedata so that the list is filled dynamically if all companies haven't loaded from the database
                        Intent companiesIntent = new Intent(MainActivity.this, CompanyRegisterActivity.class);
                        startActivity(companiesIntent);
                        break;
                    case R.id.calendar:
                        // nothing needed here?
                        break;
                    case R.id.register:

                        if(!dateAllowed(today, y, m, d))
                        {
                            Toast.makeText(MainActivity.this, "Du kan inte registrera på ett datum i framtiden", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent dateSelectIntent = new Intent(MainActivity.this, TimeRegisterActivity.class);
                            dateSelectIntent.putExtra(CURRENT_YEAR_KEY, y);
                            dateSelectIntent.putExtra(CURRENT_MONTH_KEY, m);
                            dateSelectIntent.putExtra(CURRENT_DAY_KEY, d);
                            startActivity(dateSelectIntent);
                        }

                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    private boolean dateAllowed(Calendar today, int year, int month, int day)
    {
        Calendar c = Calendar.getInstance();

        c.set(year,month,day,0, 0, 0);

        if(c.getTimeInMillis() > today.getTimeInMillis())
        {
            return false;
        }

        return true;
    }


}*/
