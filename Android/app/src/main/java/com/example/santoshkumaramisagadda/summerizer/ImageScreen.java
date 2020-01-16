package com.example.santoshkumaramisagadda.summerizer;

import android.*;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImageScreen extends AppCompatActivity {

    String tok;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    File photoFile;
    Uri photoURI;
    SharedPreferences sharedPreferences;
    ImageView ivCaptured;
    Button bProceed;
    int nLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tok= (String) getIntent().getExtras().get("tok");
        nLines=(int) getIntent().getExtras().get("nLines");

        setContentView(R.layout.activity_image_screen);
        sharedPreferences = getSharedPreferences("File names", Context.MODE_PRIVATE);
        ivCaptured = (ImageView) findViewById(R.id.ivCaptured);
        bProceed = (Button) findViewById(R.id.bProceed);

        //proceed button
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File root = new File(Environment.getExternalStorageDirectory()+"/Android/Data/Cloud");
                String fileName=tok+"_input_img.jpg";
                String fileName1= tok+"_inp_text.txt";
                File textFile = new File(root, fileName1);

                File to = new File(root,fileName);
                photoFile.renameTo(to);

                Intent myIntent =new Intent(ImageScreen.this,UploadScreen.class);
                myIntent.putExtra("filename",to);
                myIntent.putExtra("nLines",nLines);
                startActivity(myIntent);

                //ocr trial
                Bitmap bitmap = BitmapFactory.decodeFile(to.getAbsolutePath());
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
                try {
                    while(textFile.exists())
                        textFile.delete();
                    writer = new FileWriter(textFile);
                    writer.append(s);
                    writer.flush();
                    writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                myIntent =new Intent(ImageScreen.this,UploadScreen.class);
                myIntent.putExtra("filename",textFile);
                myIntent.putExtra("nLines",nLines);
                startActivity(myIntent);

                //ocr end
            }
        });
        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                createFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                photoURI = FileProvider.getUriForFile(this, this.getApplicationContext()
                                .getPackageName() + ".provider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void createFile() throws IOException {
        // Create an image file name
        String fileName = tok+"_input_img";
        File root = new File(Environment.getExternalStorageDirectory()+"/Android/Data/Cloud");
        photoFile = File.createTempFile(fileName, ".jpg", root);
        mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
    }

    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setPic();
        } else if(resultCode == RESULT_CANCELED) {
            photoFile.delete();
            Intent it = new Intent(ImageScreen.this, FirstScreen.class);
            startActivity(it);
        }
    }


    private void setPic() {
        if(photoFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ivCaptured.setImageBitmap(myBitmap);
        }
    }
}
