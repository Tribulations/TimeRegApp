package com.example.timeregtest1.CompanyDatabase;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;

@Database(entities = {Company.class, DateReg.class}, version = 2)
public abstract class CompanyDatabase extends RoomDatabase
{
    private static final String TAG = "CompanyDatabase";
    
    public abstract CompanyDao companyDao();
    public abstract DateRegDao dateRegDao();

    // singleton patter have only one instance of the database
    private static CompanyDatabase instance;
    public static synchronized CompanyDatabase getInstance(Context context)
    {
        Log.d(TAG, "getInstance: called");
        
        if(instance == null)
        {
            instance = Room.databaseBuilder(context, CompanyDatabase.class, "companies_database")
                    .addMigrations(MIGRATION_1_2)
                    .setJournalMode(JournalMode.TRUNCATE) // needed to save the db in one file. This is needed for makin a backup
                    .addCallback(initialCallback)
                    .build();
        }

        return instance;
    }

    // this callback is automaticly called the first time the db is created?
    private static RoomDatabase.Callback initialCallback = new RoomDatabase.Callback()
    {
        
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            Log.d(TAG, "onCreate in Roomdatabase.Callback: called ");
            super.onCreate(db);

            // call asynctask
            new InitialAsyncTask(instance).execute();

        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database)
        {
            database.execSQL("ALTER TABLE date_registrations ADD COLUMN note TEXT");
        }
    };

    public static CompanyDatabase getInstance()
    {
        return instance;
    }

    private static class InitialAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private static final String TAG = "InitialAsyncTask";
        
        private CompanyDao companyDao;
        private DateRegDao dateRegDao;
        private ArrayList<Company> testCompanies = new ArrayList<>();
        private ArrayList<DateReg> testDates = new ArrayList<>();

        // init db
        public InitialAsyncTask(CompanyDatabase db)
        {
            Log.d(TAG, "InitialAsyncTask: instantiated");

            this.companyDao = db.companyDao();
            this.dateRegDao = db.dateRegDao();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d(TAG, "doInBackground: (InitialAsyncTask) called");

            initTestCompanies();

            for(Company company : testCompanies)
            {
                companyDao.insert(company);
            }

            /*initTestDates();*/

            /*for(DateReg dateReg : testDates)
            {
                dateRegDao.insert(dateReg);
            }*/

            return null;
        }

        private void initTestCompanies()
        {
            Company dg = new Company("DG i Enköping AB");
            Company lb = new Company("LB Ohlssons Mekaniska AB");
            Company av = new Company("Av Bolaget AB");
            Company blend = new Company("Blend In Bra");
            Company cdh = new Company("CDH Tek Daniel Hjort");
            Company da = new Company("Daan Wigertz");
            Company di = new Company("Digital Visual Solutions Sweden AB");
            Company en = new Company("Enköpings Glastjänst AB");
            Company fe = new Company("FE Mur & Puts AB");
            Company fi = new Company("Firma Fredrik Pettersson");
            Company fj = new Company("Fjärdhundra Åkeri AB");
            Company g = new Company("Gudali");
            Company gu = new Company("Gun Eriksson");
            Company gun = new Company("Gunilla Sjödén Förvaltning AB");
            Company ha = new Company("Hanna Källarklippet");
            Company hl = new Company("HL Bygg & Montage");
            Company ig = new Company("IGFNP Airsoft Ekonomisk Förening");
            Company ju = new Company("Jung Bygg AB");
            Company k = new Company("Klippet Eva-Lis");
            Company l = new Company("L T Sjödén Consulting AB");
            Company l8 = new Company("L8s Redovisning");
            Company l8s = new Company("L8s Redovisning AB");
            Company la = new Company("Lars Davidsson");
            Company li = new Company("Lilléns bud & allservice AB");
            Company lil = new Company("Lillkyrka Mekaniska Verkstad Ulf Palm AB");
            Company lv = new Company("Lövlunds Snickeri");
            Company m = new Company("MPA Montage AB");
            Company ml = new Company("Måleri & Ytskikt Mälardalen AB");
            Company o = new Company("Olas Måleri");
            Company p = new Company("P Eklunds Måleri AB");
            Company pa = new Company("Palm Bygg & Montage i Enköping AB");
            Company r = new Company("Rikard Söderlund Trading AB");
            Company ro = new Company("Roland E Pettersson Åkeri");
            Company s = new Company("Salong Frida");
            Company sa = new Company("Salong Parant AB");
            Company sn = new Company("Snickaren Sjöqvist AB");
            Company sni = new Company("Snickerikompaniet Håkansson AB");
            Company snic = new Company("Snickerikompaniet Krippe AB");
            Company st = new Company("Stamar Fastighet HB");
            Company sw = new Company("Swedwrap");
            Company sd = new Company("Söderholms Fastighet HB");
            Company t = new Company("Thomas Sjödén Förvaltning AB");
            Company to = new Company("Tobbe Svensson Entertainment");
            Company u = new Company("Ulf Sjöström i Enköping AB");
            Company ke = new Company("Åke Lindström Bygg AB");

            testCompanies.add(dg);
            testCompanies.add(lb);
            testCompanies.add(av);
            testCompanies.add(blend);
            testCompanies.add(cdh);
            testCompanies.add(da);
            testCompanies.add(di);
            testCompanies.add(en);
            testCompanies.add(fe);
            testCompanies.add(fi);
            testCompanies.add(fj);
            testCompanies.add(g);
            testCompanies.add(gu);
            testCompanies.add(gun);
            testCompanies.add(ha);
            testCompanies.add(hl);
            testCompanies.add(ig);
            testCompanies.add(ju);
            testCompanies.add(k);
            testCompanies.add(l);
            testCompanies.add(l8);
            testCompanies.add(l8s);
            testCompanies.add(la);
            testCompanies.add(li);
            testCompanies.add(lil);
            testCompanies.add(lv);
            testCompanies.add(m);
            testCompanies.add(ml);
            testCompanies.add(o);
            testCompanies.add(p);
            testCompanies.add(pa);
            testCompanies.add(r);
            testCompanies.add(ro);
            testCompanies.add(s);
            testCompanies.add(sa);
            testCompanies.add(sn);
            testCompanies.add(sni);
            testCompanies.add(snic);
            testCompanies.add(st);
            testCompanies.add(sw);
            testCompanies.add(sd);
            testCompanies.add(t);
            testCompanies.add(to);
            testCompanies.add(u);
            testCompanies.add(ke);

        }

        /*private void initTestDates()
        {
            DateReg a = new DateReg(2021, 8, 12, "DG", 2, 1628726400000L);
            DateReg b = new DateReg(2021, 8, 12, "RST", 0.5f, 1628726400000L);
            DateReg c = new DateReg(2021, 8, 12, "CDH", 0.84f, 1628726400000L);
            DateReg d = new DateReg(2021, 8, 12, "LB", 3.5f, 1628726400000L);
            DateReg e = new DateReg(2021, 8, 12, "Limek", 1f, 1628726400000L);
            DateReg f = new DateReg(2021, 8, 12, "Lilléns", 12.34f, 1628726400000L);

            testDates.add(a);
            testDates.add(b);
            testDates.add(c);
            testDates.add(d);
            testDates.add(e);
            testDates.add(f);
        }*/
    }
}
