package com.example.timeregtest1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeregtest1.CompanyDatabase.Company;
import com.example.timeregtest1.TimeRegister.TimeRegisterActivity;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity.COMPANY_NAME_KEY;


public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder>
{
    private ArrayList<Company> allCompanies = new ArrayList<>();

    private Context context;

    // try to use this interface to inform the TImeregactivity each time a company name has been clicked in the recview.
    public interface CompanyNameClicked
    {
        void onCompanyNameClicked(String companyName, int id);
    }

    private CompanyNameClicked companyNameClicked;

    /*boolean recItemClicked = false;*/

    public CompanyAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_list_item, parent, false);
        return new CompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.CompanyHolder holder, int position)
    {
        holder.txtCompanyName.setText(allCompanies.get(position).getCompanyName());

        holder.companyListItemRelLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               /* recItemClicked = true;
                String name = allCompanies.get(position).getCompanyName();
                Intent intent = new Intent(context, TimeRegisterActivity.class);
                intent.putExtra(COMPANY_NAME_KEY, name);
                intent.putExtra(REC_ITEM_CLICKED_KEY, recItemClicked);
                context.startActivity(intent);*/

                // get the company name that is clicked

                try
                {
                    companyNameClicked = (CompanyNameClicked) context;
                    companyNameClicked.onCompanyNameClicked(allCompanies.get(position).getCompanyName(), allCompanies.get(position).getId());
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                }
            }
        });

        // Maybe I just can use a bundle and intent from here instead of the callback I created in this class?
    }

    @Override
    public int getItemCount()
    {
        return allCompanies.size();
    }

    public class CompanyHolder extends RecyclerView.ViewHolder
    {
        private TextView txtCompanyName;
        private RelativeLayout companyListItemRelLayout;

        public CompanyHolder(@NonNull View itemView)
        {
            super(itemView);

            txtCompanyName = itemView.findViewById(R.id.txtCompanyName);
            companyListItemRelLayout = itemView.findViewById(R.id.companyListItemRelLayout);
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
