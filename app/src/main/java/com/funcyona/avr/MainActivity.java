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
import android.widget.TextView;

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

        Button btnRun = (Button) findViewById(R.id.buttonRun);
        TextView txtArch = (TextView) findViewById(R.id.textViewArch);
        TextView txtOutput = (TextView) findViewById(R.id.textViewOutput);

        String mArch = getProperty("os.arch");
        txtArch.setText(mArch);


        btnRun.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                txtOutput.setText("");

                String avrDir =  getDataDir().toString() + "/avr/" ;

                File mDir = new File(avrDir);
                mDir.mkdirs();

                try {
                    if (mArch.equals("i686"))
                        copyAssetFile(mArch + "/busybox-i686", avrDir + "/busybox");
                    else
                        copyAssetFile( "arm/busybox", avrDir + "/busybox");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File execFile = new File(avrDir + "/busybox");
                execFile.setExecutable(true);

                Process process = null;
                try {
                    process = Runtime.getRuntime().exec(avrDir + "/busybox");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                int read = 0;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();

                try {
                    read = reader.read(buffer);
                    output.append(buffer, 0, read);
                } catch (IOException e) {
                    e.printStackTrace();
                };

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

                txtOutput.setText(output.toString());
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
}