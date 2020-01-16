package com.example.santoshkumaramisagadda.summerizer;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadScreen extends AppCompatActivity {

    private StorageReference mStorageRef;
    int nLines;
    File toUploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_screen);
        toUploadFile = (File) getIntent().getExtras().get("filename");
        nLines=(int) getIntent().getExtras().get("nLines");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Uri file = Uri.fromFile(toUploadFile);
        StorageReference FileRef = mStorageRef.child("uploads/"+toUploadFile);

        FileRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        sendJSOn(downloadUrl);
                        //push download url
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Toast.makeText(UploadScreen.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //displaying percentage in progress dialog
                        //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });

    }

    private void sendJSOn(final Uri x) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://poised-sunrise-200202-202320.appspot.com//api/suaas");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("link",x);
                    jsonParam.put("numberOfSentences",nLines);

                    File filePath = new File(String.valueOf(toUploadFile));
                    String extension = filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf("."));

                    if(extension.equalsIgnoreCase(".jpg"))
                        jsonParam.put("type","ocr");
                    else if(extension.equalsIgnoreCase(".mp3"))
                        jsonParam.put("type","audioLink");
                    else if(extension.equalsIgnoreCase(".txt"))
                        jsonParam.put("type","webArticle");

                    jsonParam.put("token",FirebaseInstanceId.getInstance().getToken());
                    jsonParam.put("tokenType","fcm");

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();
                    int response=conn.getResponseCode();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
