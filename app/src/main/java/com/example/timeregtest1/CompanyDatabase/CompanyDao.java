package com.example.timeregtest1.CompanyDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;

@Dao
public interface CompanyDao
{
    @Insert
    void insert(Company company);

    @Query("INSERT INTO ftg (ftg_namn) VALUES(:companyName)")
    void insertSingleCompanyByName(String companyName);

    @Query("INSERT INTO ftg (ftg_namn) VALUES (:companyName)")
    void insertSingleCompany(String companyName);

    @Query("SELECT * FROM ftg ORDER BY ftg_namn")
    List<Company> getAllCompanies();

    // asyncronous method?
    @Query("SELECT * FROM ftg")
    ListenableFuture<List<Company>> getAllCompaniesAsync();

    @Query("SELECT * FROM ftg ORDER BY ftg_namn")
    LiveData<List<Company>> getAllCompaniesLiveData();

    @Query("SELECT * FROM ftg WHERE ftg_namn LIKE :search")
    List<Company> searchForCompany(String search);

    @Query("SELECT * FROM ftg WHERE id = :id")
    Company getCompanyById(int id);

    @Query("DELETE FROM ftg WHERE id = :id")
    void deleteCompany(int id);

    @Query("UPDATE ftg SET ftg_namn = :newName WHERE id = :id")
    void updateCompanyName(String newName, int id);
}
