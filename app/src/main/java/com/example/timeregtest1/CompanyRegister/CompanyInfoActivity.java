package com.example.timeregtest1.CompanyRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyAdapter;
import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.EditDialog;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.example.timeregtest1.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

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
            Utils.hideKeyboard(this);
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
    private Button btnHelp;
    private ConstraintLayout parentConLayout;

    private String companyName;
    private int companyId = -1;

    private Snackbar snackbar;

    private boolean showingHelp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);

        initViews();
        initBottomNavView();
        initSnackbar();

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

        btnHelp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!showingHelp)
                {
                    Utils.hideKeyboard(CompanyInfoActivity.this);
                    snackbar.show();
                    btnHelp.setText("Göm");
                    showingHelp = true;
                }
                else
                {
                    btnHelp.setText("Hjälp");
                    snackbar.dismiss();
                    showingHelp = false;
                }

            }
        });

    }

    private void initViews()
    {
        txtCompanyNameCIA = findViewById(R.id.txtCompanyNameCIA);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        edtTxtNewName = findViewById(R.id.edtTxtNewName);
        btnHelp = findViewById(R.id.btnHelp);
        parentConLayout = findViewById(R.id.parentConLayout);
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

    private void initSnackbar()
    {
        // make some sentences bold
        final SpannableStringBuilder snackbarText = new SpannableStringBuilder("För att ta " +
                "bort det här företaget trycker du och håller in på företagsnamnet " +
                "och sedan väljer ta bort.\n\n" +
                "Ändra namn på företaget?\nOm du vill byta namn på det här företaget " +
                "så skirver du först in det nya namnet i textrutan och sedan trycker " +
                "och håller in på företagsnamnet och sedan i fönstret som kommer upp klickar " +
                "du på ändra namn.");

        final StyleSpan bold = new StyleSpan(Typeface.BOLD);
        snackbarText.setSpan(bold, 100, 128, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        snackbar = Snackbar.make(parentConLayout, snackbarText, Snackbar.LENGTH_INDEFINITE)
                .setAction("Stäng", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        snackbar.dismiss();
                        showingHelp = false;
                        btnHelp.setText("Hjälp");
                    }
                });

        View snackbarView = snackbar.getView();
        TextView txtSnack = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        txtSnack.setMaxLines(15);

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