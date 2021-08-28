package com.example.timeregtest1.CompanyDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DateRegDao
{
    @Insert
    void insert(DateReg dateReg);

    @Query("SELECT * FROM date_registrations")
    List<DateReg> getAllRegistrations();

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 ORDER BY timestamp ASC")
    List<DateReg> getAllDateRegsInPeriod (long timestampStart, long timestampEnd);

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 ORDER BY timestamp ASC")
    LiveData<List<DateReg>> getAllDateRegsInPeriodLiveData (long timestampStart, long timestampEnd);

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 AND company_id = :companyId ORDER BY timestamp")
    List<DateReg> getAllDateRegsInPeriodByCompanyId (long timestampStart, long timestampEnd, int companyId);

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 AND company_name = :companyId ORDER BY timestamp")
    LiveData<List<DateReg>> getAllDateRegsInPeriodByCompanyIdLiveData (long timestampStart, long timestampEnd, int companyId);

    @Query("SELECT * FROM date_registrations WHERE year = :y AND month = :m AND day = :d")
    List<DateReg> getSelectedDatesData(int y, int m, int d);

    @Query("SELECT * FROM date_registrations WHERE year = :y AND month = :m AND day = :d")
    LiveData<List<DateReg>> getSelectedDateLiveData(int y, int m, int d);

    @Query("UPDATE date_registrations SET company_name = :newName, time_worked = :newTime, company_id = :companyId WHERE id = :id")
    void updateDateReg(String newName, float newTime, int id, int companyId);

    @Query("DELETE FROM date_registrations WHERE id = :id")
    void deleteDateReg(int id);
}
