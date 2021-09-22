package com.example.timeregtest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Database;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timeregtest1.CompanyDatabase.CompanyDatabase;
import com.example.timeregtest1.mainfragment.MainFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    public static final String CURRENT_YEAR_KEY = "current_year";
    public static final String CURRENT_MONTH_KEY = "current_month";
    public static final String CURRENT_DAY_KEY = "current_day";
    private static final int EXT_STORAGE_PERMISSION_CODE = 101;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        // toggle for the navdrawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // get the textview in the navdrawer to set the versionname in the navdrawer
        View navDrawerHeader = navigationView.getHeaderView(0);
        TextView txtVersion = navDrawerHeader.findViewById(R.id.txtVersion);

        // get the version name of this application
        String version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        txtVersion.setText(txtVersion.getText().toString() + " version " + version);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.period:
                        Intent intent = new Intent(MainActivity.this, RegisteredDatesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_drawer_item2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try
                            {
                                handlePermissions();
                                BackupDatabase backupDatabase = new BackupDatabase(MainActivity.this);
                                backupDatabase.copyDatabase();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        break;
                }

                return false;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutContainer, new MainFragment());
        transaction.commit();

    }

    /*private boolean dateAllowed(Calendar today, int year, int month, int day)
    {
        Calendar c = Calendar.getInstance();

        c.set(year,month,day,0, 0, 0);

        if(c.getTimeInMillis() > today.getTimeInMillis())
        {
            return false;
        }

        return true;
    }*/

    private void initViews()
    {
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

    }

    public void backupDb()
    {
        try
        {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();



            if(sd.canWrite())
            {
                String dateRegsDbPath = getDatabasePath("date_registrations").getAbsolutePath();
                String ftgDbPath = getDatabasePath("ftg").getAbsolutePath();

                String backupPathDateRegsDb = "date_registrations_backup.db";
                String backupPathftgDb = "ftg_backup.db";

                File currentDateRegsDb = new File(dateRegsDbPath);
                File currentFtgDb = new File(ftgDbPath);



                File backupDateRegsDb = new File(sd, backupPathDateRegsDb);
                File backupFtgDb = new File(sd, backupPathftgDb);

                if(currentDateRegsDb.exists())
                {
                    FileChannel src = new FileInputStream(currentDateRegsDb).getChannel();
                    FileChannel dst = new FileOutputStream(backupDateRegsDb).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }

                if(currentFtgDb.exists())
                {
                    FileChannel src = new FileInputStream(currentFtgDb).getChannel();
                    FileChannel dst = new FileOutputStream(backupFtgDb).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void handlePermissions()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {


            Toast.makeText(this, "Har redan permissions", Toast.LENGTH_SHORT).show();

        }
        else // request permission
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_STORAGE_PERMISSION_CODE);
            }
        }


    }

    /*public static void backupDatabase(Context context, String dbName)
    {

        CompanyDatabase companyDatabase = CompanyDatabase.getInstance(context);
        companyDatabase.close();
        File dbfile = context.getDatabasePath(dbName);
        File sdir = new File(getFilePath(context, 0), "backup");
        String fileName = FILE_NAME + getDateFromMillisForBackup(System.currentTimeMillis());
        String sfpath = sdir.getPath() + File.separator + fileName;
        if (!sdir.exists())
        {
            sdir.mkdirs();
        }
        else
        {
            //Directory Exists. Delete a file if count is 5 already. Because we will be creating a new.
            //This will create a conflict if the last backup file was also on the same date. In that case,
            //we will reduce it to 4 with the function call but the below code will again delete one more file.
            checkAndDeleteBackupFile(sdir, sfpath);
        }

        File savefile = new File(sfpath);

        if (savefile.exists())
        {
            Log.d(TAG, "File exists. Deleting it and then creating new file.");
            savefile.delete();
        }
        try
        {
            if (savefile.createNewFile())
            {
                int buffersize = 8 * 1024;
                byte[] buffer = new byte[buffersize];
                int bytes_read = buffersize;
                OutputStream savedb = new FileOutputStream(sfpath);
                InputStream indb = new FileInputStream(dbfile);

                while ((bytes_read = indb.read(buffer, 0, buffersize)) > 0)
                {
                    savedb.write(buffer, 0, bytes_read);
                }

                savedb.flush();
                indb.close();
                savedb.close();
                SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
                sharedPreferences.edit().putString("backupFileName", fileName).apply();
                updateLastBackupTime(sharedPreferences);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "ex: " + e);
        }
    }*/

}