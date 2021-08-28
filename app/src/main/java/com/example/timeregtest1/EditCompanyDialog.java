package com.example.timeregtest1;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditCompanyDialog extends DialogFragment
{
    private Button btnRename, btnDelete;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_company, null);

        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);

        Bundle bundle = getArguments();
        if(bundle != null)
        {

        }

        return builder.create();
    }

    private void initViews(View view)
    {
        btnRename = view.findViewById(R.id.btnRename);
        btnDelete = view.findViewById(R.id.btnDelete);
    }
}
