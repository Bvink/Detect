package com.tornado.detect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tornado.detect.settings.DetectSettings;

public class MainActivity extends Activity {

	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		instance = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		findViewById(R.id.imageView1).postDelayed(new Runnable() {
			@Override
			public void run() {
				instance.setContentView(R.layout.main);
			}
		}, 2000);

	}

	public void openEasy(View view) {
		DetectSettings.setDifficulty(0);
		DetectSettings.setWorld(false);
		Intent intent = new Intent(this, ActivityMap.class);
		startActivity(intent);
	}

	public void openNormal(View view) {
		DetectSettings.setDifficulty(1);
		DetectSettings.setWorld(false);
		Intent intent = new Intent(this, ActivityMap.class);
		startActivity(intent);
	}

	public void openHard(View view) {
		DetectSettings.setDifficulty(2);
		DetectSettings.setWorld(false);
		Intent intent = new Intent(this, ActivityMap.class);
		startActivity(intent);
	}

	public void openEasySpiral(View view) {
		DetectSettings.setDifficulty(0);
		DetectSettings.setWorld(true);
		Intent intent = new Intent(this, ActivityMap.class);
		startActivity(intent);
	}

	public void openNormalSpiral(View view) {
		DetectSettings.setDifficulty(1);
		DetectSettings.setWorld(true);
		Intent intent = new Intent(this, ActivityMap.class);
		startActivity(intent);
	}

	public void openHardSpiral(View view) {
		DetectSettings.setDifficulty(2);
		DetectSettings.setWorld(true);
		Intent intent = new Intent(this, ActivityMap.class);
		startActivity(intent);
	}

	public void openEasyH(View view) {
		DetectSettings.setDifficulty(0);
		Intent intent = new Intent(this, ActivityHiScore.class);
		startActivity(intent);
	}

	public void openNormalH(View view) {
		DetectSettings.setDifficulty(1);
		Intent intent = new Intent(this, ActivityHiScore.class);
		startActivity(intent);
	}

	public void openHardH(View view) {
		DetectSettings.setDifficulty(2);
		Intent intent = new Intent(this, ActivityHiScore.class);
		startActivity(intent);
	}

}