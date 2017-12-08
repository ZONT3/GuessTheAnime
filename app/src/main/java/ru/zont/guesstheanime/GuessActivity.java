package ru.zont.guesstheanime;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class GuessActivity extends AppCompatActivity {

    ImageView image;
    TextView result;
    TextView resultO;
    EditText input;
    LinearLayout lay;

    Anime anime;
    Player player;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        i = getIntent().getIntExtra("i", -1);
        if (i<0) {
            setResult(RESULT_CANCELED);
            finish();
        }

        anime = new Anime(i, this);
        player = new Player();

        image = (ImageView) findViewById(R.id.guess_image);
        result = (TextView) findViewById(R.id.guess_result);
        resultO = (TextView) findViewById(R.id.guess_resultO);
        input = (EditText) findViewById(R.id.guess_input);
        lay = (LinearLayout) findViewById(R.id.guess_input_lay);

        image.setImageResource(anime.image);

        if (player.isCompleted(i, this))
            showTitle();
    }

    public void enter(View v) {
        boolean res = anime.hasTitle(input.getText().toString());
        if (res) {
            showTitle();
            player.setCompleted(i, this);
        } else
            Toast.makeText(this, R.string.guess_wrong, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void showTitle() {
        resultO.setText(anime.originalTitle+" / "+anime.originalRomTitle);
        result.setText("");

        for (int j=0; j<anime.titles.size(); j++) {
            if (anime.titles.get(j)[1].equals(Locale.getDefault().toString())||anime.titles.get(j)[1].equals("en_US"))
                continue;
            if (j>0) result.setText(result.getText().toString()+" / ");
            result.setText(result.getText().toString()+anime.titles.get(j)[0]);
        }
    }

}
