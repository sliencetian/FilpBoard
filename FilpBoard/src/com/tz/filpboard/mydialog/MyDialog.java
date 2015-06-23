package com.tz.filpboard.mydialog;

import java.util.List;

import com.tz.filpboard.R;
import com.tz.filpboard.moedl.Music;
import com.tz.filpboard.musicadapter.MusicAdapter;
import com.tz.filpboard.switchview.SwitchView;
import com.tz.filpboard.switchview.SwitchView.OnSwitchChangeListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyDialog extends Dialog {

	private Context context;
	// 标题
	private String title;
	// 提示信息
	private String message;
	// 确认按钮名字
	private String confirmButtonName;
	// 取消按钮名字
	private String cacelButtonName;
	// 音乐信息
	private List<Music> musicList;

	private int flag = 0;// 0-对话框；1-提示框；2-设置框；3-关于

	private boolean isOpen;

	private String musicName;

	private MyDialogListener myDialogListener;

	private TipDialogListener tipDialogListener;

	private SettingDialogListener settingDialogListener;

	private SettingMusicLisener settingMusicLisener;

	private AboutFilpBoardListener aboutFilpBoardListener;

	/**
	 * @author Administrator 对话框接口
	 */
	public interface MyDialogListener {
		public void doConfirm();

		public void doCacel();
	}

	/**
	 * @author Administrator 提示框接口
	 */
	public interface TipDialogListener {
		void closeDialog();
	}

	/**
	 * @author Administrator 设置框接口
	 */
	public interface SettingDialogListener {
		void isMusic(boolean open);

		void clearCache();

		void saveData();

		void closeDialog();

		void setMusic(int flag);
	}

	/**
	 * 设置音乐接口
	 */
	public interface SettingMusicLisener {
		void setMusic(Music music);

		void closeDialog();
	}

	/**
	 * 关于接口
	 */
	public interface AboutFilpBoardListener {
		void aboutPosition(int position);

		void closeDialog();
	}

	public void setMyDialogListener(MyDialogListener myDialogListener) {
		this.myDialogListener = myDialogListener;
	}

	public void setTipDialogListener(TipDialogListener tipDialogListener) {
		this.tipDialogListener = tipDialogListener;
	}

	public void setSettingDialogListener(
			SettingDialogListener settingDialogListener) {
		this.settingDialogListener = settingDialogListener;
	}

	public void setSettingMusicLisener(SettingMusicLisener settingMusicLisener) {
		this.settingMusicLisener = settingMusicLisener;
	}

	public void setAboutFilpBoardListener(
			AboutFilpBoardListener aboutFilpBoardListener) {
		this.aboutFilpBoardListener = aboutFilpBoardListener;
	}

	public MyDialog(Context context, String title, String message,
			String confirmName, String cacelName) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.title = title;
		this.message = message;
		this.confirmButtonName = confirmName;
		this.cacelButtonName = cacelName;
	}

	public MyDialog(Context context, String title, String confirmName,
			String cacelName) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.title = title;
		this.confirmButtonName = confirmName;
		this.cacelButtonName = cacelName;
	}

	public MyDialog(Context context, int flag) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.flag = flag;
	}

	public MyDialog(Context context, int flag, List<Music> musicList) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.flag = flag;
		this.musicList = musicList;
	}

	public MyDialog(Context context, int flag, boolean isOpen, String musicName) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.flag = flag;
		this.isOpen = isOpen;
		this.musicName = musicName;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (flag == 0) {// 对话框
			initChatDialog();
		} else if (flag == 1) {// 提示框
			initTipDialog();
		} else if (flag == 2) {// 设置
			initSettingDialog();
		} else if (flag == 3) {// 关于
			initAboutDialog();
		} else if (flag == 4) {// 设置音乐
			initSetMusic();
		}

	}

	/**
	 * 显示音乐列表
	 */
	private void initSetMusic() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.music_list_dialog, null);
		setContentView(rootView);

		ImageView imageView = (ImageView) findViewById(R.id.iv_music_list_close);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingMusicLisener.closeDialog();
			}
		});

		ListView lv = (ListView) rootView.findViewById(R.id.lv_music_list);
		MusicAdapter adapter = new MusicAdapter(context, musicList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				settingMusicLisener.setMusic(musicList.get(position));
			}
		});
	}

	/**
	 * 关于
	 */
	private void initAboutDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.about_dialog, null);
		setContentView(rootView);

		ImageView imageView = (ImageView) findViewById(R.id.iv_about_close);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aboutFilpBoardListener.closeDialog();
			}
		});
		
		LinearLayout ll_guanwang = (LinearLayout) rootView
				.findViewById(R.id.ll_about_guanwang);
		ll_guanwang.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aboutFilpBoardListener.aboutPosition(1);
			}
		});
		LinearLayout ll_email = (LinearLayout) rootView
				.findViewById(R.id.ll_about_email);
		ll_email.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aboutFilpBoardListener.aboutPosition(2);
			}
		});
		LinearLayout ll_csdn = (LinearLayout) rootView
				.findViewById(R.id.ll_about_csdn);
		ll_csdn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aboutFilpBoardListener.aboutPosition(4);
			}
		});

	}

	TextView tv_musicName;

	/**
	 * 设置
	 */
	private void initSettingDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.seting_dialog, null);
		setContentView(rootView);

		ImageView imageView = (ImageView) findViewById(R.id.iv_setdialog_close);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingDialogListener.closeDialog();
			}
		});

		SwitchView switchView = (SwitchView) rootView
				.findViewById(R.id.sv_set_music);
		switchView.setSwitchStatus(isOpen);
		switchView.setOnSwitchChangeListener(new OnSwitchChangeListener() {

			@Override
			public void onSwitchChanged(boolean open) {
				settingDialogListener.isMusic(open);
			}
		});

		tv_musicName = (TextView) rootView
				.findViewById(R.id.tv_setting_musicname);
		tv_musicName.setText(musicName);

		Button bt_setMusic = (Button) rootView.findViewById(R.id.bt_set_music);
		bt_setMusic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingDialogListener.setMusic(1);
			}
		});

		Button bt_setDefault = (Button) rootView
				.findViewById(R.id.bt_set_default);
		bt_setDefault.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingDialogListener.setMusic(2);
			}
		});

		Button bt_saveData = (Button) rootView
				.findViewById(R.id.bt_set_savedata);
		bt_saveData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingDialogListener.saveData();
			}
		});

		Button bt_clearCache = (Button) rootView
				.findViewById(R.id.bt_set_clearcache);
		bt_clearCache.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingDialogListener.clearCache();
			}
		});
	}

	public void setMusicName(String musicName) {
		tv_musicName.setText(musicName);
	}

	/**
	 * 提示框
	 */
	private void initTipDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.tip_dialog, null);
		setContentView(rootView);

		ImageView imageView = (ImageView) findViewById(R.id.iv_tipdialog_close);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tipDialogListener.closeDialog();
			}
		});
	}

	/**
	 * 对话框
	 */
	private void initChatDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.mydialog, null);
		setContentView(rootView);

		TextView tv_title = (TextView) rootView
				.findViewById(R.id.tv_mydialog_title);
		TextView tv_message = (TextView) rootView
				.findViewById(R.id.tv_mydialog_message);
		Button bt_confirm = (Button) rootView
				.findViewById(R.id.bt_mydialog_confirm);
		Button bt_cacel = (Button) rootView
				.findViewById(R.id.bt_mydialog_cacel);

		tv_title.setText(title);
		tv_message.setText(message);
		bt_confirm.setText(confirmButtonName);
		bt_cacel.setText(cacelButtonName);

		bt_confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				myDialogListener.doConfirm();
			}
		});
		bt_cacel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				myDialogListener.doCacel();
			}
		});
	}

}
