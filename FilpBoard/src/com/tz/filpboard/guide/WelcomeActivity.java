package com.tz.filpboard.guide;

import com.tz.filpboard.MainActivity;
import com.tz.filpboard.R;
import com.tz.filpboard.datautils.DataUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

	
	// ¹Ø¿¨Êý¾Ý
	private String levelDataStr;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
			intent.putExtra("levelDataStr", levelDataStr);
			startActivity(intent);
			finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_activity);
		ImageView iv = (ImageView) findViewById(R.id.welcome_img);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
		alphaAnimation.setDuration(2800);
		iv.startAnimation(alphaAnimation);
		levelDataStr = DataUtils.getLevelData(WelcomeActivity.this);
		
		handler.sendEmptyMessageDelayed(0, 2800);
	}

}
