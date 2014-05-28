package cl.ipp.katbag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private final int TIME_SPLASH = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}

		}, TIME_SPLASH);
	}
}
