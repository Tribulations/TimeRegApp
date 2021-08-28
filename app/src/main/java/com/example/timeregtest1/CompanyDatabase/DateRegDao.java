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

    /*// TODO: 2021-08-15 Retrevie the regiesetered data between a time period. How aprroach this?
    @Query("SELECT * FROM date_registrations WHERE CASE WHEN :sD > :eD THEN year = :sY AND month = :sM AND day <= :sD AND day >= :eD ELSE year = :sY AND month = :sM AND day >= :sD AND day <= :eD END")
    List<DateReg> getAllDateRegsBySameYearAndMonth(int sY, int sM, int sD, int eD);*/

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 ORDER BY timestamp ASC")
    List<DateReg> getAllDateRegsInPeriod (long timestampStart, long timestampEnd);

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 ORDER BY timestamp ASC")
    LiveData<List<DateReg>> getAllDateRegsInPeriodLiveData (long timestampStart, long timestampEnd);

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 AND company_id = :companyId ORDER BY timestamp")
    List<DateReg> getAllDateRegsInPeriodByCompanyId (long timestampStart, long timestampEnd, int companyId);

    @Query("SELECT * FROM date_registrations WHERE timestamp >= :timestampStart AND timestamp < :timestampEnd + 86400000 AND company_name = :companyId ORDER BY timestamp")
    LiveData<List<DateReg>> getAllDateRegsInPeriodByCompanyIdLiveData (long timestampStart, long timestampEnd, int companyId);

    /*// TODO: 2021-08-15 Retrevie the regiesetered data between a time period. How aprroach this?
    @Query("SELECT * FROM date_registrations WHERE year = :sY AND month == :sM AND day >= :sD AND day <= :eD")
    List<DateReg> getAllDateRegsBySameYearAndMonth(int sY, int sM, int sD, int eD);

    @Query("SELECT * FROM date_registrations WHERE year = :sY AND month >= :sM AND month <= :eM AND day >= :sD AND day <= :eD OR year = :sY AND month >= :sM AND month < :eM ")
    List<DateReg> getAllDateRegsBySameYearSdayLEday(int sY, int sM, int eM, int sD, int eD); // the start day is less than end day eg. 3/5-21 and 6/5-21

    @Query("SELECT * FROM date_registrations WHERE year = :sY AND month >= :sM AND month <= :eM AND day <= :sD AND day >= :eD OR year = :sY AND month >= :sM AND month < :eM")
    List<DateReg> getAllDateRegsBySameYearSdayGEday(int sY, int sM, int eM, int sD, int eD); // the start day is greater than end day eg. 13/5-21 and 6/5-21*/

    @Query("SELECT * FROM date_registrations WHERE year = :y AND month = :m AND day = :d")
    List<DateReg> getSelectedDatesData(int y, int m, int d);

    @Query("SELECT * FROM date_registrations WHERE year = :y AND month = :m AND day = :d")
    LiveData<List<DateReg>> getSelectedDateLiveData(int y, int m, int d);
}
