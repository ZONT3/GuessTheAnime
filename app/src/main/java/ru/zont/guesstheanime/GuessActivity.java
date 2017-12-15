package ru.zont.guesstheanime;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuessActivity extends AppCompatActivity {
    InterstitialAd ia;

    static final int[] HINT_COST = {60, 40, 50, 20, 10, 30, 30};
    static final int HINT_SKIP = 0;
    static final int HINT_SHWENG = 1;
    static final int HINT_SHWJP = 2;
    static final int HINT_RANDENG = 3;
    static final int HINT_RANDJP = 4;
    static final int HINT_DESC = 5;
    static final int HINT_CHARS = 6;

    ImageView image;
    TextView result;
    TextView resultO;
    EditText input;
    LinearLayout lay;
    LinearLayout titleLay;

    ActionBar ab;

    Anime anime;
    Player player;

    int animeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        AdView av = findViewById(R.id.guess_ad);
        AdRequest request = new AdRequest.Builder().build();
        av.loadAd(request);
        ia = AdShower.load(this);

        animeID = getIntent().getIntExtra("animeID", -1);
        if (animeID <0) finish();

        anime = new Anime(animeID, this);
        player = new Player();

        refreshBar();

        image = findViewById(R.id.guess_image);
        result = findViewById(R.id.guess_result);
        resultO = findViewById(R.id.guess_resultO);
        input = findViewById(R.id.guess_input);
        lay = findViewById(R.id.guess_input_lay);
        titleLay = findViewById(R.id.guess_titleLay);

        image.setImageResource(anime.image);

        if (player.isCompleted(animeID, this))
            showTitle();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean res = anime.hasTitle(input.getText().toString());
                boolean corrlang = !(!anime.getTitleLang(input.getText().toString()).equals("jp")&&player.hintPurchased(animeID, HINT_SHWENG, GuessActivity.this))
                        && !(anime.getTitleLang(input.getText().toString()).equals("jp")&&player.hintPurchased(animeID, HINT_SHWJP, GuessActivity.this));
                if (res && corrlang) {
                    complete();
                    Toast.makeText(GuessActivity.this, getString(R.string.guess_true, anime.bayannost), Toast.LENGTH_LONG).show();
                } else if (res) Toast.makeText(GuessActivity.this, R.string.guess_error_wronglang, Toast.LENGTH_LONG).show();
                else Log.d("input", "incorr");
            }
        });
    }

    private void complete() {
        player.setCompleted(animeID, GuessActivity.this);
        player.addScore(anime.bayannost, GuessActivity.this);
        showTitle();
        invalidateOptionsMenu();
        refreshBar();
    }

    private void refreshBar() {
        ab = getSupportActionBar();
        if (ab != null) ab.setTitle(getString(R.string.guess_activity_title, player.addScore(0, this), animeID +1));
    }

    public void next(View v) {
        if (!player.isCompleted(animeID, this)) {
            Toast.makeText(this, R.string.guess_lol, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Anime.getTotalCount(this)<= animeID +1) {
            Intent i = new Intent(GuessActivity.this, MainActivity.class);
            i.putExtra("end", true);
            startActivity(i);
            finish();
            return;
        }

        Intent intent = new Intent(GuessActivity.this, GuessActivity.class);
        intent.putExtra("animeID", animeID +1);
        startActivity(intent);
        ia.show();
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void showTitle() {
        resultO.setText(anime.originalTitle+" / "+anime.originalRomTitle);
        result.setText("");

        boolean first = true;
        for (int j=0; j<anime.displayTitles.size(); j++) {
            if (!anime.displayTitles.get(j)[1].equals(Locale.getDefault().getLanguage())&&!anime.displayTitles.get(j)[1].equals("en"))
                continue;
            if (!first) result.setText(result.getText().toString()+" / ");
            result.setText(result.getText().toString()+anime.displayTitles.get(j)[0]);
            first = false;
        }

        lay.setVisibility(View.GONE);
        titleLay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem info = menu.findItem(R.id.guess_menu_info);
        MenuItem hints = menu.findItem(R.id.guess_menu_hints);

        if (player.isCompleted(animeID, GuessActivity.this)) {
            info.setVisible(true);
            hints.setVisible(false);
        } else {
            info.setVisible(false);
            hints.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.guess, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(GuessActivity.this, MainActivity.class));
        ia.show();
        finish();
    }

    public void info(MenuItem item) {
        if (!player.isCompleted(animeID, this)) {
            Toast.makeText(this, R.string.guess_info_error_guessed, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(GuessActivity.this, InfoActivity.class);
        intent.putExtra("animeID", animeID);
        startActivity(intent);
    }

    public void hints(MenuItem item) {
        if (player.isCompleted(animeID, this)) {
            Toast.makeText(this, R.string.guess_hints_error_guessed, Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.guess_hints_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(GuessActivity.this);
                builder1.setTitle(getResources().getStringArray(R.array.guess_hints_array)[i])
                        .setMessage(String.format(getResources().getStringArray(R.array.guess_hints_desc_array)[i], HINT_COST[i]))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.guess_hints_purchase, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {purchaseHint(i);}
                        }).create().show();

            }
        }).setNegativeButton(android.R.string.cancel, null).create().show();
    }

    private void purchaseHint(int hint) {
        if (!player.hintPurchased(animeID, hint, this)) {
            if (player.addScore(0, this)<HINT_COST[hint]) {
                Toast.makeText(this, R.string.guess_hints_error_nopoints, Toast.LENGTH_LONG).show();
                return;
            }
        }
        
        boolean fail = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, null)
                .setTitle(getResources().getStringArray(R.array.guess_hints_array)[hint]);

        switch (hint) {
            case HINT_SKIP:
                complete();
                break;
            case HINT_SHWENG:
                String title = anime.getTitle("en", true);
                if (title==null) {
                    builder.setMessage(R.string.guess_hints_error_titlenotexist);
                    fail = true;
                } else setMessage(builder, title, hint);
                break;
            case HINT_SHWJP:
                String title1 = anime.getTitle("jp", true);
                if (title1==null) {
                    builder.setMessage(R.string.guess_hints_error_titlenotexist);
                    fail = true;
                } else setMessage(builder, title1, hint);
                break;
            case HINT_RANDENG:
                String title2 = anime.getTitle("en", true);
                if (title2==null) {
                    builder.setMessage(R.string.guess_hints_error_titlenotexist);
                    fail = true;
                } else setMessage(builder, shuffle(title2), hint);
                break;
            case HINT_RANDJP:
                String title3 = anime.getTitle("jp", true);
                if (title3==null) {
                    builder.setMessage(R.string.guess_hints_error_titlenotexist);
                    fail = true;
                } else setMessage(builder, shuffle(title3), hint);
                break;
            case HINT_DESC:
                String title4 = anime.getDescription(Locale.getDefault().getLanguage());
                if (title4==null) {
                    title4 = anime.getDescription("en");
                    if (title4==null) {
                        builder.setMessage(R.string.guess_hints_error_descnotexist);
                        fail=true;
                    } else setMessage(builder, title4, hint);
                } else setMessage(builder, title4, hint);
                break;
            case HINT_CHARS:
                StringBuilder title5 = new StringBuilder();
                for (int j=0; j<anime.characters.size(); j++) {
                    if (j>0) title5.append(", ");
                    title5.append(anime.characters.get(j));
                }
                if (anime.characters.size()<=0) {
                    builder.setMessage(R.string.guess_hints_error_charnotexist);
                    fail = true;
                } else setMessage(builder, title5.toString(), hint);
                break;
        }

        if (hint!= HINT_SKIP)
            builder.create().show();

        if (!fail && !player.hintPurchased(animeID, hint, this)) {
            player.purchaseHint(animeID, hint, this);
            player.addScore(-HINT_COST[hint], this);
        }

        refreshBar();
    }

    private void setMessage(AlertDialog.Builder builder, String title, int hint) {
        if (!player.hintPurchased(animeID, hint, this))
            builder.setMessage(getString(R.string.guess_hints_content_prefix, title));
        else builder.setMessage(title);
    }

    public String shuffle(String input){
        List<Character> characters = new ArrayList<>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }


}
