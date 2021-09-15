package com.example.timeregtest1.TimeRegister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.content.DialogInterface;
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
import com.example.timeregtest1.EditDialog;
import com.example.timeregtest1.MainActivity;
import com.example.timeregtest1.R;
import com.example.timeregtest1.selectedDate.DateRegsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.timeregtest1.MainActivity.CURRENT_DAY_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_MONTH_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_YEAR_KEY;
import static com.example.timeregtest1.selectedDate.DateSelectedActivity.SELECTED_DATE_KEY;

// trying to implement an interface to use callback to let this activity be notified when an company name in the recview has been clicked

public class TimeRegisterActivity extends AppCompatActivity implements CompanyAdapter.CompanyNameClicked, View.OnClickListener, DateRegsAdapter.DateRegClicked, EditDialog.EditDialogClicked
{
    private static final String TAG = "TimeRegisterActivity";

    // get the id of the companyname tha was clicked
    private int firstCompanyId = -1, secondCompanyId = -1, thirdCompanyId = -1;
    private int companyId = -1, dateRegId = -1;
    private float timeWorked = 0f;
    private String companyName = "";
    private int y = 0, m = 0, d = 0;
    private Company companyById = new Company("default");

    // used to keep track of when the user wants to update or delete a datereg post
    private boolean isRename = false;

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

    @Override
    public void onDateRegClick(int id, String companyN, float timeW, int cId)
    {
        dateRegId = id;
        companyName = companyN;
        timeWorked = timeW;
        companyId = cId;

        EditDialog editDialog = new EditDialog();
        editDialog.show(getSupportFragmentManager(), "Ändra något?");

    }

    // edit dialog
    @Override
    public void onRename()
    {
        isRename = true;

        Toast.makeText(this, "Ändra posten och tryck på lägga till knappen igen", Toast.LENGTH_SHORT).show();

        edtTxtCompany.setText(formatCompanyName(companyName));
        edtTxtTime.setText(String.valueOf(timeWorked));

        txtWarning.setVisibility(View.VISIBLE);
        txtWarning.setText("REDIGERAR POST: " + companyName + " " + String.valueOf(timeWorked));
    }

    // edit dialog
    @Override
    public void onDelete()
    {
        Snackbar snackbar = Snackbar.make(timeRegConLayout, "Vill du ta bort post: " + companyName + " " + String.valueOf(timeWorked) + " tim?", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();


        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(TimeRegisterActivity.this)
                .setTitle("Ta bort post?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Thread deleteThread = new Thread(new DeleteDateRegThread(dateRegId));
                        deleteThread.start();
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
                });
        deleteDialog.show();
    }

    private ScrollView timeRegScrollView;
    private RelativeLayout timeRegParentRelLayout, timeRegRelLayout, firstTimeRegRelLayout, secondTimeRegRelLayout;
    private EditText edtTxtCompany, edtTxtTime, edtTxtNote, secondEdtTxtCompany, thirdEdtTxtCompany;
    /*private ImageView btnAccepted, btnNotAccepted, secondBtnAccepted, secondBtnNotAccepted, thirdBtnAccepted, thirdBtnNotAccepted;*/
    private RecyclerView chooseCompanyRecView;
    private ImageView btnAddInputField;
    private TextView txtSelectedDate, txtWarning;

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

                if(isRename)
                {


                    Thread getCompanyThread = new Thread(new GetCompanyByIdThread(companyId));
                    getCompanyThread.start();

                    while (getCompanyThread.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    Thread t = new Thread(new UpdateDateRegThread(companyById.getCompanyName(), Float.valueOf(edtTxtTime.getText().toString()), dateRegId ,companyId));
                    t.start();

                    while(t.isAlive())
                    {
                        SystemClock.sleep(10);
                    }

                    Toast.makeText(this, "Posten ändrad", Toast.LENGTH_SHORT).show();

                    edtTxtCompany.setText("");
                    edtTxtTime.setText("");
                    if(edtTxtCompany.isFocused())
                    {
                        edtTxtCompany.clearFocus();
                    }
                    if(edtTxtTime.isFocused())
                    {
                        edtTxtTime.clearFocus();
                    }

                    chooseCompanyRecView.setVisibility(View.GONE);
                    frameLayoutRelView.setVisibility(View.VISIBLE);
                    txtWarning.setVisibility(View.GONE);

                    isRename = false;
                }
                else
                {
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
                                Thread t2 = new Thread(new GetCompanyByIdThread(ids.get(i)));
                                t2.start();

                                while(t2.isAlive())
                                {
                                    SystemClock.sleep(10);
                                }

                                Calendar currentDate = Calendar.getInstance();
                                currentDate.set(y, m, d, 12, 0, 0); // the month is counted from 0

                                DateReg dateReg = new DateReg(y, m, d, companyById.getCompanyName(), companyHours.get(i), currentDate.getTimeInMillis(), companyId, edtTxtNote.getText().toString());

                                // add company to database
                                Thread t3 = new Thread(new InsertDateRegThread(dateReg));
                                t3.start();

                                // shouldnt need to wait like this but doing it know for safety?
                                while(t3.isAlive())
                                {
                                    SystemClock.sleep(10);
                                }

                                i++;
                            }
                        }

