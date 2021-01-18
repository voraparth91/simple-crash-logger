package xyz.parth.crashlytics.logger;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class UncaughtExceptionImpl implements Thread.UncaughtExceptionHandler {
    final Thread.UncaughtExceptionHandler defaultHandler;
    final Context context;

    public UncaughtExceptionImpl(Thread.UncaughtExceptionHandler handler, Context ctx){
        this.defaultHandler = handler;
        this.context = ctx;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        String formattedThrowable = formatThrowable(e);
        Log.e("FatalException", formattedThrowable);

        File path = context.getFilesDir();
        File file = new File(path, "crash-logs-"+ UUID.randomUUID().toString() +".txt");

        try{
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(formattedThrowable.getBytes());
            stream.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        if(defaultHandler!=null){
            defaultHandler.uncaughtException(t,e);
        }else{
            System.exit(1);
        }
    }

    private static String getStackTraceAsString(Throwable e){
        String output = e.toString();
        for(StackTraceElement se : e.getStackTrace()){
            output += "\n\tat " + se.toString();
        }
        return output;
    }


    private static String formatThrowable(Throwable e){
        String output = getStackTraceAsString(e);
        Throwable cause = e.getCause();
        while (cause != null ){
            output += "\nCaused by ";
            output +=  getStackTraceAsString(cause);
            cause = cause.getCause();
        }
        return output;
    }
}
