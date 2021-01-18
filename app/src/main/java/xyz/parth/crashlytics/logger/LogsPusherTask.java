package xyz.parth.crashlytics.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LogsPusherTask extends AsyncTask {

    private final WeakReference<Context> context;

    public LogsPusherTask(Context ctx){
        this.context = new WeakReference<>(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        File path = context.get().getFilesDir();
        final String[] listOfFiles = context.get().getFilesDir().list();
        if (listOfFiles != null) {
            for (String fileName : listOfFiles) {
                Log.d("CrashFile", fileName);
                if(!fileName.startsWith("crash-logs-")){
                    continue;
                }

                try{
                    byte[] fileContent = readContents(fileName);
                    if(fileContent.length == 0)continue;

                    int status = pushContent(fileContent);
                    if(status == 200){
                        Log.e("CrashLogPusher", "Pushed file" + fileName);
                        deleteFile(fileName);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private int pushContent(byte[] fileContent){
        try{
            URL url = new URL ("https://eno78h0yf0to.x.pipedream.net");
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain; utf-8");
            con.setDoOutput(true);
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            OutputStream os = con.getOutputStream();
            os.write(fileContent); os.flush(); os.close();

            return con.getResponseCode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    private boolean deleteFile(String fileName){
        try{
            File path = context.get().getFilesDir();
            File file = new File(path + "/" + fileName);
            if(file.exists()){
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private byte[] readContents(String fileName){
        try{
            File path = context.get().getFilesDir();
            File file = new File(path + "/" + fileName);
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            try (FileInputStream in = new FileInputStream(file)) {
                in.read(bytes);
            }
            return bytes;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[]{};
    }
}
