package com.funcyona.avr;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import static java.lang.System.getProperty;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mArch = getProperty("os.arch");

        Button btnRun = (Button) findViewById(R.id.button);
        EditText edtArch = (EditText) findViewById(R.id.editTextArch);

        edtArch.setText(getProperty("os.arch"));


        String nativeDir = getApplicationInfo().nativeLibraryDir;


        btnRun.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                String dataDir = getDataDir().toString();
                String avrDir =  getDataDir().toString() + "/avr/" + mArch;

                File mDir = new File(dataDir, "avr/" + mArch );
                mDir.mkdirs();

                try {
                    copyAssetFile("test.txt", avrDir + "/test.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    copyAssetFile(mArch + "/busybox-i686", avrDir + "/busybox-i686");
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
//                    copyAssetFile2("avr.bin", avrDir + "/avr.bin");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }



                File execFile = new File(avrDir + "/busybox-i686");
                execFile.setExecutable(true);

                Process process = null;
                try {
                    process = Runtime.getRuntime().exec(avrDir + "/busybox-i686");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                int read = 0;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();

                do {
                    try {
                        read = reader.read(buffer);

                        output.append(buffer, 0, read);

                    } catch (IOException e) {
                        e.printStackTrace();
                    };


                } while (read > 0);

                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                System.out.println("end");
            }
        });
    }



    public void copyAssetFile(String assetFilePath, String destinationFilePath) throws IOException
    {
        InputStream in = getApplicationContext().getAssets().open(assetFilePath);
        FileOutputStream out = new FileOutputStream(destinationFilePath);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }

    public void copyAssetFile2(String assetFilePath, String destinationFilePath) throws IOException
    {
        FileChannel inChannel = new FileInputStream(getApplicationContext().getAssets().open(assetFilePath).toString()).getChannel();
        FileChannel outChannel = new FileOutputStream(destinationFilePath).getChannel();

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public void copyAssetFile3(String assetFilePath, String destinationFilePath) throws IOException
    {
        InputStream in = getApplicationContext().getAssets().open(assetFilePath);



    }

    public String addTrailingSlash(String path)
    {
        if (path.charAt(path.length() - 1) != '/')
            path += "/";
        return path;
    }

    public String addLeadingSlash(String path)
    {
        if (path.charAt(0) != '/')
            path = "/" + path;
        return path;
    }

    public void createDir(File dir) throws IOException
    {
        if (dir.exists()) {
            if (!dir.isDirectory())
                throw new IOException("Can't create directory, a file is in the way");

        } else {
            dir.mkdirs();
            if (!dir.isDirectory())
                throw new IOException("Unable to create directory");

        }
    }

    public static boolean exists(AssetManager assetManager,
                                 String directory, String fileName) throws IOException {
        final String[] assets = assetManager.list(directory);
        for (String asset : assets)
            if (asset.equals(fileName))
                return true;
        return false;
    }

    public void openAssetFile(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getApplicationContext().getAssets().open(fileName), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                Log.d("debug", mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

    }

}