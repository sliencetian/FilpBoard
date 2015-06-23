package com.tz.filpboard.musicService;

import com.tz.filpboard.R;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

public class MusicService extends Service {

	private MediaPlayer mp;
	private final Uri musicTableForSD = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private final String musicTitle = MediaStore.Audio.AudioColumns.TITLE;
	private final String musicId = MediaStore.Audio.Media._ID;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mp = MediaPlayer.create(this, R.raw.filp);
		mp.setLooping(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			String state = intent.getStringExtra("state");
			if (state.equals("pause")) {
				onPause();
			} else {
				play();
			}
			return super.onStartCommand(intent, flags, startId);
		} catch (Exception e) {
		}
		boolean playing = false;
		try {
			playing = intent.getBooleanExtra("playing", false);
		} catch (Exception e) {
			stopSelf();
		}
		try {
			String musicName = intent.getStringExtra("musicName");
			if (!musicName.equals("高山流水")) {
				playBackgroundMusic(musicName);
			} else {
				if (mp.isPlaying()) {
					mp.stop();
					mp = null;
				}
				mp = MediaPlayer.create(this, R.raw.filp);
				mp.setLooping(true);
			}
		} catch (Exception e) {
		}
		if (mp == null)
			return super.onStartCommand(intent, flags, startId);
		if (playing) {
			mp.start();
		} else {
			mp.pause();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void onPause() {
		if (mp.isPlaying())
			mp.pause();
	}

	private void play() {
		if (!mp.isPlaying())
			mp.start();
	}

	private void playBackgroundMusic(String musicName) {
		if (mp.isPlaying()) {
			mp.stop();
			mp = null;
		}
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(musicTableForSD, new String[] {
				musicId, musicTitle }, musicTitle + " LIKE ?",
				new String[] { musicName }, null);
		if (cursor.moveToNext()) {
			int position = cursor.getInt(cursor.getColumnIndex(musicId));
			Uri uri = Uri.withAppendedPath(musicTableForSD, "/" + position);
			mp = MediaPlayer.create(this, uri);
			mp.setLooping(true);
		}
		cursor.close();
	}

}
