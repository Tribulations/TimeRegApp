package com.example.timeregtest1.selectedDate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeregtest1.CompanyDatabase.DateReg;
import com.example.timeregtest1.R;

import java.util.ArrayList;

public class DateRegsAdapter extends RecyclerView.Adapter<DateRegsAdapter.ViewHolder>
{
    private ArrayList<DateReg> allDateRegs = new ArrayList<>();

    private Context context;

    public DateRegsAdapter(Context context)
    {
        this.context = context;
    }

    // interface to get the DateReg id
    public interface DateRegClicked
    {
        void onDateRegClick(int id, String companyN, float timeW, int cId);
    }

    private DateRegClicked dateRegClicked;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_reg_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateRegsAdapter.ViewHolder holder, int position)
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

        holder.txtCompanyName.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                try
                {
                    dateRegClicked = (DateRegClicked) context;
                    dateRegClicked.onDateRegClick(allDateRegs.get(position).getId(),
                            allDateRegs.get(position).getCompanyName(), allDateRegs.get(position).getTimeWorked(),
                            allDateRegs.get(position).getCompanyId());
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                }

                return true;
            }
        });
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
        private TextView txtCompanyName, txtTimeWorked;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            txtCompanyName = itemView.findViewById(R.id.txtCompanyName);
            txtTimeWorked = itemView.findViewById(R.id.txtTimeWorked);
        }
    }
}
