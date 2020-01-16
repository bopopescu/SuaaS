package com.example.santoshkumaramisagadda.summerizer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class TextScreen extends AppCompatActivity {


    EditText text_inp;
    Button b1;
    int nLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String tok= (String) getIntent().getExtras().get("tok");
        nLines=(int) getIntent().getExtras().get("nLines");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_screen);

        text_inp=(EditText)findViewById(R.id.editText_inp);
        b1=(Button)findViewById(R.id.summerize_1);




        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                String inp=text_inp.getText().toString();

                File root = new File(Environment.getExternalStorageDirectory()+"/Android/Data/Cloud");

                String fileName= tok+"_inp_text.txt";
                File textFile = new File(root, fileName);

                while (textFile.exists()){

                    textFile.delete(); // here i'm checking if file exists and if yes then i'm deleting it but its not working
                }
                textFile = new File(root, fileName);

                FileWriter writer = null;
                try {
                    writer = new FileWriter(textFile);
                    writer.append(inp);
                    writer.flush();
                    writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Intent myIntent =new Intent(TextScreen.this,UploadScreen.class);
                myIntent.putExtra("nLines",nLines);
                myIntent.putExtra("filename",textFile);
                startActivity(myIntent);

            }
        });





    }
}
