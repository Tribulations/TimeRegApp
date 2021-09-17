package com.example.timeregtest1;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class BackupDatabase
{
    private Context context;
    private CompanyDatabase companyDatabase;
    private String databaseFilePath;
    private String pathToCsvBackupFile;
    private ArrayList<DateReg> allDateRegs = new ArrayList<>();

    public BackupDatabase(Context context)
    {
        this.context = context;
        this.companyDatabase = CompanyDatabase.getInstance(context);
        databaseFilePath = companyDatabase.getOpenHelper().getReadableDatabase().getPath();
        pathToCsvBackupFile = databaseFilePath + "_dates_backup.csv";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeDatabaseToCsv() throws IOException
    {
        // write the dateReg table to csv file

        Thread thread = new Thread(new GetDateRegsThread());
        thread.start();

        while(thread.isAlive())
        {
            SystemClock.sleep(10);
        }

        String dbContent = "";

        for(DateReg dateReg : allDateRegs)
        {
            /*String dbRow = dateReg.getCompanyId() + "," + dateReg.getYear() + "," +
                    dateReg.getMonth() + "," + dateReg.getDay() + "," + dateReg.getCompanyName() +
                    "," + dateReg.getTimeWorked() + "," + dateReg.getTimestamp() +
                    "," + dateReg.getCompanyId() + "," + dateReg.getNote() + "\n";*/

            dbContent += dateReg.getCompanyId() + "," + dateReg.getYear() + "," +
                    String.valueOf(dateReg.getMonth() + 1) + "," + dateReg.getDay() + "," + dateReg.getCompanyName() +
                    "," + dateReg.getTimeWorked() + "," + dateReg.getTimestamp() +
                    "," + dateReg.getCompanyId() + "," + dateReg.getNote() + "\n\t";

            /*Files.write(Paths.get(pathToCsvBackupFile), dbRow.getBytes(), StandardOpenOption.CREATE);*/
            Files.write(Paths.get(pathToCsvBackupFile), dbContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String readDatabaseFromCsv() throws IOException
    {
        String fileContent = "";
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(pathToCsvBackupFile));

        for(String row : lines)
        {
            fileContent += row;
        }

        return fileContent;
    }

    public class GetDateRegsThread implements Runnable
    {
        @Override
        public void run()
        {
            allDateRegs = (ArrayList<DateReg>) companyDatabase.dateRegDao().getAllRegistrations();
        }
    }

}
