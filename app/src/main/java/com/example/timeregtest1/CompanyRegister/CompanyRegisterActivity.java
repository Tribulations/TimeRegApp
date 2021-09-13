package com.example.timeregtest1.CompanyRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CompanyRegisterActivity extends AppCompatActivity implements CompanyAdapter.CompanyNameClicked
{
    public static final String COMPANY_NAME_KEY = "company_name";
    public static final String COMPANY_ID_KEY = "company_id";

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
        intent.putExtra(COMPANY_ID_KEY, id);
        startActivity(intent);
    }

    private RecyclerView companiesRecView;
    private BottomNavigationView bottomNavigationView;

    private Button btnAddCompany, btnHelp;
    private EditText edtTxtAddCompany;

    private ArrayList<Company> allCompanies = new ArrayList<>();
    private LiveData<List<Company>> allCompaniesLiveData;

    private CompanyAdapter companyAdapter;

    private RelativeLayout parentRelLayout;

    private boolean showingHelp = false;

    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);

        companiesRecView = findViewById(R.id.companiesRecView);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        btnAddCompany = findViewById(R.id.btnAddCompany);
        edtTxtAddCompany = findViewById(R.id.edtTxtAddCompany);
        parentRelLayout = findViewById(R.id.parentRelLayout);
        btnHelp = findViewById(R.id.btnHelp);



        initSnackbar();

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
                if(edtTxtAddCompany.getText().toString().equals(""))
                {
                    Toast.makeText(CompanyRegisterActivity.this, "Du måste skriva in företagets namn i fältet innan du klickar på lägg till", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(parentRelLayout, "Vill du lägga till företag " + edtTxtAddCompany.getText().toString() + "?", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();

                    AlertDialog.Builder addCompanyDialog = new AlertDialog.Builder(CompanyRegisterActivity.this)
                            .setTitle("Lägga till företag?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String companyToAdd = edtTxtAddCompany.getText().toString();
                                    Thread t = new Thread(new InsertSingleCompanyThread(companyToAdd));
                                    t.start();

                                    edtTxtAddCompany.setText("");

                                    if(edtTxtAddCompany.isFocused())
                                    {
                                        edtTxtAddCompany.clearFocus();
                                    }

                                    snackbar.dismiss();
                                }
                            })
                            .setNegativeButton("Nej", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    snackbar.dismiss();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    snackbar.dismiss();
                                }
                            });

                    addCompanyDialog.show();
                }
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if(!showingHelp)
                {
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


        /*btnPeriod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CompanyRegisterActivity.this, RegisteredDatesActivity.class);
                startActivity(intent);
            }
        });*/

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

    }

    private void initSnackbar()
    {
        // make some sentences bold
        final SpannableStringBuilder snackbarText = new SpannableStringBuilder("För att lägga till " +
                "ett nytt företag skriver du först namnet på företaget i textrutan" +
                " och sen trycker på lägg till.\n\n" +
                "Ändra namn eller ta bort ett företag?\nOm du klickar på ett företagsnamn i detta fönster kommer du till ett nytt fönster " +
                "som visar info om företaget och detta fönster kan du även ta bort eller ändra namn " +
                "på företaget.");
        final StyleSpan bold = new StyleSpan(Typeface.BOLD);
        snackbarText.setSpan(bold, 115, 154, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        snackbar = Snackbar.make(parentRelLayout, snackbarText, Snackbar.LENGTH_INDEFINITE)
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
                        break;
                    case R.id.calendar:
                        Intent mainIntent = new Intent(CompanyRegisterActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case R.id.register:
                        Toast.makeText(CompanyRegisterActivity.this,
                                "Du måste först gå till kalendern och välja ett datum innan du kan registrera nya tider", Toast.LENGTH_SHORT).show();
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