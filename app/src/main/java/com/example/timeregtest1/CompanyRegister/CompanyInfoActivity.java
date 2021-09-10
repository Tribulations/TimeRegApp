package com.example.timeregtest1.CompanyRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyAdapter;
import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.EditDialog;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import static com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity.COMPANY_ID_KEY;
import static com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity.COMPANY_NAME_KEY;

public class CompanyInfoActivity extends AppCompatActivity implements EditDialog.EditDialogClicked
{
    @Override
    public void onRename()
    {
        if(edtTxtNewName.getText().toString().equals(""))
        {
            Toast.makeText(this, "Du måste först skriva in det nya namnet i textfältet", Toast.LENGTH_SHORT).show();
        }
        else
        {

            AlertDialog.Builder newNameDialog = new AlertDialog.Builder(CompanyInfoActivity.this)
                    .setTitle("Byt namn?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Thread updateThread = new Thread(new UpdateCompanyNameThread(edtTxtNewName.getText().toString(), companyId));
                            updateThread.start();

                            txtCompanyNameCIA.setText(edtTxtNewName.getText().toString());

                            edtTxtNewName.setText("");
                            edtTxtNewName.setHint("Nytt namn?");
                            if(edtTxtNewName.isFocused())
                            {
                                edtTxtNewName.clearFocus();
                            }
                        }
                    })
                    .setNegativeButton("Nej", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
            newNameDialog.show();
        }
    }

    @Override
    public void onDelete()
    {
        AlertDialog.Builder deletedialog = new AlertDialog.Builder(CompanyInfoActivity.this)
                .setTitle("Ta bort företaget?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(CompanyInfoActivity.this, CompanyRegisterActivity.class);
                        startActivity(intent);

                        Thread deleteThread = new Thread(new DeleteCompanyThread(companyId));
                        deleteThread.start();
                    }
                })
                .setNegativeButton("Nej", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
        deletedialog.show();
    }

    private TextView txtCompanyNameCIA;
    private BottomNavigationView bottomNavigationView;
    private EditText edtTxtNewName;

    private String companyName;
    private int companyId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        txtCompanyNameCIA = findViewById(R.id.txtCompanyNameCIA);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        edtTxtNewName = findViewById(R.id.edtTxtNewName);

        initBottomNavView();

        Intent intent = getIntent();

        if(intent != null)
        {
            companyName = intent.getStringExtra(COMPANY_NAME_KEY);
            companyId = intent.getIntExtra(COMPANY_ID_KEY, -1);
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
                editCompanyDialog.show(getSupportFragmentManager(), "Ändra företagsnamn eller ta bort företaget?");

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

    public class DeleteCompanyThread implements Runnable
    {
        private int companyId;

        public DeleteCompanyThread(int companyId)
        {
            this.companyId = companyId;
        }

        @Override
        public void run()
        {
            CompanyDatabase.getInstance(CompanyInfoActivity.this).companyDao().deleteCompany(companyId);
        }
    }

    public class UpdateCompanyNameThread implements Runnable
    {
        private String companyName;
        private int id;

        public UpdateCompanyNameThread(String companyName, int id)
        {
            this.companyName = companyName;
            this.id = id;
        }

        @Override
        public void run()
        {
            CompanyDatabase.getInstance(CompanyInfoActivity.this).companyDao().updateCompanyName(companyName, id);
        }
    }
}