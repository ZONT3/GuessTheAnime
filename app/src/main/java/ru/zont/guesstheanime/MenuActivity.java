package ru.zont.guesstheanime;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

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
        TextView score = findViewById(R.id.menu_score);

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

        View.OnLongClickListener longlistener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MenuActivity.this, "Done", Toast.LENGTH_SHORT).show();
                OpeningActivity.played = new ArrayList<>();
                return true;
            }
        };

        art.setOnClickListener(listener);
        op.setOnClickListener(listener);
        op.setOnLongClickListener(longlistener);

        score.setText(getString(R.string.menu_score, new Player().addScore(0, this)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_reset:
                new AlertDialog.Builder(MenuActivity.this).setTitle(R.string.main_reset_title)
                        .setMessage(R.string.main_reset_message)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Player.delete(MenuActivity.this))
                                    Toast.makeText(MenuActivity.this, R.string.main_delete_suc, Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(MenuActivity.this, R.string.main_delete_fail, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MenuActivity.this, MenuActivity.class));
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
    protected void onResume() {
        super.onResume();

        TextView score = findViewById(R.id.menu_score);
        score.setText(getString(R.string.menu_score, new Player().addScore(0, this)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main, menu);
        return true;
    }

    public void reward(MenuItem item) {
        startActivity(new Intent(MenuActivity.this, RewardActivity.class));
    }
}
