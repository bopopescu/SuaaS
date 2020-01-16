package com.example.santoshkumaramisagadda.summerizer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class FirstScreen extends AppCompatActivity {

    Button b1,b2,b3,b4;
    EditText et1;
    int nLines;
    String tok;
    File to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.example.santoshkumaramisagadda.summerizer_FCMMESSAGE"));


        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10001);
        }

        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10002);
        }

        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},10003);
        }

        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.INTERNET},10004);
        }

        b1=(Button)findViewById(R.id.text_button);
        b2=(Button)findViewById(R.id.image_button);
        b3=(Button)findViewById(R.id.audio_button);
        b4=(Button)findViewById(R.id.file_button);
        et1=(EditText)findViewById(R.id.editText2);
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);


        tok = FirebaseInstanceId.getInstance().getToken();
        File root = new File(Environment.getExternalStorageDirectory()+"/Android/Data/Cloud");
        if (!root.exists()) {
            root.mkdirs();
        }

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                nLines=Integer.parseInt(et1.getText().toString());
                Intent myIntent = new Intent(FirstScreen.this, TextScreen.class);
                myIntent.putExtra("nLines", nLines);
                myIntent.putExtra("tok", tok);
                startActivity(myIntent);

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                nLines=Integer.parseInt(et1.getText().toString());
                Intent myIntent = new Intent(FirstScreen.this,ImageScreen.class);
                myIntent.putExtra("nLines",nLines);
                myIntent.putExtra("tok",tok);
                startActivity(myIntent);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                nLines=Integer.parseInt(et1.getText().toString());
                Intent myIntent = new Intent(FirstScreen.this,AudioScreen.class);
                myIntent.putExtra("nLines",nLines);
                myIntent.putExtra("tok",tok);
                startActivity(myIntent);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                new MaterialFilePicker()
                        .withActivity(FirstScreen.this)
                        .withRequestCode(1)
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });

        if(getIntent().getExtras()!=null)
        {
            for(String key: getIntent().getExtras().keySet())
            {
                if(key.equalsIgnoreCase("key"))
                {
                    String message = getIntent().getExtras().getString(key);

                    //get request
                    URL obj = null;
                    JSONObject myResponse=null;
                    final String[] summary = {""};
                        String url = "https://poised-sunrise-200202-202320.appspot.com//api/suaas?key="+message;
                        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                summary[0] =response;
                                if(summary[0].toLowerCase().contains("completed"))  {
                                    Intent finalIntent = new Intent(FirstScreen.this, Result.class);
                                    finalIntent.putExtra("summary", summary[0]);
                                    startActivity(finalIntent);
                                }
                            }
                        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), (CharSequence) error,Toast.LENGTH_LONG).show();
                            }
                        });

                    ExampleRequestQueue.add(ExampleStringRequest);

                }
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 10002: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 10003: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 10004: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            nLines=Integer.parseInt(et1.getText().toString());
            File filePath = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
            String extension = filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf("."));

            File root = new File(Environment.getExternalStorageDirectory()+"/Android/Data/Cloud");
            String fileName="";
            if(extension.equalsIgnoreCase(".jpg"))
            {
                fileName=tok+"_input_text.txt";
                Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
                TextRecognizer textRecognizer= new TextRecognizer.Builder(getApplicationContext()).build();
                StringBuilder sb = new StringBuilder();

                if(!textRecognizer.isOperational())
                {
                    Toast.makeText(getApplicationContext(),"Unable to extract text",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Frame frame =new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);

                    for(int i=0;i<items.size();i++)
                    {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append(" ");
                    }
                }
                String s= sb.toString();
                FileWriter writer = null;
                try
                {
                    to = new File(root,fileName);
                    writer = new FileWriter(to);
                    writer.append(s);
                    writer.flush();
                    writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(extension.equalsIgnoreCase(".mp3"))
            {
                fileName=tok+"_input_audio"+extension;
                to = new File(root,fileName);
                filePath.renameTo(to);
            }

            else if(extension.equalsIgnoreCase(".txt"))
            {
                fileName=tok+"_input_txt"+extension;
                to = new File(root,fileName);
                filePath.renameTo(to);
            }




            Intent myIntent =new Intent(FirstScreen.this,UploadScreen.class);
            myIntent.putExtra("nLines",nLines);
            myIntent.putExtra("filename",to);
            startActivity(myIntent);
        }
    }

    private BroadcastReceiver mHandler = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("key");
            RequestQueue ExampleRequestQueue = Volley.newRequestQueue(FirstScreen.this);

            //get request
            URL obj = null;
            JSONObject myResponse=null;
            final String[] summary = {""};
            String url = "https://poised-sunrise-200202-202320.appspot.com//api/suaas?key="+message;
            StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    summary[0] =response;
                    //String status=summary[0].split("\": \"")[1].split(",")[0];
                    if(summary[0].toLowerCase().contains("completed")) {
                        Intent finalIntent = new Intent(FirstScreen.this, Result.class);
                        finalIntent.putExtra("summary", summary[0]);
                        startActivity(finalIntent);
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), (CharSequence) error,Toast.LENGTH_LONG).show();
                }
            });

            ExampleRequestQueue.add(ExampleStringRequest);
        }
    };
}
