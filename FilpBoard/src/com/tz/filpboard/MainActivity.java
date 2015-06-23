package com.tz.filpboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tz.filpboard.customertoast.CustomToast;
import com.tz.filpboard.datautils.DataUtils;
import com.tz.filpboard.levelutils.LevelUtil;
import com.tz.filpboard.moedl.Music;
import com.tz.filpboard.musicService.MusicService;
import com.tz.filpboard.mydialog.MyDialog;
import com.tz.filpboard.mydialog.MyDialog.AboutFilpBoardListener;
import com.tz.filpboard.mydialog.MyDialog.MyDialogListener;
import com.tz.filpboard.mydialog.MyDialog.SettingDialogListener;
import com.tz.filpboard.mydialog.MyDialog.SettingMusicLisener;
import com.tz.filpboard.mydialog.MyDialog.TipDialogListener;
import com.tz.filpboard.view.GameFilpBoardLayout;
import com.tz.filpboard.view.GameFilpBoardLayout.GameFilpBoardListener;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GameFilpBoardLayout mGameFilpBoardLayout;

	// �Ƿ񲥷����ֱ���
	private boolean isMusic = true;
	// ����������
	private String musicName = "";
	// �����ļ�
	private List<Music> musicList;
	// �Ƿ񱣴�����
	private boolean isSaveLevelData = false;
	// ����
	private TextView tv_steps;
	// �ؿ���
	private TextView tv_level;
	// ��ʾ
	private TextView tv_explan;
	// ��������
	private Button bt_saveData;
	// ˢ�µ�ǰ�ؿ�
	private Button bt_newGame;
	// �����ı䶯��
	private Animation stepsAnimation;
	// ��ǰ�ؿ�
	private int currLevel = 1;
	private TextView tv_level_anim;
	// ����
	private int steps = 0;
	// �ؿ�����
	private Map<Integer, Integer> gameProgress;
	// ��ǰ�ؿ�����
	public static List<Integer> levelData;
	// ѡ��ؿ�
	private RelativeLayout rl_chooseLevel;
	private ImageView iv_chooseLevel_lift;
	// �ؿ�ID
	private static final int[] chooseLevelId = new int[] { 0,
			R.id.bt_chooselevel_1, R.id.bt_chooselevel_2,
			R.id.bt_chooselevel_3, R.id.bt_chooselevel_4,
			R.id.bt_chooselevel_5, R.id.bt_chooselevel_6,
			R.id.bt_chooselevel_7, R.id.bt_chooselevel_8,
			R.id.bt_chooselevel_9, R.id.bt_chooselevel_10,
			R.id.bt_chooselevel_11, R.id.bt_chooselevel_12,
			R.id.bt_chooselevel_13, R.id.bt_chooselevel_14,
			R.id.bt_chooselevel_15, R.id.bt_chooselevel_16,
			R.id.bt_chooselevel_17, R.id.bt_chooselevel_18,
			R.id.bt_chooselevel_19, R.id.bt_chooselevel_20,
			R.id.bt_chooselevel_21, R.id.bt_chooselevel_22,
			R.id.bt_chooselevel_23, R.id.bt_chooselevel_24,
			R.id.bt_chooselevel_25, R.id.bt_chooselevel_26,
			R.id.bt_chooselevel_27, R.id.bt_chooselevel_28,
			R.id.bt_chooselevel_29, R.id.bt_chooselevel_30 };
	// ѡ��ؿ���Button
	private Button[] bt_chooseLevel = new Button[chooseLevelId.length];

	private int flag;
	// ��Ļ���
	private int mWidth;

	// �˵�
	private Button bt_menu;
	// ����
	private Button bt_set;
	// ����
	private Button bt_about;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ������Ϸ���������
		List<Integer> levelDataTemp = initLevelData();
		initGameData();
		setContentView(R.layout.activity_main);
		isMusic = DataUtils.getIsMusic(MainActivity.this);
		// ��ʼ���ؼ�
		findViews();
		// ��ʼ������
		initAnim();
		// ��ʼ������
		startMusic();
		// ��ȡ��Ļ���
		mWidth = getWindowManager().getDefaultDisplay().getWidth();
		// ���ݼ��ص���Ϸ���ݳ�ʼ������
		initBoardData(levelDataTemp);
	}

	private void initGameData() {
		gameProgress = LevelUtil.getGamePrggress(MainActivity.this);
		musicName = DataUtils.getMusicName(MainActivity.this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				musicList = getMusicList();
			}
		}).start();

	}

	/**
	 * ��ʼ������
	 */
	private void startMusic() {
		if (isMusic) {
			Intent intent = new Intent(MainActivity.this, MusicService.class);
			intent.putExtra("playing", isMusic);
			intent.putExtra("musicName", musicName);
			startService(intent);
		}
	}

	/**
	 * ֹͣ����
	 */
	public void stopMusic() {
		Intent intent = new Intent(MainActivity.this, MusicService.class);
		stopService(intent);
	}

	private void initBoardData(List<Integer> levelDataTemp) {
		tv_level.setText(currLevel + "");
		tv_steps.setText(steps + "");
		if (flag == 2) {
			mGameFilpBoardLayout.setLineAndColmun(
					levelData.get(levelData.size() - 2),
					levelData.get(levelData.size() - 1));
		} else if (flag == 3) {
			mGameFilpBoardLayout.setSteps(steps);
			mGameFilpBoardLayout.setLineAndColmun(
					levelDataTemp.get(levelDataTemp.size() - 4),
					levelDataTemp.get(levelDataTemp.size() - 3));
		}
		showTipDialog();
	}

	private List<Integer> initLevelData() {
		String levelDataStr = getIntent().getStringExtra("levelDataStr");
		List<Integer> levelDataTemp = DataUtils
				.levelDataStrToList(levelDataStr);
		if (levelDataTemp == null) {
			initLevelData(currLevel);
		} else if (levelDataTemp.size() < 2) {
			flag = 2;
			currLevel = levelDataTemp.get(0);
			initLevelData(currLevel);
		} else {
			flag = 3;
			levelData = levelDataTemp;
			currLevel = levelDataTemp.get(levelDataTemp.size() - 1);
			steps = levelDataTemp.get(levelDataTemp.size() - 2);
		}
		return levelDataTemp;
	}

	/**
	 * ��ʼ���ܿ�����
	 */
	private void initLevelData(int level) {
		if (levelData != null)
			levelData = null;
		try {
			levelData = LevelUtil.getLevelData(MainActivity.this, level);
		} catch (Exception e) {

		}
	}

	private void initAnim() {
		stepsAnimation = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.text_anim);
	}

	private void findViews() {
		tv_steps = (TextView) findViewById(R.id.tv_steps);
		tv_level = (TextView) findViewById(R.id.tv_level);
		tv_explan = (TextView) findViewById(R.id.tv_main_explain);
		bt_saveData = (Button) findViewById(R.id.bt_main_save);
		bt_newGame = (Button) findViewById(R.id.bt_main_newgame);
		tv_level_anim = (TextView) findViewById(R.id.tv_level_anim);
		// ����
		bt_set = (Button) findViewById(R.id.bt_main_set);
		bt_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ��ʾ���öԻ���
				showSettingDialog();
			}
		});
		// ����
		bt_about = (Button) findViewById(R.id.bt_main_about);
		bt_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showAboutDialog();
			}
		});
		bt_menu = (Button) findViewById(R.id.bt_main_menu);
		bt_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (chooseLevelIsLook) {
					chooseLevelOut();
				} else {
					chooseLevelIn();
				}
			}
		});
		iv_chooseLevel_lift = (ImageView) findViewById(R.id.iv_main_chooselevel_lift);
		iv_chooseLevel_lift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				chooseLevelOut();
			}
		});
		rl_chooseLevel = (RelativeLayout) findViewById(R.id.rl_main_chooselevel);
		rl_chooseLevel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				chooseLevelOut();
			}
		});
		/*
		 * rl_chooseLevel.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View arg0, MotionEvent e) {
		 * 
		 * toucherListener(e); return false; } });
		 */
		for (int i = 1; i < chooseLevelId.length; i++) {
			bt_chooseLevel[i] = (Button) findViewById(chooseLevelId[i]);
			if (gameProgress.get(i) == 1) {
				awakeLevel(i);
			} else {
				bt_chooseLevel[i].setBackgroundResource(R.drawable.star_false);
			}
		}
		mGameFilpBoardLayout = (GameFilpBoardLayout) findViewById(R.id.gfbl_main_game_filpboard);
		mGameFilpBoardLayout.setmFilpBoardListener(new GameFilpBoardListener() {

			/*
			 * ��Ϸ�ɹ��ص��÷���
			 */
			@Override
			public void success() {
				successDialog();
			}

			/*
			 * �������ӻص��÷���
			 */
			@Override
			public void stepChange(int step) {
				steps = step;
				addStep(step);
			}

		});
		mGameFilpBoardLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent e) {
				if (chooseLevelIsLook) {
					return false;
				}
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					firstX = e.getX();
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					secondX = e.getX();
					float result = firstX - secondX;
					firstX = 0;
					secondX = 0;
					if (result > 120) {
						chooseLevelIn();
					}
				}
				return false;
			}
		});
		// ����һ��
		bt_saveData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (steps != 0 && !mGameFilpBoardLayout.fallBack()) {
					CustomToast.showToast(MainActivity.this, "ֻ�ܻ���һ��", 1500);
				}
			}
		});
		// ���¿�ʼ��ǰ�ؿ�
		bt_newGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				restart();
			}
		});
		ScrollView sl = (ScrollView) findViewById(R.id.sl_main_level);
		sl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent e) {
				toucherListener(e);
				return false;
			}
		});
	}

	private void toucherListener(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			firstX = e.getX();
		} else if (e.getAction() == MotionEvent.ACTION_UP) {
			secondX = e.getX();
			float result = secondX - firstX;
			firstX = 0;
			secondX = 0;
			if (result > 120) {
				chooseLevelOut();
			}
		}
	}

	/**
	 * ��ʾ���ڶԻ���
	 */
	protected void showAboutDialog() {
		final MyDialog aboutDialog = new MyDialog(MainActivity.this, 3);
		aboutDialog.setAboutFilpBoardListener(new AboutFilpBoardListener() {

			@Override
			public void aboutPosition(int position) {
				switch (position) {
				case 1:// ����
					openBrowser("http://www.xiyoumobile.com");
					break;
				case 2:// email
					sendEmail("420315258@qq.com");
					break;
				case 4:// csdn
					openBrowser("http://blog.csdn.net/tianzhaoai");
					break;
				}
			}

			@Override
			public void closeDialog() {
				aboutDialog.dismiss();
			}
		});
		aboutDialog.show();
	}

	/**
	 * �����ʼ�
	 */
	protected void sendEmail(String emailAdd) {
		Uri uri = Uri.parse("mailto:" + emailAdd);
		String[] email = { emailAdd };
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra(Intent.EXTRA_CC, email); // ������
		intent.putExtra(Intent.EXTRA_SUBJECT, "�����ʼ������ⲿ��"); // ����
		intent.putExtra(Intent.EXTRA_TEXT, "�����ʼ������Ĳ���"); // ����
		startActivity(Intent.createChooser(intent, "��ѡ���ʼ���Ӧ��"));
	}

	/**
	 * �������
	 */
	protected void openBrowser(String uri) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(uri);
		intent.setData(content_url);
		startActivity(intent);
	}

	/**
	 * ��ʾ���öԻ���
	 */
	protected void showSettingDialog() {
		final MyDialog dialog = new MyDialog(MainActivity.this, 2, isMusic,
				musicName);
		dialog.setSettingDialogListener(new SettingDialogListener() {

			@Override
			public void saveData() {
				List<Integer> levelData = mGameFilpBoardLayout.getSaveData();
				levelData.add(currLevel);
				DataUtils.saveLevelData(MainActivity.this, levelData);
				isSaveLevelData = true;
				CustomToast.showToast(MainActivity.this, "����ɹ�", 1500);
				LevelUtil.saveGameProgress(MainActivity.this, gameProgress);
				dialog.dismiss();
				chooseLevelOut();
			}

			@Override
			public void isMusic(boolean open) {
				DataUtils.saveIsMusic(MainActivity.this, open);
				if (open) {
					if (!isMusic) {
						isMusic = true;
						startMusic();
					}
				} else if (isMusic) {
					stopMusic();
					isMusic = false;
				}
			}

			@Override
			public void closeDialog() {
				dialog.dismiss();
			}

			@Override
			public void clearCache() {
				final MyDialog myDialog = new MyDialog(MainActivity.this,
						"FilpBoard", "ȷ�������Ϸ����?", "ȷ��", "ȡ��");
				myDialog.setMyDialogListener(new MyDialogListener() {

					@Override
					public void doConfirm() {
						if (DataUtils.clearCache(MainActivity.this)) {
							currLevel = 1;
							restart();
							gameProgress = LevelUtil
									.getGamePrggress(MainActivity.this);
							for (int i = 1; i < chooseLevelId.length; i++) {
								if (gameProgress.get(i) == 1) {
									awakeLevel(i);
								} else {
									bt_chooseLevel[i]
											.setBackgroundResource(R.drawable.star_false);
								}
							}
							chooseLevelOut();
							isSaveLevelData = false;
							CustomToast.showToast(MainActivity.this, "����ɹ�",
									2000);
						} else {
							CustomToast.showToast(MainActivity.this, "���ʧ��",
									2000);
						}
						dialog.dismiss();
						myDialog.dismiss();
					}

					@Override
					public void doCacel() {
						myDialog.dismiss();
					}
				});
				myDialog.show();
			}

			@Override
			public void setMusic(int flag) {
				if (flag == 1) {// ��������
					final MyDialog setMusicDialog = new MyDialog(
							MainActivity.this, 4, musicList);
					setMusicDialog
							.setSettingMusicLisener(new SettingMusicLisener() {

								@Override
								public void setMusic(Music music) {
									musicName = music.getName();
									dialog.setMusicName(musicName);
									DataUtils.saveMusicName(MainActivity.this,
											musicName);
									startMusic();
								}

								@Override
								public void closeDialog() {
									setMusicDialog.dismiss();
								}
							});
					setMusicDialog.show();
				} else {// �ָ�Ĭ��
					musicName = "��ɽ��ˮ";
					dialog.setMusicName(musicName);
					DataUtils.saveMusicName(MainActivity.this, musicName);
					startMusic();
				}
			}
		});
		dialog.show();
	}

	/**
	 * ���ǰ�ؿ������ü���
	 * 
	 * @param level
	 *            Ҫ����Ĺؿ�
	 */
	private boolean awakeLevel(int level) {
		if (level == bt_chooseLevel.length) {
			upDateing();
			return false;
		}
		bt_chooseLevel[level]
				.setBackgroundResource(R.drawable.bt_start_selector);
		bt_chooseLevel[level].setTag(level + "");
		bt_chooseLevel[level].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currLevel = Integer.parseInt(v.getTag().toString().trim());
				showTipDialog();
				restart();
				chooseLevelOut();
			}
		});
		return true;
	}

	/**
	 * ���¿�ʼ��ǰ��Ϸ
	 */
	private void restart() {
		steps = 0;
		tv_steps.setText(steps + "");
		tv_level.setText(currLevel + "");
		tv_steps.startAnimation(stepsAnimation);
		tv_level.startAnimation(stepsAnimation);
		initLevelData(currLevel);
		mGameFilpBoardLayout.nextLevel(levelData.get(levelData.size() - 2),
				levelData.get(levelData.size() - 1));
	}

	protected void addStep(int step) {
		tv_steps.setText(step + "");
		tv_steps.startAnimation(stepsAnimation);
	}

	/**
	 * ��ʾ�Ի���
	 */
	private void showTipDialog() {
		if (currLevel == 1) {
			final MyDialog dialog = new MyDialog(MainActivity.this, 1);
			dialog.setTipDialogListener(new TipDialogListener() {

				@Override
				public void closeDialog() {
					dialog.dismiss();
				}
			});
			dialog.setCancelable(false);
			dialog.show();
		}
	}

	private boolean isFirst = true;

	/**
	 * ��Ϸ�ɹ��Ի���
	 */
	protected void successDialog() {
		if (!awakeLevel(currLevel + 1)) {
			return;
		}
		gameProgress.put(currLevel + 1, 1);
		final MyDialog myDialog = new MyDialog(MainActivity.this, "FilpBoard",
				"��ϲ��,������!�Ƿ������һ��?", "��һ��", "���¿�ʼ");
		myDialog.setCancelable(false);
		;
		myDialog.setMyDialogListener(new MyDialogListener() {

			@Override
			public void doConfirm() {
				myDialog.dismiss();
				if (isFirst) {
					Animation animation = AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.text_explean_anim);
					tv_explan.startAnimation(animation);
					isFirst = false;
				}
				isSaveLevelData = false;
				tv_steps.setText(0 + "");
				currLevel++;
				tv_level.setText(currLevel + "");
				tv_level.startAnimation(stepsAnimation);
				initLevelData(currLevel);
				Animation level_anim = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.level_anim);
				tv_level_anim.setVisibility(View.VISIBLE);
				tv_level_anim.setText(currLevel + "");
				tv_level_anim.startAnimation(level_anim);
				level_anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						tv_level_anim.setVisibility(View.INVISIBLE);
					}
				});
				if (levelData != null) {
					mGameFilpBoardLayout.nextLevel(
							levelData.get(levelData.size() - 2),
							levelData.get(levelData.size() - 1));
				} else {
					upDateing();
				}
			}

			@Override
			public void doCacel() {
				myDialog.dismiss();
				restart();
			}
		});
		myDialog.show();
	}

	/**
	 * �Ѵﵽ���¹ؿ���ˢ�µ�ǰ�ؿ�
	 */
	protected void upDateing() {
		final MyDialog myDialog = new MyDialog(MainActivity.this, "FilpBoard",
				"��Ŷ - -,�������¹ؿ���!\n  �����ڴ�", "ȷ��", "�˳�");
		myDialog.setCancelable(false);
		;
		myDialog.setMyDialogListener(new MyDialogListener() {

			@Override
			public void doConfirm() {
				myDialog.dismiss();
				restart();
			}

			@Override
			public void doCacel() {
				myDialog.dismiss();
				finish();
			}
		});
		myDialog.show();
	}

	/*
	 * �������ؼ�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (chooseLevelIsLook) {
				chooseLevelOut();
				return false;
			}
			final MyDialog myDialog = new MyDialog(MainActivity.this,
					"FilpBoard", "�˳�", "ȡ��");
			myDialog.setMessage("ȷ���˳�?");
			myDialog.setMyDialogListener(new MyDialogListener() {

				@Override
				public void doConfirm() {
					myDialog.dismiss();
					if (!isSaveLevelData) {
						List<Integer> levelData = new ArrayList<Integer>();
						levelData.add(currLevel);
						DataUtils.saveLevelData(MainActivity.this, levelData);
					}
					LevelUtil.saveGameProgress(MainActivity.this, gameProgress);
					stopMusic();
					finish();
				}

				@Override
				public void doCacel() {
					myDialog.dismiss();
				}
			});
			myDialog.show();
		}
		return false;
	}

	private float firstX;
	private float secondX;

	private boolean chooseLevelIsLook = false;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (chooseLevelIsLook) {
			return false;
		}
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			firstX = e.getX();
		} else if (e.getAction() == MotionEvent.ACTION_UP) {
			secondX = e.getX();
			float result = firstX - secondX;
			firstX = 0;
			secondX = 0;
			if (result > 120) {
				chooseLevelIn();
			}
		}
		return super.onTouchEvent(e);
	}

	Animation[] animation2 = new Animation[chooseLevelId.length];
	@SuppressLint("HandlerLeak")
	Handler btChooseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			final int i = msg.what;
			animation2[i] = AnimationUtils.loadAnimation(MainActivity.this,
					R.anim.bt_choose_level_anim);
			bt_chooseLevel[i].setVisibility(View.VISIBLE);
			bt_chooseLevel[i].startAnimation(animation2[i]);
		};
	};

	/**
	 * ѡ��ؿ�����
	 */
	private void chooseLevelIn() {
		chooseLevelIsLook = true;
		rl_chooseLevel.setVisibility(View.VISIBLE);
		iv_chooseLevel_lift.setVisibility(View.VISIBLE);
		for (int i = 1; i < chooseLevelId.length; i++) {
			if (bt_chooseLevel[i].getVisibility() == 0)
				bt_chooseLevel[i].setVisibility(View.INVISIBLE);
		}
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(
				"translationX", mWidth, 0f);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(
				"translationY", 0f, 0f);
		ObjectAnimator translateAnimator = ObjectAnimator
				.ofPropertyValuesHolder(rl_chooseLevel, pvhX, pvhY);
		translateAnimator.setDuration(500);
		translateAnimator.start();
		new Thread() {
			public void run() {
				for (int i = 1; i < chooseLevelId.length; i++) {
					if (!chooseLevelIsLook)
						break;
					btChooseHandler.sendEmptyMessage(i);
					try {
						Thread.sleep(180);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}

	/**
	 * ѡ��ؿ���ʧ
	 */
	private void chooseLevelOut() {
		chooseLevelIsLook = false;
		iv_chooseLevel_lift.setVisibility(View.GONE);
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(
				"translationX", 0f, mWidth);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(
				"translationY", 0f, 0f);
		ObjectAnimator translateAnimator = ObjectAnimator
				.ofPropertyValuesHolder(rl_chooseLevel, pvhX, pvhY);
		translateAnimator.setDuration(500);
		translateAnimator.start();

	}

	@Override
	protected void onPause() {
		Intent intent = new Intent(MainActivity.this, MusicService.class);
		intent.putExtra("playing", isMusic);
		intent.putExtra("state", "pause");
		startService(intent);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		stopMusic();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if (isMusic) {
			Intent intent = new Intent(MainActivity.this, MusicService.class);
			intent.putExtra("playing", isMusic);
			intent.putExtra("state", "play");
			startService(intent);
		}
		super.onResume();
	}

	/**
	 * �����ֻ������б�
	 * 
	 * @return
	 */
	private List<Music> getMusicList() {
		List<Music> musicList = new ArrayList<Music>();
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ARTIST }, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();// ���α��ƶ�����ʼλ��
		for (int i = 0; i < cursor.getCount(); i++) {
			Music music = new Music();
			music.setName(cursor.getString(0));
			music.setArtist(cursor.getString(1));
			musicList.add(music);
			cursor.moveToNext();// ���α��Ƶ���һ��
		}
		return musicList;
	}

}
