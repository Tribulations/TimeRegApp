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
import com.example.timeregtest1.mainfragment.MainFragment;
import com.example.timeregtest1.selectedDate.DateSelectedActivity;
import com.google.android.material.appbar.MaterialToolbar;
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

        initViews();

        // toggle for the navdrawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.period:
                        Intent intent = new Intent(MainActivity.this, RegisteredDatesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_drawer_item2:

                        break;
                    default:
                        break;
                }

                return false;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutContainer, new MainFragment());
        transaction.commit();

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

    private void initViews()
    {
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

    }

}