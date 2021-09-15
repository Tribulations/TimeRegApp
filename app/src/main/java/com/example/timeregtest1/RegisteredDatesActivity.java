package com.example.timeregtest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;
import com.example.timeregtest1.selectedDate.DateRegsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegisteredDatesActivity extends AppCompatActivity implements CompanyAdapter.CompanyNameClicked, RegDatesAdapter.RegDateClicked
{


    private static final String TAG = "RegisteredDatesActivity";
    
    private ArrayList<DateReg> allDateRegs = new ArrayList<>();
    private LiveData<List<DateReg>> allDateRegsLiveData;

    private ArrayList<Company> allCompanies = new ArrayList<>();
    private LiveData<List<Company>> allCompaniesLiveData;

    private ArrayList<Company> searchedCompanies;

    private Button btnSelectPeriod, btnHelp;
    private RecyclerView dateRegsRecView, chooseCompanyRecView;

    private EditText edtTxtNameToSearch;

    private ImageButton btnCloseWindow;

    private TextView txtSumTimeWorked, txtDateInterval, txtLongNote;

    private RegDatesAdapter dateRegsAdapter;
    private CompanyAdapter companyAdapter;

    private RelativeLayout parentRelLayout, totTimeRelLayout, noteWindowLayout;

    private int companyId = -1;

    private float timeWorkedSum = 0.0f;

    private BottomNavigationView bottomNavigationView;

    private boolean showingHelp = false;

    private Snackbar snackbar;

    @Override
    public void onRegDateClicked(String regDateNote)
    {
        noteWindowLayout.setVisibility(View.VISIBLE);
        txtLongNote.setText(regDateNote);
    }

    @Override
    public void onCompanyNameClicked(String companyName, int id)
    {
        companyId = id;

        edtTxtNameToSearch.setText(formatCompanyName(companyName));
        edtTxtNameToSearch.clearFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_dates);

        initViews();

        initBottomNavView();

        initSnackbar();

        companyAdapter = new CompanyAdapter(this);
        chooseCompanyRecView.setAdapter(companyAdapter);
        chooseCompanyRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        Thread thread = new Thread(new GetAllCompaniesThread());
        thread.start();

        allCompaniesLiveData = CompanyDatabase.getInstance(this).companyDao().getAllCompaniesLiveData();
        allCompaniesLiveData.observe(this, new Observer<List<Company>>()
        {
            @Override
            public void onChanged(List<Company> companies)
            {
                //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                Thread thread = new Thread(new GetAllCompaniesThread());
                thread.start();

                companyAdapter.setAllCompanies(allCompanies);
            }
        });

        edtTxtNameToSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dateRegsRecView.setVisibility(View.GONE);
                totTimeRelLayout.setVisibility(View.GONE);
                txtDateInterval.setVisibility(View.GONE);
                chooseCompanyRecView.setVisibility(View.VISIBLE);
            }

        });

        edtTxtNameToSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                initSearch(edtTxtNameToSearch);
                chooseCompanyRecView.setVisibility(View.VISIBLE);
                dateRegsRecView.setVisibility(View.GONE);
                totTimeRelLayout.setVisibility(View.GONE);
                txtDateInterval.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        /*btnCompanyNameToSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(edtTxtNameToSearch.getText().toString().equals(""))
                {
                    Toast.makeText(RegisteredDatesActivity.this, "Du behöver välja kund innan du klickar på knappen", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    MaterialDatePicker rangePicker = MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Välj tidsperiod")
                            .build();

                    rangePicker.show(getSupportFragmentManager(), "tag");

                    rangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>()
                    {
                        @Override
                        public void onPositiveButtonClick(Pair<Long, Long> selection)
                        {
                            Calendar startDate = Calendar.getInstance();
                            Calendar endDate = Calendar.getInstance();
                            startDate.setTimeInMillis(selection.first);
                            endDate.setTimeInMillis(selection.second);

                            final int sYear, sMonth, sDay, eYear, eMonth, eDay;

                            sYear = startDate.get(Calendar.YEAR);
                            sMonth = startDate.get(Calendar.MONTH) + 1;
                            sDay = startDate.get(Calendar.DAY_OF_MONTH);
                            eYear = endDate.get(Calendar.YEAR);
                            eMonth = endDate.get(Calendar.MONTH) + 1;
                            eDay = endDate.get(Calendar.DAY_OF_MONTH);

                            Thread thread = new Thread(new GetAllDateRegsInPeriodByCompanyIdThread(selection.first, selection.second, companyId));
                            thread.start();

                            allDateRegsLiveData = CompanyDatabase.getInstance(RegisteredDatesActivity.this).dateRegDao().getAllDateRegsInPeriodByCompanyIdLiveData(selection.first, selection.second, companyId);
                            allDateRegsLiveData.observe(RegisteredDatesActivity.this, new Observer<List<DateReg>>()
                            {
                                @Override
                                public void onChanged(List<DateReg> dateRegs)
                                {
                                    Thread thread = new Thread(new GetAllDateRegsInPeriodThread(selection.first, selection.second));
                                    thread.start();

                                    if(allDateRegs != null && allDateRegs.size() > 0)
                                    {
                                        dateRegsAdapter.setAllDateRegs(allDateRegs);
                                        dateRegsRecView.setVisibility(View.VISIBLE);
                                        totTimeRelLayout.setVisibility(View.VISIBLE);
                                        txtDateInterval.setVisibility(View.VISIBLE);

                                        timeWorkedSum = 0.0f;
                                        calcSumTimeWorked();

                                        txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));

                                    }
                                    else
                                    {
                                        dateRegsRecView.setVisibility(View.GONE);
                                        totTimeRelLayout.setVisibility(View.GONE);
                                        txtDateInterval.setVisibility(View.GONE);
                                        Toast.makeText(RegisteredDatesActivity.this, "Det finns inga registrerade tider under det valda intervallet!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            chooseCompanyRecView.setVisibility(View.GONE);

                            timeWorkedSum = 0.0f;
                            calcSumTimeWorked();

                            txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));
                            txtDateInterval.setText(String.valueOf(sYear) + "-" + String.valueOf(formatDateInt(sMonth)) + "-" +
                                    String.valueOf(formatDateInt(sDay)) + " till " + String.valueOf(eYear) + "-" + String.valueOf(formatDateInt(eMonth)) + "-" + String.valueOf(formatDateInt(eDay)));
                        }
                    });
                }
            }
        });*/

        /*btnSelectPeriod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MaterialDatePicker rangePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Välj tidsperiod")
                        .build();

                rangePicker.show(getSupportFragmentManager(), "tag");

                rangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>()
                {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection)
                    {
                        Calendar startDate = Calendar.getInstance();
                        Calendar endDate = Calendar.getInstance();
                        startDate.setTimeInMillis(selection.first);
                        endDate.setTimeInMillis(selection.second);

                        final int sYear, sMonth, sDay, eYear, eMonth, eDay;

                        sYear = startDate.get(Calendar.YEAR);
                        sMonth = startDate.get(Calendar.MONTH) + 1;
                        sDay = startDate.get(Calendar.DAY_OF_MONTH);
                        eYear = endDate.get(Calendar.YEAR);
                        eMonth = endDate.get(Calendar.MONTH) + 1;
                        eDay = endDate.get(Calendar.DAY_OF_MONTH);

                        Thread thread = new Thread(new GetAllDateRegsInPeriodThread(selection.first, selection.second));
                        thread.start();

                        allDateRegsLiveData = CompanyDatabase.getInstance(RegisteredDatesActivity.this).dateRegDao().getAllDateRegsInPeriodLiveData(selection.first, selection.second);
                        allDateRegsLiveData.observe(RegisteredDatesActivity.this, new Observer<List<DateReg>>()
                        {
                            @Override
                            public void onChanged(List<DateReg> dateRegs)
                            {
                                Thread thread = new Thread(new GetAllDateRegsInPeriodThread(selection.first, selection.second));
                                thread.start();

                                if(allDateRegs != null && allDateRegs.size() > 0)
                                {
                                    dateRegsAdapter.setAllDateRegs(allDateRegs);
                                    dateRegsRecView.setVisibility(View.VISIBLE);
                                    totTimeRelLayout.setVisibility(View.VISIBLE);
                                    txtDateInterval.setVisibility(View.VISIBLE);

                                    timeWorkedSum = 0.0f;
                                    calcSumTimeWorked();

                                    txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));
                                }
                                else
                                {
                                    dateRegsRecView.setVisibility(View.GONE);
                                    totTimeRelLayout.setVisibility(View.GONE);
                                    txtDateInterval.setVisibility(View.GONE);
                                    Toast.makeText(RegisteredDatesActivity.this, "Det finns inga registrerade tider under det valda intervallet!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        chooseCompanyRecView.setVisibility(View.GONE);


                        timeWorkedSum = 0.0f;
                        calcSumTimeWorked();

                        txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));
                        txtDateInterval.setText(String.valueOf(sYear) + "-" + String.valueOf(formatDateInt(sMonth)) + "-" +
                                String.valueOf(formatDateInt(sDay)) + " till " + String.valueOf(eYear) + "-" + String.valueOf(formatDateInt(eMonth)) + "-" + String.valueOf(formatDateInt(eDay)));
                    }
                });
            }
        });*/

        btnSelectPeriod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MaterialDatePicker rangePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Välj tidsperiod")
                        .build();

                rangePicker.show(getSupportFragmentManager(), "tag");

                rangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>()
                {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection)
                    {
                        Calendar startDate = Calendar.getInstance();
                        Calendar endDate = Calendar.getInstance();
                        startDate.setTimeInMillis(selection.first);
                        endDate.setTimeInMillis(selection.second);

                        final int sYear, sMonth, sDay, eYear, eMonth, eDay;

                        sYear = startDate.get(Calendar.YEAR);
                        sMonth = startDate.get(Calendar.MONTH) + 1;
                        sDay = startDate.get(Calendar.DAY_OF_MONTH);
                        eYear = endDate.get(Calendar.YEAR);
                        eMonth = endDate.get(Calendar.MONTH) + 1;
                        eDay = endDate.get(Calendar.DAY_OF_MONTH);

                        if(!edtTxtNameToSearch.getText().toString().equals("") && companyId != -1) // if the textfield is not empty, and a company has been clicked
                        {

                            Thread thread = new Thread(new GetAllDateRegsInPeriodByCompanyIdThread(selection.first, selection.second, companyId));
                            thread.start();

                            allDateRegsLiveData = CompanyDatabase.getInstance(RegisteredDatesActivity.this).dateRegDao().getAllDateRegsInPeriodByCompanyIdLiveData(selection.first, selection.second, companyId);
                            allDateRegsLiveData.observe(RegisteredDatesActivity.this, new Observer<List<DateReg>>()
                            {
                                @Override
                                public void onChanged(List<DateReg> dateRegs)
                                {
                                            /*Thread thread = new Thread(new GetAllDateRegsInPeriodThread(selection.first, selection.second));
                                            thread.start();*/

                                    if(allDateRegs != null && allDateRegs.size() > 0)
                                    {
                                        dateRegsAdapter.setAllDateRegs(allDateRegs);
                                        dateRegsRecView.setVisibility(View.VISIBLE);
                                        totTimeRelLayout.setVisibility(View.VISIBLE);
                                        txtDateInterval.setVisibility(View.VISIBLE);

                                        timeWorkedSum = 0.0f;
                                        calcSumTimeWorked();

                                        txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));

                                    }
                                    else
                                    {
                                        dateRegsRecView.setVisibility(View.GONE);
                                        totTimeRelLayout.setVisibility(View.GONE);
                                        txtDateInterval.setVisibility(View.GONE);
                                        Toast.makeText(RegisteredDatesActivity.this, "Det finns inga registrerade tider under det valda intervallet!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            chooseCompanyRecView.setVisibility(View.GONE);

                            timeWorkedSum = 0.0f;
                            calcSumTimeWorked();

                            txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));
                            txtDateInterval.setText(String.valueOf(sYear) + "-" + String.valueOf(formatDateInt(sMonth)) + "-" +
                                    String.valueOf(formatDateInt(sDay)) + " till " + String.valueOf(eYear) + "-" + String.valueOf(formatDateInt(eMonth)) + "-" + String.valueOf(formatDateInt(eDay)));
                        }
                        else
                        {
                            Thread thread = new Thread(new GetAllDateRegsInPeriodThread(selection.first, selection.second));
                            thread.start();

                            allDateRegsLiveData = CompanyDatabase.getInstance(RegisteredDatesActivity.this).dateRegDao().getAllDateRegsInPeriodLiveData(selection.first, selection.second);
                            allDateRegsLiveData.observe(RegisteredDatesActivity.this, new Observer<List<DateReg>>()
                            {
                                @Override
                                public void onChanged(List<DateReg> dateRegs)
                                {
                                /*Thread thread = new Thread(new GetAllDateRegsInPeriodThread(selection.first, selection.second));
                                thread.start();*/

                                    if(dateRegs != null && dateRegs.size() > 0)
                                    {
                                        dateRegsAdapter.setAllDateRegs((ArrayList<DateReg>) dateRegs);
                                        dateRegsRecView.setVisibility(View.VISIBLE);
                                        totTimeRelLayout.setVisibility(View.VISIBLE);
                                        txtDateInterval.setVisibility(View.VISIBLE);

                                        timeWorkedSum = 0.0f;
                                        calcSumTimeWorked();

                                        txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));
                                    }
                                    else
                                    {
                                        dateRegsRecView.setVisibility(View.GONE);
                                        totTimeRelLayout.setVisibility(View.GONE);
                                        txtDateInterval.setVisibility(View.GONE);
                                        Toast.makeText(RegisteredDatesActivity.this, "Det finns inga registrerade tider under det valda intervallet!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            chooseCompanyRecView.setVisibility(View.GONE);

                            timeWorkedSum = 0.0f;
                            calcSumTimeWorked();

                            txtSumTimeWorked.setText(String.valueOf(timeWorkedSum));
                            txtDateInterval.setText(String.valueOf(sYear) + "-" + String.valueOf(formatDateInt(sMonth)) + "-" +
                                    String.valueOf(formatDateInt(sDay)) + " till " + String.valueOf(eYear) + "-" + String.valueOf(formatDateInt(eMonth)) + "-" + String.valueOf(formatDateInt(eDay)));
                        }

                    }
                });
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
        
        dateRegsAdapter = new RegDatesAdapter(this);
        dateRegsRecView.setAdapter(dateRegsAdapter);
        dateRegsRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        parentRelLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseCompanyRecView.setVisibility(View.GONE);
                dateRegsRecView.setVisibility(View.VISIBLE);
                totTimeRelLayout.setVisibility(View.VISIBLE);
                txtDateInterval.setVisibility(View.VISIBLE);
            }
        });

        btnCloseWindow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                noteWindowLayout.setVisibility(View.GONE);
            }
        });

    }

    private void initViews()
    {
        /*btnSelectPeriod = findViewById(R.id.btnSelectPeriod);
        btnCompanyNameToSearch = findViewById(R.id.btnCompanyNameToSearch);*/
        dateRegsRecView = findViewById(R.id.dateRegsRecView);
        chooseCompanyRecView = findViewById(R.id.companyNameRecView);
        edtTxtNameToSearch = findViewById(R.id.edtTxtNameToSearch);

        parentRelLayout = findViewById(R.id.parentRelLayout);

        txtSumTimeWorked = findViewById(R.id.txtSumTimeWorked);

        txtDateInterval = findViewById(R.id.txtDateInterval);

        bottomNavigationView = findViewById(R.id.bottomNavView);

        totTimeRelLayout = findViewById(R.id.totTimeRelLayout);

        btnSelectPeriod = findViewById(R.id.btnSelectPeriod);

        btnHelp = findViewById(R.id.btnHelp);

        noteWindowLayout = findViewById(R.id.noteWindowLayout);

        txtLongNote = findViewById(R.id.txtLongNote);

        btnCloseWindow = findViewById(R.id.btnCloseWindow);
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
                        Intent companiesIntent = new Intent(RegisteredDatesActivity.this, CompanyRegisterActivity.class);
                        startActivity(companiesIntent);
                        break;
                    case R.id.calendar:
                        Intent mainIntent = new Intent(RegisteredDatesActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case R.id.register:
                        Toast.makeText(RegisteredDatesActivity.this,
                                "Du måste först gå till kalendern och välja ett datum innan du kan registrera nya tider", Toast.LENGTH_SHORT).show();
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
        final SpannableStringBuilder snackbarText = new SpannableStringBuilder("För att visa " +
                "alla registrerade tider för ett visst företag så väljer man först företaget " +
                "och sedan klickar på knappen period för att i fönstret som kommer upp välja mellan " +
                "vilka datum perioden ska sträcka sig.\n\n" +
                "Visa alla företags tider under en viss period?\nOm du vill visa tider för alla " +
                "företag under en viss period behöver man se till att textfältet är tomt och " +
                "därefter klickar man på knappen period för att välja start och slut datum för " +
                "den period man vill visa.");

        // ge fetstil till en del av texten
        final StyleSpan bold = new StyleSpan(Typeface.BOLD);
        snackbarText.setSpan(bold, 205, 258, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

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

    private void initSearch(EditText editText)
    {
        if(!editText.getText().toString().equals(""))
        {
            String enteredText = "%" + editText.getText().toString() + "%";
            //ArrayList<Company> companies = (ArrayList<Company>) CompanyDatabase.getInstance(this).companyDao().searchForCompany(enteredText);

            Runnable g = new SearchForCompanyThread(enteredText);
            Thread thread = new Thread(g);
            thread.start();

            while(thread.isAlive())
            {
                SystemClock.sleep(10);
            }

            if(searchedCompanies != null)
            {
                companyAdapter.setAllCompanies(searchedCompanies);
            }
            else
            {
                // this doesn't show
                Toast.makeText(this, "Inget företag med det namnet hittades", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatCompanyName(String name)
    {
        if(name.length() >= 9)
        {
            name = name.substring(0, 8).trim();
            name = name + ".";
        }

        return name;
    }

    private void calcSumTimeWorked()
    {
        for(DateReg d : allDateRegs)
        {
            timeWorkedSum += d.getTimeWorked();
        }
    }

    // add a zero (0) to the month and dayOfMonth when showing the date as a string
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

    public class GetAllDateRegsInPeriodThread implements Runnable
    {
        private static final String TAG = "GetAllDateRegsInPeriodThread";

        private long timestampStart, timestampEnd;

        public GetAllDateRegsInPeriodThread(long timestampStart, long timestampEnd)
        {
            this.timestampStart = timestampStart;
            this.timestampEnd = timestampEnd;
        }

        @Override
        public void run()
        {
            allDateRegs = (ArrayList<DateReg>) CompanyDatabase.getInstance(RegisteredDatesActivity.this).dateRegDao().getAllDateRegsInPeriod(timestampStart, timestampEnd);
        }
    }

    public class GetAllDateRegsInPeriodByCompanyIdThread implements Runnable
    {
        private static final String TAG = "GetAllDateRegsInPeriodB";
        
        private long timestampStart, timestampEnd;
        private int companyId;

        public GetAllDateRegsInPeriodByCompanyIdThread(long timestampStart, long timestampEnd, int companyId)
        {
            this.timestampStart = timestampStart;
            this.timestampEnd = timestampEnd;
            this.companyId = companyId;
        }

        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            
            allDateRegs = (ArrayList<DateReg>) CompanyDatabase.getInstance(RegisteredDatesActivity.this).dateRegDao().getAllDateRegsInPeriodByCompanyId(timestampStart, timestampEnd, companyId);
        }
    }

    public class GetAllCompaniesThread implements Runnable
    {
        private static final String TAG = "GetAllCompaniesThread";

        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(RegisteredDatesActivity.this).companyDao().getAllCompanies();
        }

    }

    public class SearchForCompanyThread implements Runnable
    {
        private static final String TAG = "SearchForCompaniesThread";

        String enteredText;

        public SearchForCompanyThread(String enteredText)
        {
            this.enteredText = enteredText;
        }

        @SuppressLint({"LongLogTag"})
        @Override
        public void run()
        {
            Log.d(TAG, "run: ");
            //super.run();

            searchedCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(RegisteredDatesActivity.this).companyDao().searchForCompany(enteredText);
        }

    }

}