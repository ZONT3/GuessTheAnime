package ru.zont.guesstheanime;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class InfoActivity extends AppCompatActivity {

    private Anime anime;

	private TextView titles;
	private TextView desc;
	private TextView chars;
	private ToggleButton showall;

	private String lang;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_info);

        int animeID = getIntent().getIntExtra("animeID", -1);
		if (animeID <0) {
			Toast.makeText(this, "ERROR: Anime ID was not defined", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		anime = new Anime(animeID, this);

		lang = Locale.getDefault().getLanguage();

		titles = findViewById(R.id.info_titles);
		desc = findViewById(R.id.info_desc);
		chars = findViewById(R.id.info_chars);
		showall = findViewById(R.id.info_showall);

		AdView ad = findViewById(R.id.info_ad);
		AdRequest request = new AdRequest.Builder().build();
		ad.loadAd(request);

		if (anime.titles.size()==anime.displayTitles.size())
		    showall.setVisibility(View.GONE);
		else showall.setVisibility(View.VISIBLE);

		parseTitles();
		parseChars();
		parseDesc();

		showall.setOnCheckedChangeListener(showallListener);
	}

	private void parseTitles() {
	    titles.setText(String.format("%s | %s\n", anime.originalTitle, anime.originalRomTitle));

		ArrayList<String[]> ts = anime.displayTitles;
		if (showall.isChecked()) ts = anime.titles;

		for (int i=0; i<ts.size(); i++) {
			if (i>0) titles.setText(titles.getText()+" / ");
			titles.setText(titles.getText()+String.format("[%s] %s", ts.get(i)[1].toUpperCase(), ts.get(i)[0]));
		}
	}

	private void parseDesc() {
	    this.desc.setText("");

		String desc = anime.getDescription(lang);
		if (desc == null) desc = anime.getDescription("en");
		if (desc == null) desc = getString(R.string.info_descnotex);
		this.desc.setText(desc);
	}

	private void parseChars() {
	    this.chars.setText("");

		ArrayList<String> chars = anime.characters;
		StringBuilder string = new StringBuilder();
		if (chars.size()<=0) string = new StringBuilder(getString(R.string.info_charsdoesnotex));
		for (String chr : chars) string.append(chr).append("\n");
		this.chars.setText(string);
	}

	private CompoundButton.OnCheckedChangeListener showallListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			parseTitles();
		}
	};
}