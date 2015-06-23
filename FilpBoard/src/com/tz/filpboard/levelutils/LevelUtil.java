package com.tz.filpboard.levelutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tz.filpboard.MainActivity;
import com.tz.filpboard.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class LevelUtil {

	private static final int[] LEVEL = new int[] { 0, R.raw.level_1,
			R.raw.level_2, R.raw.level_3, R.raw.level_4, R.raw.level_5,
			R.raw.level_6, R.raw.level_7, R.raw.level_8, R.raw.level_9,
			R.raw.level_10, R.raw.level_11, R.raw.level_12, R.raw.level_13,
			R.raw.level_14, R.raw.level_15, R.raw.level_16, R.raw.level_17,
			R.raw.level_18, R.raw.level_19, R.raw.level_20, R.raw.level_21,
			R.raw.level_22, R.raw.level_23, R.raw.level_24, R.raw.level_25,
			R.raw.level_26, R.raw.level_27, R.raw.level_28, R.raw.level_29,
			R.raw.level_30 };

	public static List<Integer> getLevelData(Context context, int level) {
		List<Integer> levelData = new ArrayList<Integer>();
		InputStream inputStream = ((MainActivity) context).getResources()
				.openRawResource(LEVEL[level]);
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "gbk");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				String[] strings = line.trim().split(" ");
				for (int i = 0; i < strings.length; i++)
					levelData.add(Integer.parseInt(strings[i]));
			}
			return levelData;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 返回游戏关卡进度
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Integer> getGamePrggress(Context context) {
		Map<Integer, Integer> gameProgress;
		MainActivity mainActivity = (MainActivity) context;
		SharedPreferences spData = mainActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		String prs = spData.getString("GAME_PROGRESS", "");
		if (prs.equals("") || prs == null) {
			prs = "1-1_";
			for (int i = 2; i < LEVEL.length; i++) {
				prs = prs + i + "-0_";
			}
			prs = prs.substring(0, prs.length() - 1);
			gameProgress = strToMap(prs);
		} else {
			gameProgress = strToMap(prs);
		}
		return gameProgress;
	}

	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Integer> strToMap(String str) {
		Map<Integer, Integer> gameProgress = new HashMap<Integer, Integer>();
		String[] strings = str.split("_");
		for (int i = 0; i < strings.length; i++) {
			String[] strings2 = strings[i].split("-");
			int key = Integer.parseInt(strings2[0]);
			int value = Integer.parseInt(strings2[1]);
			gameProgress.put(key, value);
		}

		return gameProgress;
	}

	/**
	 * 保存关卡进度
	 * 
	 * @param gameProgress
	 *            要保存的进度
	 */
	public static void saveGameProgress(Context context,
			Map<Integer, Integer> gameProgress) {
		MainActivity mainActivity = (MainActivity) context;
		SharedPreferences spData = mainActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		String gameProgressStr = "";
		for (Map.Entry<Integer, Integer> entry : gameProgress.entrySet()) {
			gameProgressStr += entry.getKey() + "-" + entry.getValue() + "_";
		}
		gameProgressStr = gameProgressStr.substring(0,
				gameProgressStr.length() - 1);
		spData.edit().putString("GAME_PROGRESS", gameProgressStr).commit();
	}
}
