package com.example.timeregtest1.CompanyRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.timeregtest1.CompanyAdapter;
import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.example.timeregtest1.RegisteredDatesActivity;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class CompanyRegisterActivity extends AppCompatActivity implements CompanyAdapter.CompanyNameClicked
{
    public static final String COMPANY_NAME_KEY = "company_name";

    private String mCompanyName;

    @Override
    public void onCompanyNameClicked(String companyName, int id)
    {
        /*CompanyInfoFragment companyInfoFragment = new CompanyInfoFragment();
        companyInfoFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, companyInfoFragment);
        transaction.commit();*/

        Intent intent = new Intent(CompanyRegisterActivity.this, CompanyInfoActivity.class);
        intent.putExtra(COMPANY_NAME_KEY, companyName);
        startActivity(intent);
    }

    private RecyclerView companiesRecView;
    private BottomNavigationView bottomNavigationView;

    private Button btnAddCompany;
    private Button tempBtn;
    private EditText edtTxtAddCompany;

    private ArrayList<Company> allCompanies = new ArrayList<>();
    private LiveData<List<Company>> allCompaniesLiveData;

    private CompanyAdapter companyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);

        companiesRecView = findViewById(R.id.companiesRecView);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        btnAddCompany = findViewById(R.id.btnAddCompany);
        edtTxtAddCompany = findViewById(R.id.edtTxtAddCompany);
        tempBtn = findViewById(R.id.tempBtn);

        initBottomNavView();

        Runnable r = new GetAllCompaniesThread();
        //Thread thread = new Thread(new GetAllCompaniesThread());
        Thread thread = new Thread(r);
        thread.start();

        // remove this and use LiveData or something? or show a load screen at least?
        while(thread.isAlive())
        {
            SystemClock.sleep(10);
        }

        companyAdapter = new CompanyAdapter(this);
        companiesRecView.setAdapter(companyAdapter);
        companiesRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        if(allCompanies != null)
        {
            companyAdapter.setAllCompanies(allCompanies);
        }

        btnAddCompany.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String companyToAdd = edtTxtAddCompany.getText().toString();
                Runnable r2 = new InsertSingleCompanyThread(companyToAdd);
                //Thread t = new Thread(new InsertSingleCompanyThread(companyToAdd));
                Thread t = new Thread(r2);
                t.start();
            }
        });

        // when a change eg. a new company is added refresh the recyclerview
        allCompaniesLiveData = CompanyDatabase.getInstance(this).companyDao().getAllCompaniesLiveData();
        allCompaniesLiveData.observe(this, new Observer<List<Company>>()
        {
            @Override
            public void onChanged(List<Company> companies)
            {
                //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                /*Runnable g = new GetAllCompaniesThread();
                Thread thread = new Thread(g);
                thread.start();

                while(thread.isAlive())
                {
                    SystemClock.sleep(10);
                }*/

                //companyAdapter.setAllCompanies(allCompanies);
                companyAdapter.setAllCompanies((ArrayList<Company>) companies);
            }


        });

        tempBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CompanyRegisterActivity.this, RegisteredDatesActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initBottomNavView()
    {
        bottomNavigationView.setSelectedItemId(R.id.companies);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.companies:
                        // TODO: 2021-08-09 Use livedata so that the list is filled dynamically if all companies haven't loaded from the database
                        /*Intent companiesIntent = new Intent(CompanyRegisterActivity.this, CompanyRegisterActivity.class);
                        startActivity(companiesIntent);*/
                        break;
                    case R.id.calendar:
                        Intent mainIntent = new Intent(CompanyRegisterActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case R.id.register:
                        // what will this one do? hahaa remove?
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    // make this its own class in other file becuase I use this class in multiple activites?
    public class GetAllCompaniesThread implements Runnable
    {
        private static final String TAG = "GetAllCompaniesThread";

        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(CompanyRegisterActivity.this).companyDao().getAllCompanies();
        }

    }

    public class InsertSingleCompanyThread implements Runnable
    {
        private static final String TAG = "InsertSingleCompanyThread";

        private String companyName;

        public InsertSingleCompanyThread(String companyName)
        {
            this.companyName = companyName;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            CompanyDatabase.getInstance(CompanyRegisterActivity.this).companyDao().insertSingleCompany(companyName);
        }

    }
}