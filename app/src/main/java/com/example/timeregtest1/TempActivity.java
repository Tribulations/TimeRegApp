/*
package com.example.timeregtest1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;

import java.io.IOException;

public class TempActivity extends AppCompatActivity
{
    private TextView txt;
    private CompanyDatabase companyDatabase;
    private BackupDatabase backupDatabase;
    private Button btnSend;

    private static final int EXT_STORAGE_PERMISSION_CODE = 101;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        txt = findViewById(R.id.textViewTemp);
        btnSend = findViewById(R.id.btnSend);

        companyDatabase = CompanyDatabase.getInstance(this);

Toast.makeText(getActivity(), companyDatabase.getOpenHelper().getReadableDatabase().getPath(), Toast.LENGTH_LONG).show();


String currentDir = System.getProperty("user.dir");

        Toast.makeText(getActivity(), currentDir, Toast.LENGTH_LONG).show();


        backupDatabase = new BackupDatabase(this);

handlePermissions();

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

        String contentFromCsvFile = "Text";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            try
            {
                contentFromCsvFile = "";
                contentFromCsvFile = backupDatabase.readDatabaseFromCsv();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            contentFromCsvFile = "Couldn't read the file";
        }

        System.out.println("contentFromCsvFile: " + contentFromCsvFile);;
        txt.setText(contentFromCsvFile);




        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v)
            {
handlePermissions();

backupDatabase.sendCsvByEmail();

                try
                {
                    backupDatabase.copyDatabase();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Vi har perm", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handlePermissions()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            */
/*backupDatabase.sendCsvByEmail();*//*


            Toast.makeText(this, "Har redan permissions", Toast.LENGTH_SHORT).show();

        }
        else // request permission
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                showSnackbar();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_STORAGE_PERMISSION_CODE);
            }
        }


    }

    private void showSnackbar()
    {
    }

 @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == )

    }



    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(TempActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
*/
