package com.example.timeregtest1.CompanyDatabase;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "date_registrations")
public class DateReg
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int year;
    private int month;
    private int day;
    @ColumnInfo(name = "company_name")
    private String companyName;
    @ColumnInfo(name = "time_worked")
    private float timeWorked;
    private long timestamp;
    @ColumnInfo(name = "company_id")
    private int companyId;
    private String note;

    public DateReg(int year, int month, int day, String companyName, float timeWorked, long timestamp, int companyId, String note)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.companyName = companyName;
        this.timeWorked = timeWorked;
        this.timestamp = timestamp;
        this.companyId = companyId;
        this.note = note;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public float getTimeWorked()
    {
        return timeWorked;
    }

    public void setTimeWorked(float timeWorked)
    {
        this.timeWorked = timeWorked;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(int companyId)
    {
        this.companyId = companyId;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    @Ignore
    @Override
    public String toString()
    {
        return "DateReg{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", companyName='" + companyName + '\'' +
                ", timeWorked=" + timeWorked +
                ", timestamp=" + timestamp +
                ", note='" + note + '\'' +
                '}';
    }
}
