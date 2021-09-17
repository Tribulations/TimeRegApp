package com.example.timeregtest1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;

import java.io.IOException;

public class TempActivity extends AppCompatActivity
{
    private TextView txt;
    private CompanyDatabase companyDatabase;
    private BackupDatabase backupDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        txt = findViewById(R.id.textViewTemp);

        companyDatabase = CompanyDatabase.getInstance(this);

        /*Toast.makeText(getActivity(), companyDatabase.getOpenHelper().getReadableDatabase().getPath(), Toast.LENGTH_LONG).show();

         *//*String currentDir = System.getProperty("user.dir");

        Toast.makeText(getActivity(), currentDir, Toast.LENGTH_LONG).show();*/

        backupDatabase = new BackupDatabase(this);

        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                backupDatabase.writeDatabaseToCsv();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String contentFromCsvFile = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                contentFromCsvFile = backupDatabase.readDatabaseFromCsv();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        txt.setText(contentFromCsvFile);
    }
}