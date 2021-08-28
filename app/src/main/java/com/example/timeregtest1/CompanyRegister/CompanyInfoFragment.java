package com.example.timeregtest1.CompanyRegister;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.timeregtest1.R;

import static com.example.timeregtest1.CompanyRegister.CompanyRegisterActivity.COMPANY_NAME_KEY;

public class CompanyInfoFragment extends Fragment
{
    private TextView txtCompanyName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_company_info, container, false);

        txtCompanyName = view.findViewById(R.id.txtCompanyNameCIF);

        if(getArguments() != null)
        {
            String companyName = getArguments().getString(COMPANY_NAME_KEY);
            txtCompanyName.setText(companyName);
        }

        return view;
    }
}
