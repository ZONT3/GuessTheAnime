package ru.zont.guesstheanime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MobileAds.initialize(this, "ca-app-pub-7799305268524604~8194410407");
        AdView av = findViewById(R.id.menu_ad);
        AdRequest request = new AdRequest.Builder().build();
        av.loadAd(request);

        Button art = findViewById(R.id.menu_art);
        Button op = findViewById(R.id.menu_op);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (view.getId()) {
                    case R.id.menu_art:
                        intent = new Intent(MenuActivity.this, MainActivity.class);
                        break;
                    case R.id.menu_op:
                        intent = new Intent(MenuActivity.this, OpeningActivity.class);
                        break;
                }

                if (intent!=null)
                    startActivity(intent);
            }
        };

        art.setOnClickListener(listener);
        op.setOnClickListener(listener);
    }
}
