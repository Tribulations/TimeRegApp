package com.example.timeregtest1;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.data.StringFormat;

// class used to init ACRA
// use acra for debug reports
@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "kung_jocke@hotmail.com")
public class MyApplication extends Application
{
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.KEY_VALUE_LIST);

        // init ACRA
        ACRA.init(this, builder);
    }
}
