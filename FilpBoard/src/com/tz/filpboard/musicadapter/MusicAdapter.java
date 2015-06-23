package com.tz.filpboard.musicadapter;

import java.util.List;

import com.tz.filpboard.R;
import com.tz.filpboard.moedl.Music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {

	private List<Music> musicList;
	private LayoutInflater mInflater;

	public MusicAdapter(Context context, List<Music> musicList) {
		mInflater = LayoutInflater.from(context);
		this.musicList = musicList;
	}

	@Override
	public int getCount() {
		return musicList == null ? 0 : musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		Music music = musicList.get(position);
		LinearLayout rootView = (LinearLayout) mInflater.inflate(
				R.layout.musit_list_item, null);
		TextView tv_musicName = (TextView) rootView
				.findViewById(R.id.tv_musit_list_musicname);
		tv_musicName.setText(music.getName());

		TextView tv_musicArtist = (TextView) rootView
				.findViewById(R.id.tv_musit_list_musicartist);
		tv_musicArtist.setText(music.getArtist());

		return rootView;
	}

}
