package ru.zont.guesstheanime;

public class InfoActivity extends AppCompatActivity {

	private int animeID;


	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_info);

		animeID = getIntent().getIntExtra("animeID", -1);
		if (animeID<0) {
			Toast.makeText(this, "ERROR: Anime ID was not defined");
			finish();
			return;
		}

		//TODO parser
	}
}