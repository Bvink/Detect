package com.tornado.detect;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.tornado.core.api.utils.Hiscore;
import com.tornado.core.api.utils.Timer;

public class ActivityHiScore extends Activity {

	protected int getLayoutId() {
		return R.layout.hiscore;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		Context context = getBaseContext();
		
		TextView t = (TextView) findViewById(R.id.HiScoreTextList);
		ArrayList<Integer> scores = Hiscore.getHiScore(false, context);
		if (scores != null) {
			t.setText(setScores(scores));
		}

		TextView x = (TextView) findViewById(R.id.HiScoreTextListTwo);
		ArrayList<Integer> xores = Hiscore.getHiScore(true, context);
		if (xores != null) {
			x.setText(setScores(xores));
		}
	}
	
	private String setScores(ArrayList<Integer> scores) {
		StringBuilder scoreList = new StringBuilder("");
		for (int score : scores) {
			String val = Timer.format(score);
			scoreList.append(val + "\n");
		}
		return scoreList.toString();
	}
}
