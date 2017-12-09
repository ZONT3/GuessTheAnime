package ru.zont.guesstheanime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Locale;

public class GuessActivity extends AppCompatActivity {
    InterstitialAd ia;

    ImageView image;
    TextView result;
    TextView resultO;
    EditText input;
    LinearLayout lay;
    LinearLayout titleLay;

    ActionBar ab;

    Anime anime;
    Player player;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        AdView av = findViewById(R.id.guess_ad);
        AdRequest request = new AdRequest.Builder().build();
        av.loadAd(request);
        ia = AdShower.load(this);

        i = getIntent().getIntExtra("i", -1);
        if (i<0) finish();

        anime = new Anime(i, this);
        player = new Player();

        ab = getSupportActionBar();
        if (ab != null) ab.setTitle(getString(R.string.guess_activity_title, player.addScore(0, this), i+1));

        image = findViewById(R.id.guess_image);
        result = findViewById(R.id.guess_result);
        resultO = findViewById(R.id.guess_resultO);
        input = findViewById(R.id.guess_input);
        lay = findViewById(R.id.guess_input_lay);
        titleLay = findViewById(R.id.guess_titleLay);

        image.setImageResource(anime.image);

        if (player.isCompleted(i, this)) {
            showTitle();
            lay.setVisibility(View.GONE);
            titleLay.setVisibility(View.VISIBLE);
        }
    }

    public void enter(View v) {
        boolean res = anime.hasTitle(input.getText().toString());
        if (res) {
            showTitle();
            player.setCompleted(i, this);
            if (ab != null) ab.setTitle(getString(R.string.guess_activity_title, player.addScore(anime.bayannost, this), i+1));
            lay.setVisibility(View.GONE);
            titleLay.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.guess_true, anime.bayannost), Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, R.string.guess_wrong, Toast.LENGTH_SHORT).show();
    }

    public void next(View v) {
        if (!player.isCompleted(i, this)) {
            Toast.makeText(this, R.string.guess_lol, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Anime.getTotalCount(this)<=i+1) {
            onBackPressed();
            return;
        }

        Intent intent = new Intent(GuessActivity.this, GuessActivity.class);
        intent.putExtra("i", i+1);
        startActivity(intent);
        ia.show();
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void showTitle() {
        resultO.setText(anime.originalTitle+" / "+anime.originalRomTitle);
        result.setText("");

        for (int j=0; j<anime.displayTitles.size(); j++) {
            if (!anime.displayTitles.get(j)[1].equals(Locale.getDefault().getLanguage())&&!anime.displayTitles.get(j)[1].equals("en"))
                continue;
            if (j>0) result.setText(result.getText().toString()+" / ");
            result.setText(result.getText().toString()+anime.displayTitles.get(j)[0]);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(GuessActivity.this, MainActivity.class));
        ia.show();
        finish();
    }
}
