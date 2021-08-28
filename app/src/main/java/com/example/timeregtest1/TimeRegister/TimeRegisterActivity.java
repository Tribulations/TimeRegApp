package com.example.timeregtest1.TimeRegister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyAdapter;
import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.CompanyDatabase.CompanyDao;
import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity;
import com.example.timeregtest1.DateSelectedFragment;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.timeregtest1.MainActivity.CURRENT_DAY_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_MONTH_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_YEAR_KEY;
import static com.example.timeregtest1.selectedDate.DateSelectedActivity.SELECTED_DATE_KEY;

// trying to implement an interface to use callback to let this activity be notified when an company name in the recview has been clicked

public class TimeRegisterActivity extends AppCompatActivity implements CompanyAdapter.CompanyNameClicked, View.OnClickListener
{
    private static final String TAG = "TimeRegisterActivity";

    // TODO: 2021-08-15 Change the calendar visuals when a date has data registered eg. if som work is registered in the 15/8 that date should have a different color.
    

    // get the id of the companyname tha was clicked
    private int firstCompanyId = -1, secondCompanyId = -1, thirdCompanyId = -1;
    private int companyId;
    private int y = 0, m = 0, d = 0;
    private Company companyById = new Company("default");

    // set the edittext text to the company name that is clicked in the recview
    @Override
    public void onCompanyNameClicked(String companyName , int id)
    {
        if(edtTxtCompany.isFocused())
        {
            edtTxtCompany.setText(formatCompanyName(companyName));
            edtTxtCompany.clearFocus();
            chooseCompanyRecView.setVisibility(View.GONE);
            frameLayoutRelView.setVisibility(View.VISIBLE);
            firstCompanyId = id;
            companyId = id;
        }
        else if(secondEdtTxtCompany.isFocused())
        {
            secondEdtTxtCompany.setText(formatCompanyName(companyName));
            secondEdtTxtCompany.clearFocus();
            chooseCompanyRecView.setVisibility(View.GONE);
            secondCompanyId = id;
        }
        else if(thirdEdtTxtCompany.isFocused())
        {
            thirdEdtTxtCompany.setText(formatCompanyName(companyName));
            thirdEdtTxtCompany.clearFocus();
            chooseCompanyRecView.setVisibility(View.GONE);
            thirdCompanyId = id;
        }
    }

    private ScrollView timeRegScrollView;
    private RelativeLayout timeRegParentRelLayout, timeRegRelLayout, firstTimeRegRelLayout, secondTimeRegRelLayout;
    private EditText edtTxtCompany, edtTxtTime, secondEdtTxtCompany, secondEdtTxtTime, thirdEdtTxtCompany, thirdEdtTxtTime;
    /*private ImageView btnAccepted, btnNotAccepted, secondBtnAccepted, secondBtnNotAccepted, thirdBtnAccepted, thirdBtnNotAccepted;*/
    private RecyclerView chooseCompanyRecView;
    private ImageView btnAddInputField;
    private TextView txtSelectedDate;

    private Bundle bundle = new Bundle();

    private RelativeLayout timeRegConLayout, constraintLayout;

    private ArrayList<Company> allCompanies;

    private LiveData<List<Company>> allCompaniesLiveData;

    private ArrayList<DateReg> allDateRegs = new ArrayList<>();
    private LiveData<List<DateReg>> allDateRegsLiveData;

    private ArrayList<Company> searchedCompanies;

    private CompanyAdapter companyAdapter;

    private BottomNavigationView bottomNavigationView;

    private RelativeLayout frameLayoutRelView;

    /*private FrameLayout timeRegFrameLayout;*/

