package com.tz.filpboard.datautils;

import java.util.ArrayList;
import java.util.List;

import com.tz.filpboard.MainActivity;
import com.tz.filpboard.guide.WelcomeActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUtils {
	/**
	 * 保存当前关卡数据
	 * 
	 * @param levelData
	 *            当前关卡数据
	 * @return 成功返回true，失败返回false
	 */
	public static boolean saveLevelData(Context context, List<Integer> levelData) {
		MainActivity mainActivity = (MainActivity) context;
		SharedPreferences spData = mainActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		if (levelData.size() < 2) {
			spData.edit().putString("levelDataStr", levelData.get(0) + "")
					.commit();
			return true;
		}
		String levelDataStr = "";
		levelDataStr = levelData.get(0) + "_" + levelData.get(1) + "_";
		for (int i = 2; i < levelData.size() - 2; i++) {
			levelDataStr = levelDataStr + levelData.get(i) + "_";
		}
		levelDataStr = levelDataStr + levelData.get(levelData.size() - 2) + "_"
				+ levelData.get(levelData.size() - 1);
		spData.edit().putString("levelDataStr", levelDataStr).commit();
		return false;
	}

	/**
	 * 获取游戏记录
	 * 
	 * @param context
	 * @return
	 */
	public static String getLevelData(Context context) {
		WelcomeActivity welcomeActivity = (WelcomeActivity) context;
		SharedPreferences spData = welcomeActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		String levelDataStr = spData.getString("levelDataStr", "");
		if (levelDataStr.equals("") || levelDataStr == null) {
			return null;
		} else {
			return levelDataStr;
		}
	}

	/**
	 * 将字符串转换成list集合
	 * 
	 * @param levelDataStr
	 *            关卡的字符串数据
	 * @return
	 */
	public static List<Integer> levelDataStrToList(String levelDataStr) {
		if (levelDataStr == null || levelDataStr.equals("")) {
			return null;
		} else {
			List<Integer> levelData;
			try {
				levelData = new ArrayList<Integer>();
				String[] levelDataStrings = levelDataStr.split("_");
				int i;
				for (i = 2; i < levelDataStrings.length - 2; i++) {
					levelData.add(Integer.parseInt(levelDataStrings[i]));
				}
				levelData.add(Integer.parseInt(levelDataStrings[0]));
				levelData.add(Integer.parseInt(levelDataStrings[1]));
				levelData.add(Integer.parseInt(levelDataStrings[i]));
				levelData.add(Integer.parseInt(levelDataStrings[i + 1]));
			} catch (Exception e) {
				levelData = new ArrayList<Integer>();
				levelData.add(Integer.parseInt(levelDataStr));
			}
			return levelData;
		}
	}

	/**
	 * 获取是否播放音乐
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getIsMusic(Context context) {
		MainActivity welcomeActivity = (MainActivity) context;
		SharedPreferences spData = welcomeActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		boolean isMusic = spData.getBoolean("IS_MUSIC", true);
		return isMusic;
	}

	/**
	 * 保存游戏音乐设置
	 * 
	 * @param context
	 * @param isMusic
	 * @return
	 */
	public static boolean saveIsMusic(Context context, boolean isMusic) {
		MainActivity welcomeActivity = (MainActivity) context;
		SharedPreferences spData = welcomeActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		boolean b = spData.edit().putBoolean("IS_MUSIC", isMusic).commit();
		return b;
	}

	/**
	 * 获取音乐名称
	 * 
	 * @return 音乐名称
	 */
	public static String getMusicName(Context context) {
		MainActivity welcomeActivity = (MainActivity) context;
		SharedPreferences spData = welcomeActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		String musicName = spData.getString("MUSIC_NAME", "高山流水");
		return musicName;
	}

	/**
	 * 保存游戏音乐名称
	 * 
	 * @param context
	 * @param musicName
	 *            音乐名称
	 * @return
	 */
	public static boolean saveMusicName(Context context, String musicName) {
		MainActivity mainActivity = (MainActivity) context;
		SharedPreferences spData = mainActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		boolean b = spData.edit().putString("MUSIC_NAME", musicName).commit();
		return b;
	}
	
	
	/**
	 * 清除缓存
	 * 
	 * @param context
	 * @return
	 */
	public static boolean clearCache(Context context) {
		MainActivity welcomeActivity = (MainActivity) context;
		SharedPreferences spData = welcomeActivity.getSharedPreferences(
				"FilpBoard", Context.MODE_PRIVATE);
		Editor editor = spData.edit();
		editor.putString("levelDataStr", "");
		editor.putString("GAME_PROGRESS", "");

		return editor.commit();
	}

}
