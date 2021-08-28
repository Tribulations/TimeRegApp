package com.example.timeregtest1;

import androidx.annotation.NonNull;
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

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;
import com.example.timeregtest1.selectedDate.DateSelectedActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
/*import com.example.timeregtest1.TimeRegister.TimeRegisterActivity2;*/

public class MainActivity extends AppCompatActivity
{
    public static final String CURRENT_YEAR_KEY = "current_year";
    public static final String CURRENT_MONTH_KEY = "current_month";
    public static final String CURRENT_DAY_KEY = "current_day";

    private CalendarView calendarView;
    private BottomNavigationView bottomNavigationView;

    /*private FrameLayout mainActivityFragment;*/

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ArrayList<DateReg> allDateRegs = new ArrayList<>();

    private int y = 0, m = 0, d = 0;

    private Bundle bundle = new Bundle();




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        /*mainActivityFragment = findViewById(R.id.mainFragmentLayout);*/

        // initial date of today. Used to show the date regs of the current date when first starting the app



        Calendar today = Calendar.getInstance();
        y = today.get(Calendar.YEAR);
        m = today.get(Calendar.MONTH);
        d = today.get(Calendar.DAY_OF_MONTH);

        /*Thread initialThread = new Thread(new GetAllDateRegsThread(y, m, d));
        initialThread.start();

        // remove this while and add LiveData instead

        while(initialThread.isAlive())
        {
            SystemClock.sleep(10);
        }
*/
        // show the data in the fragment


        initFragmentTransaction(y, m, d);

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFragmentLayout, dateSelectedFragment);
        transaction.commit();*/

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                /*Intent dateSelectIntent = new Intent(MainActivity.this, DateSelectedActivity.class);
                dateSelectIntent.putExtra(CURRENT_YEAR_KEY, year);
                dateSelectIntent.putExtra(CURRENT_MONTH_KEY, month);
                dateSelectIntent.putExtra(CURRENT_DAY_KEY, dayOfMonth);
                startActivity(dateSelectIntent);*/

                y = year;
                m = month;
                d = dayOfMonth;

                //Show fragment with registered data of the selected date

                // run the code below in method
                initFragmentTransaction(y, m, d);

                /*bundle.putInt(CURRENT_YEAR_KEY, y);
                bundle.putInt(CURRENT_MONTH_KEY, m);
                bundle.putInt(CURRENT_DAY_KEY, d);
                dateSelectedFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.mainFragmentLayout, dateSelectedFragment);
                transaction.commit();*/
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
                        Intent dateSelectIntent = new Intent(MainActivity.this, TimeRegisterActivity.class);
                        dateSelectIntent.putExtra(CURRENT_YEAR_KEY, y);
                        dateSelectIntent.putExtra(CURRENT_MONTH_KEY, m);
                        dateSelectIntent.putExtra(CURRENT_DAY_KEY, d);
                        startActivity(dateSelectIntent);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    /*public class GetAllDateRegsThread implements Runnable
    {
        private static final String TAG = "GetAllDateRegsThread";

        private int mYear, mMonth, mDay;

        public GetAllDateRegsThread(int mYear, int mMonth, int mDay)
        {
            this.mYear = mYear;
            this.mMonth = mMonth;
            this.mDay = mDay;
        }

        @Override
        public void run()
        {
            allDateRegs = (ArrayList<DateReg>) CompanyDatabase.getInstance(MainActivity.this).dateRegDao().getSelectedDatesData(mYear, mMonth, mDay);
        }
    }*/
}