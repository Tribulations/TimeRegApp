package com.example.timeregtest1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.CompanyDatabase.DateReg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static java.net.Proxy.Type.HTTP;

public class BackupDatabase
{
    private Context context;
    private CompanyDatabase companyDatabase;
    private String databaseFilePath;
    private String pathToCsvBackupFile;
    private ArrayList<DateReg> allDateRegs = new ArrayList<>();
    private String filenameSuffix = "_dates_backup.txt";
    private String pathToCsvFileExternalStorage = "";

    private static final String TAG = "BackupDatabase";
    
    public BackupDatabase(Context context)
    {
        Log.d(TAG, "BackupDatabase: instantiated: databaseFilePath: " + databaseFilePath + "\n" + "pathTOCsvBackupFile: " + pathToCsvBackupFile );
        this.context = context;
        this.companyDatabase = CompanyDatabase.getInstance(context);
        databaseFilePath = companyDatabase.getOpenHelper().getReadableDatabase().getPath();
        pathToCsvBackupFile = databaseFilePath + filenameSuffix;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeDatabaseToCsv() throws IOException
    {
        // write the dateReg table to csv file

        Log.d(TAG, "writeDatabaseToCsv: called");
        
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
                    "," + dateReg.getCompanyId() + "," + dateReg.getNote() + "\n";

            /*Files.write(Paths.get(pathToCsvBackupFile), dbRow.getBytes(), StandardOpenOption.CREATE);*/

            /*Toast.makeText(context, "Created the file: " + pathToCsvBackupFile, Toast.LENGTH_SHORT).show();*/
        }

        System.out.println("dbContent: " + dbContent);
        System.out.println("The db csv filepath: " + Paths.get(pathToCsvBackupFile).toString());
        Files.write(Paths.get(pathToCsvBackupFile), dbContent.getBytes(), StandardOpenOption.CREATE);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String readFile(String pathToFile) throws IOException
    {
        String fileContent = "";
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(pathToFile));

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendCsvByEmail()
    {
        writeToExternal("companies_database" + filenameSuffix);


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        /*emailIntent.setType("application/csv");*/
        /*emailIntent.setType("message/rfc822");*/
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"kung_jocke@hotmail.com"}); //
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "The subject of email");
        /*emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");*/
        emailIntent.putExtra(Intent.EXTRA_TEXT, "The message goes here");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pathToCsvBackupFile));
        /*emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pathToCsvFileExternalStorage));*/
        /*emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + context.getExternalFilesDir(null) + File.separator + "companies_database_dates_backup.csv"));*/
        System.out.println("file://" + context.getExternalFilesDir(null) + File.separator + "companies_database_dates_backup.txt");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(emailIntent, "createChooser used"));
    }

    // method copied from stackoverflow
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeToExternal(String filename)
    {
        try {
            /*File file = new File(context.getExternalFilesDir(null), filename);*/ //Get file location from external source
            File file = new File(context.getExternalCacheDir(), filename); // trying to write to cacheDIr instead of above
            /*InputStream is = new FileInputStream(context.getFilesDir() + File.separator + filename);*/ //get file location from internal
            /*InputStream is = new FileInputStream(pathToCsvBackupFile);*/ // this line instead from the one above. The above gets the wrong folder /files that doesn't exist in the timeregapp folder
            InputStream is = new FileInputStream(Paths.get(pathToCsvBackupFile).toString()); // another test with this line again. changed from the one above obviously
            OutputStream os = new FileOutputStream(file); //Open your OutputStream and pass in the file you want to write to
            byte[] toWrite = new byte[is.available()]; //Init a byte array for handing data transfer
            Log.i("Available ", is.available() + "");
            int result = is.read(toWrite); //Read the data from the byte array
            Log.i("Result", result + "");
            os.write(toWrite); //Write it to the output stream
            is.close(); //Close it
            os.close(); //Close it
            Log.i("Copying to", "" + context.getExternalFilesDir(null) + File.separator + filename);
            /*Log.i("Copying from", context.getFilesDir() + File.separator + filename + "");*/
            Log.i("Copying from", Paths.get(pathToCsvBackupFile).toString());
            /*System.out.println("Copying from: " + context.getFilesDir() + File.separator + filename + "");*/
            System.out.println("Copying from: " + Paths.get(pathToCsvBackupFile).toString());
            System.out.println("Copying to " + context.getExternalFilesDir(null) + File.separator + filename);

            /*pathToCsvFileExternalStorage = context.getExternalFilesDir(null) + File.separator + filename;*/
            pathToCsvFileExternalStorage = context.getExternalCacheDir() + File.separator + filename;

            // read the file just created and print the text
            String contentFromExternalFile = readFile(context.getExternalCacheDir() + File.separator + filename);
            /*String contentFromExternalFile = readFile(context.getExternalFilesDir(null) + File.separator + filename);*/
            System.out.println("The data read from the file copied to external storage: " + contentFromExternalFile);

            System.out.println("-----------------------------------------------------------------\n\n\n");
            System.out.println("The external path: cache: " + pathToCsvFileExternalStorage);
            System.out.println("The external path: " + context.getExternalFilesDir(null) + File.separator + filename);
        }
        catch (Exception e)
        {
            System.out.println("copying th efile failed");
            Toast.makeText(context, "File write failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show(); //if there's an error, make a piece of toast and serve it up
            Toast.makeText(context, "Tried to copy from: " + context.getFilesDir() + File.separator + filename + "", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Tried to copy to: " + "" + context.getExternalFilesDir(null) + File.separator + filename, Toast.LENGTH_LONG).show();
        }
    }
}