                        Toast.makeText(this, "Tid tillagd!", Toast.LENGTH_SHORT).show();

                        // clear the fields
                        edtTxtCompany.setText("");
                        edtTxtTime.setText("");
                        edtTxtNote.setText("");

                        if (edtTxtCompany.isFocused())
                        {
                            edtTxtCompany.clearFocus();
                        }
                        if(edtTxtTime.isFocused())
                        {
                            edtTxtTime.clearFocus();
                        }
                        if(edtTxtNote.isFocused())
                        {
                            edtTxtNote.clearFocus();
                        }

                        frameLayoutRelView.setVisibility(View.VISIBLE);
                        chooseCompanyRecView.setVisibility(View.GONE);

                    }
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


        // is this better then the above to not cause memory leaks?
        Thread thread = new Thread(new GetAllCompaniesThread());
        thread.start(); // when is this thread destroyed?


        // when a change eg. a new company is added refresh the recyclerview
        allCompaniesLiveData = CompanyDatabase.getInstance(this).companyDao().getAllCompaniesLiveData();
        allCompaniesLiveData.observe(this, new Observer<List<Company>>()
        {
            @Override
            public void onChanged(List<Company> companies)
            {

                companyAdapter.setAllCompanies(allCompanies);
            }
        });

        companyAdapter = new CompanyAdapter(this);
        chooseCompanyRecView.setAdapter(companyAdapter);
        chooseCompanyRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));





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

        timeRegConLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseCompanyRecView.setVisibility(View.GONE);
                frameLayoutRelView.setVisibility(View.VISIBLE);
            }
        });

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

        edtTxtTime.setOnClickListener(this);

        btnAddInputField.setOnClickListener(this);

    }

    private void initSearch(EditText editText)
    {
        if(!editText.getText().toString().equals(""))
        {
            String enteredText = "%" + editText.getText().toString() + "%";

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
        edtTxtNote = findViewById(R.id.edtTxtNote);

        chooseCompanyRecView = findViewById(R.id.chooseCompayRecView);

        timeRegConLayout = findViewById(R.id.timeRegConLayout);

        btnAddInputField = findViewById(R.id.btnAddInputField);

        firstTimeRegRelLayout = findViewById(R.id.firstTimeRegRelLayout);

        timeRegParentRelLayout = findViewById(R.id.timeRegParentRelView);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);

        bottomNavigationView = findViewById(R.id.bottomNavView);

        frameLayoutRelView = findViewById(R.id.frameLayoutRelView);

        txtWarning = findViewById(R.id.txtWarning);
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

    public class UpdateDateRegThread implements Runnable
    {
        private String companyName;
        private float timeWorked;
        private int id, companyId;

        public UpdateDateRegThread(String companyName, float timeWorked, int id, int companyId)
        {
            this.companyName = companyName;
            this.timeWorked = timeWorked;
            this.id = id;
            this.companyId = companyId;
        }

        @Override
        public void run()
        {
            CompanyDatabase.getInstance(TimeRegisterActivity.this).dateRegDao().updateDateReg(companyName, timeWorked, id, companyId);
        }
    }

    public class DeleteDateRegThread implements Runnable
    {
        private int id;

        public DeleteDateRegThread(int id)
        {
            this.id = id;
        }

        @Override
        public void run()
        {
            CompanyDatabase.getInstance(TimeRegisterActivity.this).dateRegDao().deleteDateReg(id);
        }
    }

}