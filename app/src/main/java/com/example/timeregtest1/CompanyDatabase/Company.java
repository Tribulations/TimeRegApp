package com.example.timeregtest1.CompanyDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ftg")
public class Company
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "ftg_namn")
    private String companyName;

    public Company(String companyName)
    {
        this.companyName = companyName;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    @Ignore
    @Override
    public String toString()
    {
        return "Company{" +
                "companyName='" + companyName + '\'' +
                '}';
    }
}
