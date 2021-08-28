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
    /*@ColumnInfo(name = "arbetad_tid", defaultValue = "0")
    private int timeWorked;*/


    /*public Company(String companyName, int timeWorked)
    {
        this.companyName = companyName;
        this.timeWorked = timeWorked;
    }
*/
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

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    /*public int getTimeWorked()
    {
        return timeWorked;
    }

    public void setTimeWorked(int timeWorked)
    {
        this.timeWorked = timeWorked;
    }*/

    /*@Ignore
    @Override
    public String toString()
    {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", timeWorked=" + timeWorked +
                '}';
    }*/

    @Ignore
    @Override
    public String toString()
    {
        return "Company{" +
                "companyName='" + companyName + '\'' +
                '}';
    }
}
