package ru.zont.guesstheanime;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-7799305268524604~8194410407");
        AdView av = findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        av.loadAd(request);

        int MAX_BUTTONS_HORIZONTAL = 3;
        Player player = new Player();

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        if (size.x>=700) MAX_BUTTONS_HORIZONTAL = 4;
        Log.d(TAG, "MBH="+ MAX_BUTTONS_HORIZONTAL);

        LinearLayout holder = findViewById(R.id.main_levels);

        LinearLayout currLayout = findViewById(R.id.main_topLay);
        Button lastButt = findViewById(R.id.main_firstButt);
        for (int i=0; i<Anime.getTotalCount(this); i++) {
            if (i>0&&i%MAX_BUTTONS_HORIZONTAL==0) {
                LinearLayout newLay = new LinearLayout(this);
                newLay.setLayoutParams(currLayout.getLayoutParams());
                holder.addView(newLay);
                currLayout = newLay;
            }

            Button button = new Button(this);
            button.setLayoutParams(lastButt.getLayoutParams());
            button.setVisibility(View.VISIBLE);
            button.setText(i+1+"");
            button.setTextSize(32);
            button.setEnabled(player.isCompleted(i-1, this));
            if (player.isCompleted(i, this)) button.setBackgroundColor(Color.GREEN);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, GuessActivity.class);
                    intent.putExtra("animeID", finalI);
                    startActivity(intent);
                    finish();
                }
            });
            currLayout.addView(button);
        }

        if (getIntent().getBooleanExtra("end", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.guess_end_title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(R.string.guess_end_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    }).create().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_reset:
                new AlertDialog.Builder(MainActivity.this).setTitle(R.string.main_reset_title)
                        .setMessage(R.string.main_reset_message)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Player.delete(MainActivity.this))
                                    Toast.makeText(MainActivity.this, R.string.main_delete_suc, Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(MainActivity.this, R.string.main_delete_fail, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        }).create().show();
                break;
            default: return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main, menu);
        return true;
    }

    public void reward(MenuItem item) {
        startActivity(new Intent(MainActivity.this, RewardActivity.class));
    }
}
