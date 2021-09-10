package com.example.timeregtest1.mainfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity;
import com.example.timeregtest1.DateSelectedFragment;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainFragment extends Fragment
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initViews(view);

        y = today.get(Calendar.YEAR);
        m = today.get(Calendar.MONTH);
        d = today.get(Calendar.DAY_OF_MONTH);

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

        return view;
    }

    private void initViews(View view)
    {
        calendarView = view.findViewById(R.id.calendarView);
        bottomNavigationView = view.findViewById(R.id.bottomNavView);
    }

    private void initFragmentTransaction(int y, int m, int d)
    {
        final DateSelectedFragment dateSelectedFragment = new DateSelectedFragment();

        bundle.putInt(CURRENT_YEAR_KEY, y);
        bundle.putInt(CURRENT_MONTH_KEY, m);
        bundle.putInt(CURRENT_DAY_KEY, d);
        dateSelectedFragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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
                        Intent companiesIntent = new Intent(getContext(), CompanyRegisterActivity.class);
                        startActivity(companiesIntent);
                        break;
                    case R.id.calendar:
                        // nothing needed here?
                        break;
                    case R.id.register:

                        if(!dateAllowed(today, y, m, d))
                        {
                            Toast.makeText(getContext(), "Du kan inte registrera på ett datum i framtiden", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent dateSelectIntent = new Intent(getContext(), TimeRegisterActivity.class);
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
}
