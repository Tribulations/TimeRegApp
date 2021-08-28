/*
package com.example.timeregtest1.TimeRegister;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeregtest1.CompanyAdapter;
import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.R;

import java.util.ArrayList;
import java.util.List;

public class CompanyLiveDataAdapter extends RecyclerView.Adapter<CompanyLiveDataAdapter.CompanyLiveDataHolder>
{
    private LiveData<List<Company>> allCompaniesLiveData;


    @NonNull
    @Override
    public CompanyLiveDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_list_item, parent, false);
        return new CompanyLiveDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.CompanyHolder holder, int position)
    {
        holder.txtCompanyLDName.setText(allCompanies.get(position).getCompanyName());

        holder.companyLDListItemRelLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(v.getContext(), allCompaniesLiveData.get(position).getCompanyName() + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return allCompanies.size();
    }

    public class CompanyLiveDataHolder extends RecyclerView.ViewHolder
    {
        private TextView txtCompanyLDName;
        private RelativeLayout companyLDListItemRelLayout;

        public CompanyLiveDataHolder(@NonNull View itemView)
        {
            super(itemView);

            txtCompanyLDName = itemView.findViewById(R.id.txtCompanyLDName);
            companyLDListItemRelLayout = itemView.findViewById(R.id.companyLDListItemRelLayout);
        }
    }

    public ArrayList<Company> getAllCompanies()
    {
        return allCompanies;
    }

    public void setAllCompanies(ArrayList<Company> allCompanies)
    {
        this.allCompanies = allCompanies;
        notifyDataSetChanged();
    }


}
*/
