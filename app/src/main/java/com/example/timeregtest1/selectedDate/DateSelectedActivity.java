package com.example.timeregtest1.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import static com.example.timeregtest1.MainActivity.CURRENT_DAY_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_MONTH_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_YEAR_KEY;

public class DateSelectedActivity extends AppCompatActivity
{

    private ArrayList<DateReg> allDateRegs;

    public static final String SELECTED_DATE_KEY = "selected_date_bundle";

    private RecyclerView dateRegRecView;

    private DateRegsAdapter dateRegsAdapter;

    private TextView txtSelectedDate;

    private ImageView btnAddTimeWorked;
    private BottomNavigationView bottomNavigationView;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selected);

        txtSelectedDate = findViewById(R.id.txtSelectedDateDSA);
        btnAddTimeWorked = findViewById(R.id.btnAddTimeWorked);
        bottomNavigationView = findViewById(R.id.bottomNavView);

        initBottomNavView();

        // get the date that was clicked
        bundle = getIntent().getExtras();
        String currentDate;
        int y = 0, m = 0, d = 0;
        if(bundle != null)
        {
            y = bundle.getInt(CURRENT_YEAR_KEY);
            m = bundle.getInt(CURRENT_MONTH_KEY);
            d = bundle.getInt(CURRENT_DAY_KEY); // the calendar starts to count the months from zero
            currentDate = String.valueOf(y) + "-" + formatDateInt(++m) + "-" + formatDateInt(d);
            txtSelectedDate.setText(currentDate);
        }

        Thread thread = new Thread(new GetAllDateRegsThread(y, m, d));
        thread.start();

        while(thread.isAlive())
        {
            SystemClock.sleep(10);
        }

        dateRegRecView = findViewById(R.id.dateRegRecView);
        dateRegsAdapter = new DateRegsAdapter(this);
        dateRegRecView.setAdapter(dateRegsAdapter);
        dateRegRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        dateRegsAdapter.setAllDateRegs(allDateRegs);

        btnAddTimeWorked.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*Intent intent = new Intent(DateSelectedActivity.this, TimeRegisterActivity.class);
                intent.putExtra(SELECTED_DATE_KEY, bundle);
                startActivity(intent);*/
            }
        });


    }

    private String formatDateInt(int d)
    {
        if(d < 10)
        {
            return new String("0" + String.valueOf(d));
        }
        else
        {
            return String.valueOf(d);
        }
    }

    private void initBottomNavView()
    {
        bottomNavigationView.setSelectedItemId(R.id.register);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.companies:
                        Intent companiesIntent = new Intent(DateSelectedActivity.this, CompanyRegisterActivity.class);
                        startActivity(companiesIntent);
                        break;
                    case R.id.calendar:
                        Intent mainIntent = new Intent(DateSelectedActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case R.id.register:
                        Intent intent = new Intent(DateSelectedActivity.this, TimeRegisterActivity.class);
                        intent.putExtra(SELECTED_DATE_KEY, bundle);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    public class GetAllDateRegsThread implements Runnable
    {
        private static final String TAG = "GetAllDateRegsThread";

        private int y, m, d;

        public GetAllDateRegsThread(int y, int m, int d)
        {
            this.y = y;
            this.m = m;
            this.d = d;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            allDateRegs = (ArrayList<DateReg>) CompanyDatabase.getInstance(DateSelectedActivity.this).dateRegDao().getSelectedDatesData(y, m, d);
        }

    }
}