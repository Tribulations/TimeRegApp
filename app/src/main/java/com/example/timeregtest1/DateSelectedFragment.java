package com.example.timeregtest1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.selectedDate.DateRegsAdapter;
import com.example.timeregtest1.selectedDate.DateSelectedActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.timeregtest1.MainActivity.CURRENT_DAY_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_MONTH_KEY;
import static com.example.timeregtest1.MainActivity.CURRENT_YEAR_KEY;

public class DateSelectedFragment extends Fragment
{
    private static final String TAG = "DateSelectedFragment";

    private ArrayList<DateReg> allDateRegs = new ArrayList<>();
    private LiveData<List<DateReg>> allDateRegsLiveData;
    private RecyclerView dateRegRecView;
    private DateRegsAdapter dateRegsAdapter;
    //private TextView txtSelectedDate;

    int y = 0, m = 0, d = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView: called");

        View view = inflater.inflate(R.layout.fragment_date_selected, container, false);

        initViews(view);

        // init allDateRegs with thread
        // get the date that was clicked
        Bundle bundle = getArguments();
        String currentDate;

        if(bundle != null)
        {
            y = bundle.getInt(CURRENT_YEAR_KEY);
            m = bundle.getInt(CURRENT_MONTH_KEY);
            d = bundle.getInt(CURRENT_DAY_KEY); // the calendar starts to count the months from zero
        }

        Thread t = new Thread(new GetAllDateRegsThread(y, m, d));
        /*t.start();
        while (t.isAlive())
        {
            SystemClock.sleep(10);
        }*/




        dateRegsAdapter = new DateRegsAdapter();
        dateRegRecView.setAdapter(dateRegsAdapter);
        dateRegRecView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));



        allDateRegsLiveData = CompanyDatabase.getInstance(getActivity()).dateRegDao().getSelectedDateLiveData(y, m, d);
        /*int finalY = y;
        int finalM = m;
        int finalD = d;*/
        allDateRegsLiveData.observe(getActivity(), new Observer<List<DateReg>>()
        {
            @Override
            public void onChanged(List<DateReg> dateRegs)
            {
                /*Thread thread = new Thread(new GetAllDateRegsThread(finalY, finalM, finalD));
                thread.start();*/

                ArrayList<DateReg> newDateRegs = (ArrayList<DateReg>) dateRegs;

                dateRegsAdapter.setAllDateRegs(newDateRegs);
            }
        });


        dateRegsAdapter.setAllDateRegs(allDateRegs);

        return view;
    }

    private void initViews(View view)
    {
        dateRegRecView = view.findViewById(R.id.dateRegRecView);
        //txtSelectedDate = view.findViewById(R.id.txtSelectedDateDSA);
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

            allDateRegs = (ArrayList<DateReg>) CompanyDatabase.getInstance(getActivity()).dateRegDao().getSelectedDatesData(y, m, d);
        }

    }
}
