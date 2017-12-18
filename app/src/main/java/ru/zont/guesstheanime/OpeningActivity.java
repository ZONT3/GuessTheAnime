package ru.zont.guesstheanime;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class OpeningActivity extends AppCompatActivity implements TextWatcher, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{
    private static final int[] HINT_COST = {60, 40, 40, 30};
    private static final int HINT_SKIP = 0;
    private static final int HINT_SONG = 1;
    private static final int HINT_ART = 2;
    private static final int HINT_DESC = 3;

    private static final int TICK = 10;

    static ArrayList<Integer> played = new ArrayList<>();

    private InterstitialAd ia;

    private ImageView root;
    private TextView name;
    private TextView oname;
    private TextView song;
    private Button play;
    private ProgressBar pb;
    private EditText input;
    private Button next;
    private LinearLayout loading;

    private boolean guessed = false;
    private boolean playSt = false;

    ActionBar ab;

    private Anime anime;
    private Opening opening;
    private Player player;

    private MediaPlayer mp;
    Refresher refresher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        AdView av = findViewById(R.id.op_ad);
        AdRequest request = new AdRequest.Builder().build();
        av.loadAd(request);
        ia = AdShower.load(this);

        root = findViewById(R.id.op_image);
        name = findViewById(R.id.op_titles);
        oname = findViewById(R.id.op_oname);
        song = findViewById(R.id.op_song);
        play = findViewById(R.id.op_play);
        pb = findViewById(R.id.op_pb);
        input = findViewById(R.id.op_input);
        next = findViewById(R.id.op_next);
        loading = findViewById(R.id.op_loading);

        if (Opening.getAll(this).size()<=played.size()) {
            MainActivity.endDialog(this);
            return;
        }

        player = new Player();
        if (getIntent().getIntExtra("id", -1)<0) {
            Opening op;
            do op = Opening.getAll(this).get(new Random().nextInt(Opening.getAll(this).size()));
            while (played.contains(op.id));
            opening = op;
            anime = new Anime(opening.animeID, this);
            Log.d("GTO", anime.originalTitle + " / " + anime.originalRomTitle);
        } else opening = Opening.get(getIntent().getIntExtra("id", -1));

        refreshLayout();
        input.addTextChangedListener(this);

        try {
            play.setEnabled(false);
            loading.setVisibility(View.VISIBLE);

            mp = new MediaPlayer();
            mp.setDataSource(opening.resource);
            mp.setOnPreparedListener(this);
            mp.setOnErrorListener(this);
            mp.prepareAsync();
        } catch (IOException e) {e.printStackTrace();}

        refreshBar();
    }

    @SuppressLint("SetTextI18n")
    private void refreshLayout() {
        oname.setText(anime.originalTitle + "/" +anime.originalRomTitle);
        name.setText(anime.getDisplayTitles());
        song.setText(opening.song);
        root.setImageResource(anime.image);

        if (guessed) {
            name.setVisibility(View.VISIBLE);
            oname.setVisibility(View.VISIBLE);
            song.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            input.setVisibility(View.GONE);
            root.setVisibility(View.VISIBLE);
        } else {
            name.setVisibility(View.GONE);
            oname.setVisibility(View.GONE);
            song.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            input.setVisibility(View.VISIBLE);
            root.setVisibility(View.INVISIBLE);
        }
    }

    public void onPlayClick(View v) {
        if (!pb.isIndeterminate()) {
            playSt = !playSt;
            play.setBackgroundResource(playSt ? R.drawable.button_pause
                    : R.drawable.button_play);
        } else {
            mp.seekTo((int) opening.start);
            playSt = true;
            play.setBackgroundResource(R.drawable.button_pause);
        }

        refreshPlayer();
        Log.d("GTO", "POS:" + mp.getCurrentPosition());
    }

    private void refreshPlayer() {
        try {
            if (opening.end <= 0 || guessed)
                pb.setMax(mp.getDuration());
            else pb.setMax((int) (opening.end - opening.start));
            pb.setProgress((int) (mp.getCurrentPosition() - opening.start));

            if (!pb.isIndeterminate() && mp.getCurrentPosition() >= opening.end && !guessed && opening.end > 0) {
                pb.setIndeterminate(true);
                play.setBackgroundResource(R.drawable.button_replay);
            } else if (pb.isIndeterminate() && !(mp.getCurrentPosition() >= opening.end && !guessed && opening.end > 0))
                pb.setIndeterminate(false);

            setPlay(playSt && (mp.getCurrentPosition() < opening.end || guessed || opening.end <= 0));
        } catch (Exception e) {e.printStackTrace();}
    }

    private void setPlay(boolean play) {
        if (play && !mp.isPlaying()) mp.start();
        else if (!play && mp.isPlaying()) mp.pause();
    }

    private void refreshBar() {
        ab = getSupportActionBar();
        if (ab != null) ab.setTitle(getString(R.string.op_title, played.size()+1, Opening.getAll(this).size(), player.addScore(0, this)));
    }

    @SuppressLint("StaticFieldLeak")
    private class Refresher extends AsyncTask<Void, Void, Void> {
        boolean isRunning = false;

        @Override
        protected Void doInBackground(Void... voids) {
            isRunning = true;
            while (!isCancelled()) {
                try {
                    publishProgress();
                    Thread.sleep(TICK);
                } catch (InterruptedException e) {e.printStackTrace();}
            }
            isRunning = false;
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
             refreshPlayer();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        refresher = new Refresher();
        refresher.execute();
        mediaPlayer.seekTo((int) opening.start);
        loading.setVisibility(View.GONE);
        play.setEnabled(true);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (refresher!=null) refresher.cancel(false);
        mp.release();
        Toast.makeText(this, R.string.op_err, Toast.LENGTH_LONG).show();
        finish();
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        boolean res = anime.hasTitle(input.getText().toString());
        if (res && !player.isCompletedOps(opening.id, OpeningActivity.this)) {
            guessed = true;
            player.setCompletedOps(opening.id, OpeningActivity.this);
            player.addScore(opening.score, OpeningActivity.this);
            refreshLayout();
            refreshBar();
            invalidateOptionsMenu();
            play.setBackgroundResource(playSt ? R.drawable.button_pause
                    : R.drawable.button_play);
            play.setAlpha((float) 0.75);
            Toast.makeText(this, getString(R.string.guess_true, opening.score), Toast.LENGTH_LONG).show();
        } else if (res) {
            guessed = true;
            refreshLayout();
            refreshBar();
            invalidateOptionsMenu();
            play.setBackgroundResource(playSt ? R.drawable.button_pause
                    : R.drawable.button_play);
            play.setAlpha((float) 0.75);
            Toast.makeText(this, R.string.op_truebut, Toast.LENGTH_LONG).show();
        } else Log.d("input", "incorr");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem info = menu.findItem(R.id.op_menu_info);
        MenuItem hints = menu.findItem(R.id.op_menu_hints);

        if (guessed) {
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
        i.inflate(R.menu.opening, menu);
        return true;
    }

    public void info(MenuItem item) {
        if (!guessed) {
            Toast.makeText(this, R.string.guess_info_error_guessed, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(OpeningActivity.this, InfoActivity.class);
        intent.putExtra("animeID", opening.animeID);
        startActivity(intent);
    }

    public void hints(MenuItem item) {
        if (guessed) {
            Toast.makeText(this, R.string.guess_hints_error_guessed, Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.op_hints_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(OpeningActivity.this);
                builder1.setTitle(getResources().getStringArray(R.array.op_hints_array)[i])
                        .setMessage(String.format(getResources().getStringArray(R.array.op_hints_desc_array)[i], HINT_COST[i]))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.guess_hints_purchase, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {purchaseHintOps(i);}
                        }).create().show();

            }
        }).setNegativeButton(android.R.string.cancel, null).create().show();
    }
    
    private void purchaseHintOps(int hint) {
        if (!player.hintPurchasedOps(opening.id, hint, this)) {
            if (player.addScore(0, this)<HINT_COST[hint]) {
                Toast.makeText(this, R.string.guess_hints_error_nopoints, Toast.LENGTH_LONG).show();
                return;
            }
        }

        boolean fail = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, null)
                .setTitle(getResources().getStringArray(R.array.op_hints_array)[hint]);

        switch (hint) {
            case HINT_SKIP:
                guessed = true;
                refreshLayout();
                invalidateOptionsMenu();
                play.setBackgroundResource(playSt ? R.drawable.button_pause
                        : R.drawable.button_play);
                break;
            case HINT_SONG:
                song.setVisibility(View.VISIBLE);
                break;
            case HINT_ART:
                root.setVisibility(View.VISIBLE);
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
        }

        if (hint!= HINT_SKIP && hint!= HINT_SONG && hint!= HINT_ART)
            builder.create().show();

        if (!fail && !player.hintPurchasedOps(opening.id, hint, this)) {
            player.purchaseHintOps(opening.id, hint, this);
            player.addScore(-HINT_COST[hint], this);
        }

        refreshBar();
    }

    private void setMessage(AlertDialog.Builder builder, String title, int hint) {
        if (!player.hintPurchasedOps(opening.id, hint, this))
            builder.setMessage(getString(R.string.guess_hints_content_prefix, title));
        else builder.setMessage(title);
    }

    public void onNext(View v) {
        OpeningActivity.played.add(opening.id);
        if (refresher!=null) {
            refresher.cancel(false);
            waitForRefresher();
        }
        releaseMP();
        startActivity(new Intent(OpeningActivity.this, OpeningActivity.class));
        ia.show();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (refresher!=null) {
            refresher.cancel(false);
            waitForRefresher();
        }
        releaseMP();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }

    private void releaseMP() {
        if (mp != null) {
            try {
                mp.release();
                mp = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForRefresher() {
        try {
            while (refresher.isRunning)
                Thread.sleep(TICK*2);
        } catch (Exception e) {e.printStackTrace();}
    }
}
