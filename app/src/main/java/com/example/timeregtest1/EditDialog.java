package com.example.timeregtest1;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditDialog extends DialogFragment
{
    public interface EditDialogClicked
    {
        void onRename();

        void onDelete();
    }

    private EditDialogClicked editDialogClicked;

    private Button btnRename, btnDelete;
    private ImageView btnClose;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit, null);

        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);

        Bundle bundle = getArguments();
        if(bundle != null)
        {

        }

        btnRename.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    editDialogClicked = (EditDialogClicked) getContext();
                    editDialogClicked.onRename();
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    editDialogClicked = (EditDialogClicked) getContext();
                    editDialogClicked.onDelete();
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        return builder.create();


    }

    private void initViews(View view)
    {
        btnRename = view.findViewById(R.id.btnRename);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnClose = view.findViewById(R.id.btnClose);
    }
}
