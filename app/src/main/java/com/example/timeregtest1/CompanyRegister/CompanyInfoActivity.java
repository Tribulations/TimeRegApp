package com.example.timeregtest1.CompanyRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.EditDialog;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import static com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity.COMPANY_NAME_KEY;

public class CompanyInfoActivity extends AppCompatActivity
{

    private TextView txtCompanyNameCIA;
    private BottomNavigationView bottomNavigationView;

    private String companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        txtCompanyNameCIA = findViewById(R.id.txtCompanyNameCIA);
        bottomNavigationView = findViewById(R.id.bottomNavView);

        initBottomNavView();

        Intent intent = getIntent();

        if(intent != null)
        {
            companyName = intent.getStringExtra(COMPANY_NAME_KEY);
            if(companyName != null)
            {
                txtCompanyNameCIA.setText(companyName);
            }
            else
            {
                Toast.makeText(this, "Company Name is null", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Bundle is null", Toast.LENGTH_SHORT).show();
        }

        // rename or delete company when long click
        txtCompanyNameCIA.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                EditDialog editCompanyDialog = new EditDialog();
                Bundle bundle = new Bundle();
                bundle.putString(COMPANY_NAME_KEY, companyName);
                editCompanyDialog.setArguments(bundle);
                editCompanyDialog.show(getSupportFragmentManager(), "Edit company");

                return true;
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
                        Intent companiesIntent = new Intent(CompanyInfoActivity.this, CompanyRegisterActivity.class);
                        startActivity(companiesIntent);
                        break;
                    case R.id.calendar:
                        Intent mainIntent = new Intent(CompanyInfoActivity.this, MainActivity.class);
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
}