    // only make the clicklistneers here with a swtich but doesnt work atr the moment
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.edtTxtCompany:
                if(edtTxtCompany.getText().toString().equals(""))
                {
                    //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                    Runnable g = new GetAllCompaniesThread();
                    Thread thread = new Thread(g);
                    thread.start();

                    while(thread.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    companyAdapter.setAllCompanies(allCompanies);
                    chooseCompanyRecView.setVisibility(View.VISIBLE);
                    frameLayoutRelView.setVisibility(View.GONE);

                }
                break;
            /*case R.id.secondEdtTxtCompany:
                if(secondEdtTxtCompany.getText().toString().equals(""))
                {
                    //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                    Runnable g = new GetAllCompaniesThread();
                    Thread thread = new Thread(g);
                    thread.start();

                    while(thread.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    companyAdapter.setAllCompanies(allCompanies);
                    chooseCompanyRecView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.secondEdtTxtTime:
                if(secondEdtTxtCompany.getText().toString().equals(""))
                {
                    //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                    Runnable g = new GetAllCompaniesThread();
                    Thread thread = new Thread(g);
                    thread.start();

                    while(thread.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    companyAdapter.setAllCompanies(allCompanies);
                    chooseCompanyRecView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.thirdEdtTxtCompany:
                if(thirdEdtTxtCompany.getText().toString().equals(""))
                {
                    //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                    Runnable g = new GetAllCompaniesThread();
                    Thread thread = new Thread(g);
                    thread.start();

                    while(thread.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    companyAdapter.setAllCompanies(allCompanies);
                    chooseCompanyRecView.setVisibility(View.VISIBLE);
                }
                break;*/
            case R.id.btnAddInputField:// use this as save to database button?
                // check if the inputted companies exist
                if(edtTxtCompany.getText().toString().equals("") || edtTxtTime.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Fyll i företagets namn och tid innan du försöker lägga till en post!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ArrayList<String> companyNames = new ArrayList<>();
                    ArrayList<Float> companyHours = new ArrayList<>();
                    companyNames.add(edtTxtCompany.getText().toString());
                    companyHours.add(Float.valueOf(edtTxtTime.getText().toString()));

                    // TODO: 2021-08-24  change this code for adding a company!!
                    // ugly so change later
                    ArrayList<Integer> ids = new ArrayList<>();
                    if(firstCompanyId != -1)
                    {
                        ids.add(firstCompanyId);
                    }

                    int i = 0;
                    for(String name : companyNames)
                    {

                        if(!name.equals(""))
                        {
                            Thread t = new Thread(new GetCompanyByIdThread(ids.get(i))); // TODO: 2021-08-13 Maybe just add one company at a time in this activity? and when add button is clicked just clear the fields. and use a fragment to show whats already registered on the current date. which can be shown and hidden with a button click
                            t.start();

                            while(t.isAlive())
                            {
                                SystemClock.sleep(10);
                            }

                            Calendar currentDate = Calendar.getInstance();
                            currentDate.set(y, m, d, 12, 0, 0); // the month is counted from 0

                            DateReg dateReg = new DateReg(y, m, d, companyById.getCompanyName(), companyHours.get(i), currentDate.getTimeInMillis(), companyId); // Adding m + 1to get the String type date in the DateRag class to display month correct. (month is counted from 0)

                            // add company to database
                            Thread t2 = new Thread(new InsertDateRegThread(dateReg));
                            t2.start();

                            // shouldnt need to wait like this but doing it know for safety?
                            while(t2.isAlive())
                            {
                                SystemClock.sleep(10);
                            }

                            i++;
                        }
                    }

                    Toast.makeText(this, "Tid tillagd!", Toast.LENGTH_SHORT).show();
                }

                break;
            /*case R.id.secondBtnNotAccepted:
                if(constraintLayout.getVisibility() == View.GONE && secondBtnNotAccepted.getVisibility() == View.VISIBLE)
                {
                    constraintLayout.setVisibility(View.VISIBLE);
                }
                secondBtnNotAccepted.setVisibility(View.GONE);
                secondBtnAccepted.setVisibility(View.VISIBLE);
                break;
            case R.id.secondBtnAccepted:
                secondBtnNotAccepted.setVisibility(View.VISIBLE);
                secondBtnAccepted.setVisibility(View.GONE);
                break;
            case R.id.thirdBtnNotAccepted:
                thirdBtnNotAccepted.setVisibility(View.GONE);
                thirdBtnAccepted.setVisibility(View.VISIBLE);
                break;
            case R.id.thirdBtnAccepted:
                thirdBtnNotAccepted.setVisibility(View.VISIBLE);
                thirdBtnAccepted.setVisibility(View.GONE);
                break;*/
            default:
                break;



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: called");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_register);

        initViews();
        initBottomNavView();

        // get the date that was clicked
        //Bundle bundle = getIntent().getExtras();
        /*Bundle bundle = getIntent().getBundleExtra(SELECTED_DATE_KEY);*/
        bundle = getIntent().getExtras();
        /*String currentDate;*/
        if(bundle != null)
        {
            y = bundle.getInt(CURRENT_YEAR_KEY);
            m = bundle.getInt(CURRENT_MONTH_KEY);
            d = bundle.getInt(CURRENT_DAY_KEY); // the calendar starts to count the months from zero
            /*currentDate = String.valueOf(y) + "-" + formatDateInt(++m) + "-" + formatDateInt(d);*/
            txtSelectedDate.setText(String.valueOf(y) + "-" + formatDateInt(m + 1) + "-" + formatDateInt(d)); // the month is counted from 0, therefore it is incremented here for displaying
        }


        initFragmentTransaction(y, m, d);

        // TODO: 2021-08-24 I should just use Calendar and not the string type date in the DateReg class. I should just use Calendar and maybe Date and convert with these

        //initCompaniesArray();
        //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(this).companyDao().getAllCompanies();

        //test to do the above with thread

        // do this or the 2 lines on row 100 and 101 ?
        /*Runnable g = new GetAllCompaniesThread();
        Thread thread = new Thread(g);
        thread.start();*/

        // is this better then the above to not cause memory leaks?
        Thread thread = new Thread(new GetAllCompaniesThread());
        thread.start(); // when is this thread destroyed?


        /*while(thread.isAlive())
        {
            SystemClock.sleep(10);
        }*/



        /*GetAllCompaniesLiveData g2 = new GetAllCompaniesLiveData();
        Thread thread2 = new Thread(g2);
        g2.start();

        while(thread2.isAlive())
        {
            SystemClock.sleep(10);
        }*/

        // when a change eg. a new company is added refresh the recyclerview
        allCompaniesLiveData = CompanyDatabase.getInstance(this).companyDao().getAllCompaniesLiveData();
        allCompaniesLiveData.observe(this, new Observer<List<Company>>()
        {
            @Override
            public void onChanged(List<Company> companies)
            {
                //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                /*Runnable g = new GetAllCompaniesThread();
                Thread thread = new Thread(g); // memory leak? se line 351
                thread.start();*/


                companyAdapter.setAllCompanies(allCompanies);
            }
        });

        companyAdapter = new CompanyAdapter(this);
        //companyAdapter.setAllCompanies(allCompanies); // just need this line in the onChanged() of the livedata it appears
        chooseCompanyRecView.setAdapter(companyAdapter);
        chooseCompanyRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));



        /*btnNotAccepted.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnNotAccepted.setVisibility(View.GONE);
                btnAccepted.setVisibility(View.VISIBLE);
            }
        });

        btnAccepted.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnNotAccepted.setVisibility(View.VISIBLE);
                btnAccepted.setVisibility(View.GONE);
            }
        });*/

        /*edtTxtCompany.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(edtTxtCompany.getText().toString().equals(""))
                {
                    //allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();

                    Runnable g = new GetAllCompaniesThread();
                    Thread thread = new Thread(g);
                    thread.start();

                    while(thread.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    companyAdapter.setAllCompanies(allCompanies);
                    chooseCompanyRecView.setVisibility(View.VISIBLE);
                }
            }
        });*/

        edtTxtCompany.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                initSearch(edtTxtCompany);
                chooseCompanyRecView.setVisibility(View.VISIBLE);
                frameLayoutRelView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        /*secondEdtTxtCompany.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                initSearch(secondEdtTxtCompany);
                chooseCompanyRecView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        thirdEdtTxtCompany.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                initSearch(thirdEdtTxtCompany);
                chooseCompanyRecView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });*/

        // hide the keyboard when enter is pressed
        /*edtTxtCompany.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER )
                {
                    // hide the virtual keyboard
                    InputMethodManager imm = (InputMethodManager) TimeRegisterActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtTxtCompany.getWindowToken(), 0);
                    return true;
                }

                String textInput = edtTxtCompany.getText().toString();
                createNewCompany(textInput);

                return false;
            }
        });*/

        timeRegConLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseCompanyRecView.setVisibility(View.GONE);
                frameLayoutRelView.setVisibility(View.VISIBLE);
            }
        });

        // TODO: 2021-08-04  make a search function with the recview showing all the companies. so when you click the whole ordered list of  
        // TODO: 2021-08-04 companies are shown and if you write something it will instanstly search and show the companies mathing the search. Create a roomdatabase for this

        // use the add button to add another inpout field

        // to use the onClick method this is needed
        initClickListeners();

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

    private void initClickListeners()
    {
        edtTxtCompany.setOnClickListener(this);
        /*secondEdtTxtCompany.setOnClickListener(this);
        thirdEdtTxtCompany.setOnClickListener(this);*/

        edtTxtTime.setOnClickListener(this);
        /*secondEdtTxtTime.setOnClickListener(this);*/

        /*btnAccepted.setOnClickListener(this);
        btnNotAccepted.setOnClickListener(this);
        secondBtnAccepted.setOnClickListener(this);
        secondBtnNotAccepted.setOnClickListener(this);
        thirdBtnAccepted.setOnClickListener(this);
        thirdBtnNotAccepted.setOnClickListener(this);*/

        btnAddInputField.setOnClickListener(this);

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

    private void initViews()
    {
        Log.d(TAG, "initViews: called");
        
        timeRegScrollView = findViewById(R.id.timeRegScrollView);
        timeRegRelLayout = findViewById(R.id.timeRegRelLayout);
        edtTxtCompany = findViewById(R.id.edtTxtCompany);
        edtTxtTime = findViewById(R.id.edtTxtTime);
        /*btnAccepted = findViewById(R.id.btnAccepted);
        btnNotAccepted = findViewById(R.id.btnNotAccepted);*/

        /*secondEdtTxtCompany = findViewById(R.id.secondEdtTxtCompany);
        secondEdtTxtTime = findViewById(R.id.secondEdtTxtTime);
        secondBtnAccepted = findViewById(R.id.secondBtnAccepted);
        secondBtnNotAccepted = findViewById(R.id.secondBtnNotAccepted);


        thirdBtnAccepted = findViewById(R.id.thirdBtnAccepted);
        thirdBtnNotAccepted = findViewById(R.id.thirdBtnNotAccepted);
        thirdEdtTxtCompany = findViewById(R.id.thirdEdtTxtCompany);
        thirdEdtTxtTime = findViewById(R.id.thirdEdtTxtTime);*/

        chooseCompanyRecView = findViewById(R.id.chooseCompayRecView);

        timeRegConLayout = findViewById(R.id.timeRegConLayout);

        /*constraintLayout = findViewById(R.id.constraintLayout);*/

        btnAddInputField = findViewById(R.id.btnAddInputField);

        firstTimeRegRelLayout = findViewById(R.id.firstTimeRegRelLayout);
        /*secondTimeRegRelLayout = findViewById(R.id.secondTimeRegRelLayout);

        secondEdtTxtCompany = findViewById(R.id.secondEdtTxtCompany);*/

        timeRegParentRelLayout = findViewById(R.id.timeRegParentRelView);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);

        bottomNavigationView = findViewById(R.id.bottomNavView);

        frameLayoutRelView = findViewById(R.id.frameLayoutRelView);

        /*timeRegFrameLayout = findViewById(R.id.timeRegFrameLayout);*/
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
                        // TODO: 2021-08-09 Use livedata so that the list is filled dynamically if all companies haven't loaded from the database
                        Intent companiesIntent = new Intent(TimeRegisterActivity.this, CompanyRegisterActivity.class);
                        startActivity(companiesIntent);
                        break;
                    case R.id.calendar:
                        Intent mainIntent = new Intent(TimeRegisterActivity.this, MainActivity.class);
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

    // trim the companies name if it's to long to fit the recview
    private String formatCompanyName(String name)
    {
        if(name.length() >= 9)
        {
            name = name.substring(0, 8).trim();
            name = name + ".";
        }

        return name;
    }

    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBackPressed: called");

        Intent intent = new Intent(TimeRegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void initFragmentTransaction(int y, int m, int d)
    {
        final DateSelectedFragment dateSelectedFragment = new DateSelectedFragment();

        bundle.putInt(CURRENT_YEAR_KEY, y);
        bundle.putInt(CURRENT_MONTH_KEY, m);
        bundle.putInt(CURRENT_DAY_KEY, d);
        dateSelectedFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.timeRegFrameLayout, dateSelectedFragment);
        transaction.commit();
    }

    public class GetAllCompaniesThread implements Runnable
    {
        private static final String TAG = "GetAllCompaniesThread";

        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            allCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompanies();
        }

    }

    /*public class GetAllCompaniesLiveDataThread implements Runnable
    {
        private static final String TAG = "GetAllCompaniesLiveData";

        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            allCompaniesLiveData = CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getAllCompaniesLiveData();

        }

    }*/

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

            searchedCompanies = (ArrayList<Company>) CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().searchForCompany(enteredText);
        }

    }

    public class InsertSingleCompanyThread implements Runnable
    {
        private static final String TAG = "InsertSingleCompanyThred";

        private String companyName;
        private int timeWorked;

        public InsertSingleCompanyThread(String companyName, int timeWorked)
        {
            this.companyName = companyName;
            this.timeWorked = timeWorked;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void run()
        {
            Log.d(TAG, "run: called");
            //super.run();

            CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().insertSingleCompany(companyName);

            // TODO: 2021-08-06 Keep working on the registration of a new company and its worked time.
        }

    }

    public class GetCompanyByIdThread implements Runnable
    {
        private static final String TAG = "GetCompanyByIdThread";

        private int id;

        public GetCompanyByIdThread(int id)
        {
            this.id = id;
        }

        @SuppressLint({"LongLogTag"})
        @Override
        public void run()
        {
            Log.d(TAG, "run: ");
            //super.run();

            companyById = CompanyDatabase.getInstance(TimeRegisterActivity.this).companyDao().getCompanyById(id);
        }

    }

    public class InsertDateRegThread implements Runnable
    {
        private static final String TAG = "InsertDateRegThread";

        private DateReg dateReg;

        public InsertDateRegThread(DateReg dateReg)
        {
            this.dateReg = dateReg;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void run()
        {
            Log.d(TAG, "run: called");

            CompanyDatabase.getInstance(TimeRegisterActivity.this).dateRegDao().insert(dateReg);
        }
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
            allDateRegs = (ArrayList<DateReg>) CompanyDatabase.getInstance(TimeRegisterActivity.this).dateRegDao().getSelectedDatesData(mYear, mMonth, mDay);
        }
    }*/
}