package com.tornado.core.api.utils;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

import com.tornado.detect.constants.DetectConstants;
import com.tornado.detect.settings.DetectSettings;

public class Hiscore {

	public static void setHiScore(int i, Context context) {
		switch (DetectSettings.getDifficulty()) {
		case 0:
			setScore(i, "DetectEasyScoresWorld", "DetectEasyScores", context);
			break;
		case 1:
			setScore(i, "DetectNormalScoresWorld", "DetectNormalScores", context);
			break;
		case 2:
			setScore(i, "DetectHardScoresWorld", "DetectHardScores", context);
			break;
		default:
			break;
		}
	}
	
	private static void setScore(int i, String world, String normal, Context context) {
		TinyDB tinydb = new TinyDB(context);
		ArrayList<Integer> als;
		boolean worldstatus = DetectSettings.getWorld();
		als = (worldstatus) ? tinydb.getListInt(world, context) : tinydb
				.getListInt(normal, context);
		als.add(i);
		Collections.sort(als, Collections.reverseOrder());
		if (als.size() > DetectConstants.MAX_HISCORE) {
			als.remove(als.size() - 1);
		}
		if (worldstatus) {
			tinydb.putListInt(world, als, context);
		} else {
			tinydb.putListInt(normal, als, context);
		}
	}

	public static ArrayList<Integer> getHiScore(Boolean world, Context context) {
		TinyDB tinydb = new TinyDB(context);
		ArrayList<Integer> als = null;
		switch (DetectSettings.getDifficulty()) {
		case 0:
			als = world ? tinydb.getListInt("DetectEasyScoresWorld", context)
					: tinydb.getListInt("DetectEasyScores", context);
			break;
		case 1:
			als = world ? tinydb.getListInt("DetectNormalScoresWorld", context)
					: tinydb.getListInt("DetectNormalScores", context);
			break;
		case 2:
			als = world ? tinydb.getListInt("DetectHardScoresWorld", context)
					: tinydb.getListInt("DetectHardScores", context);
			break;
		default:
			break;
		}
		return als;
	}
}
