package com.example.timeregtest1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeregtest1.CompanyDatabase.DateReg;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RegDatesAdapter extends RecyclerView.Adapter<RegDatesAdapter.ViewHolder>
{
    private ArrayList<DateReg> allDateRegs = new ArrayList<>();

    @NonNull
    @Override
    public RegDatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reg_dates_list_item, parent, false);

        return new RegDatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegDatesAdapter.ViewHolder holder, int position)
    {
        // just show the first 17 letters of the company name
        String shortName = allDateRegs.get(position).getCompanyName();
        if(shortName.length() > 17)
        {
            shortName = shortName.substring(0, 16);
            shortName = shortName + ".";
        }



        holder.txtCompanyName.setText(shortName);
        holder.txtTimeWorked.setText(String.valueOf(allDateRegs.get(position).getTimeWorked()));
        /*holder.txtDate.setText(String.valueOf(allDateRegs.get(position).getYear()) + "-"
                + formatDate(allDateRegs.get(position).getMonth()) + "-" + formatDate(allDateRegs.get(position).getDay()));*/

        // use the timestamp instead of the string type to show the date
        /*Calendar date = Calendar.getInstance();
        date.setTimeInMillis(allDateRegs.get(position).getTimestamp());*/
        Date d = new Date();
        d.setTime(allDateRegs.get(position).getTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(d);

        holder.txtDate.setText(date);
    }

    @Override
    public int getItemCount()
    {
        return allDateRegs.size();
    }

    public ArrayList<DateReg> getAllDateRegs()
    {
        return allDateRegs;
    }

    public void setAllDateRegs(ArrayList<DateReg> allDateRegs)
    {
        this.allDateRegs = allDateRegs;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtCompanyName, txtTimeWorked, txtDate;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            txtCompanyName = itemView.findViewById(R.id.txtCompanyName);
            txtTimeWorked = itemView.findViewById(R.id.txtTimeWorked);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    private String formatDate(int date)
    {
        String newDate = "";
        if(date < 10)
        {
            newDate = "0" + String.valueOf(date);
        }
        else
        {
            newDate = String.valueOf(date);
        }

        return newDate;
    }

    private String formatCompanyName(String name)
    {
        int res = name.length() - 17;
        if(res < 0) // the name has less then 17 letters
        {
            // add whitespace chars until name has 17 letters
            res *= -1;
            for(int i = 0; i < res; ++i)
            {
                name += " ";
            }
        }
        else if(res > 0)
        {
            name = name.substring(0, 15);
            name = name + ".";
        }

        return name;
    }

}

