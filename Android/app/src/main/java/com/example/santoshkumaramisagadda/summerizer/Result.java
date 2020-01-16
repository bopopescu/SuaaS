package com.example.santoshkumaramisagadda.summerizer;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;


public class Result extends AppCompatActivity {

    TextView resultSum;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultSum=(TextView)findViewById(R.id.textView5);

        String summary = (String) getIntent().getExtras().get("summary");
        String[] su = summary.split(Pattern.quote("["));
        summary=su[su.length-1];
        summary=StringEscapeUtils.unescapeJava(summary);
        summary=summary.split(Pattern.quote("]"))[0];
        resultSum.setText(summary);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Result.this, FirstScreen.class));
        finish();

    }
}
