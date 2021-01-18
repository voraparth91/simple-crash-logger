package xyz.parth.crashlytics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import xyz.parth.crashlytics.logger.LogsPusherTask;
import xyz.parth.crashlytics.logger.UncaughtExceptionImpl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionImpl(Thread.getDefaultUncaughtExceptionHandler(), this));
        new LogsPusherTask(this).execute();

    }

    public void crashApp(View v){
        throw new RuntimeException("We crashed the App. Now these logs should appear in the requestbin");
    }


}