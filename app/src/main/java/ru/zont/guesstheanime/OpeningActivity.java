package ru.zont.guesstheanime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class OpeningActivity extends AppCompatActivity {

    TextView name;
    TextView oname;
    Button play;
    ProgressBar pb;
    EditText input;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        AdView av = findViewById(R.id.op_ad);
        AdRequest request = new AdRequest.Builder().build();
        av.loadAd(request);


    }
}
