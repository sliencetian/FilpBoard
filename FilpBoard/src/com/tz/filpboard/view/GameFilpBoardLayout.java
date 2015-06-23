package com.tz.filpboard.view;

import java.util.ArrayList;
import java.util.List;

import com.tz.filpboard.MainActivity;
import com.tz.filpboard.R;
import com.tz.filpboard.anim.AlphaAnim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GameFilpBoardLayout extends RelativeLayout implements
		OnClickListener {

	private static final int ADD_STEP = 0x111;// 步数增加
	private static final int GAME_SUCCESS = 0x112;// 游戏成功

	// mColumn * mLine 的棋盘
	private int mColumn = 4; // 列
	private int mLine = 4; // 行
	// 容器的内边距
	private int mPadding;
	// 每张小图之间的距离
	private int mMagin = 5;
	// 容器的宽度
	private int mWidth;
	// 游戏总步数
	private int steps = 0;
	// 游戏的图片
	private List<Bitmap> mItemBitmaps;

	public interface GameFilpBoardListener {
		// 步数改变
		void stepChange(int step);

		// 是否成功
		void success();
	}

	private GameFilpBoardListener mFilpBoardListener;

	/**
	 * 设置回调接口
	 * 
	 * @param mFilpBoardListener
	 */
	public void setmFilpBoardListener(GameFilpBoardListener mFilpBoardListener) {
		this.mFilpBoardListener = mFilpBoardListener;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ADD_STEP:
				mFilpBoardListener.stepChange(steps);
				break;
			case GAME_SUCCESS:
				mFilpBoardListener.success();
				break;
			}
		};
	};

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public GameFilpBoardLayout(Context context) {
		super(context, null);
	}

	public GameFilpBoardLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public GameFilpBoardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mMagin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());
	}

	private boolean once;// 是否是第一次进来
	private ImageView[] mGamePintuItems;// Item
	private int mItemWidth;// 每个Item的宽度和高度

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 取宽和高的最小值
		mWidth = getMeasuredWidth();
		if (!once) {
			// 初始化图片
			initBitmap();
			// 初始化ImageView（Item）
			initItem();
			once = true;
		}
		setMeasuredDimension(mWidth, getMeasuredHeight());
	}

	/**
	 * 增加步数
	 */
	private void addStep() {
		steps++;
		handler.sendEmptyMessage(ADD_STEP);
	}

	/**
	 * 设置步数
	 * 
	 * @param steps
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}

	/**
	 * 回退一步
	 */
	public boolean fallBack() {
		if (view != null) {
			turnView(view);
			steps -= 2;
			handler.sendEmptyMessage(ADD_STEP);
			view = null;
			return true;
		}
		return false;
	}

	/**
	 * 重新开始
	 */
	public void restart() {
		this.removeAllViews();
		if (mGamePintuItems != null)
			mGamePintuItems = null;
		initItem();
		GAME_IS_SUCCESS = false;
		steps = 0;
	}

	/**
	 * 下一关
	 */
	public void nextLevel(int mLine, int mColumn) {
		this.mColumn = mColumn;
		this.mLine = mLine;
		restart();
	}

	/**
	 * 获取保存的数据
	 * 
	 * @return 保存的数据
	 */
	public List<Integer> getSaveData() {
		List<Integer> saveData = new ArrayList<Integer>();
		saveData.add(mLine);
		saveData.add(mColumn);
		for (int i = 0; i < mGamePintuItems.length; i++) {
			String tag = mGamePintuItems[i].getTag().toString().trim();
			saveData.add(Integer.parseInt(tag));
		}
		saveData.add(steps);
		return saveData;
	}

	/**
	 * 初始化每一个Item
	 */
	private void initItem() {
		mItemWidth = (mWidth - mPadding * 2 - mMagin * (mColumn - 1)) / mColumn;
		mGamePintuItems = new ImageView[mColumn * mLine];
		// 生成Item，设置Rule（每个Item之间的关系）
		for (int i = 0; i < mGamePintuItems.length; i++) {

			int tag = MainActivity.levelData.get(i);

			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(tag));
			mGamePintuItems[i] = item;
			item.setId(i + 1);
			// 在Item的tag中存储了Index
			item.setTag(tag + "");
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mItemWidth, mItemWidth);
			// 不是最后一列，设置右边距，通过rightMargin
			if ((i + 1) % mColumn != 0) {
				lp.rightMargin = mMagin;
			}
			// 不是第一列
			if (i % mColumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}
			// 不是第一行,设置topMargin，和Rule
			if ((i + 1) > mColumn) {
				lp.topMargin = mMagin;
				lp.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mColumn].getId());
			}
			addView(item, lp);
		}
	}

	/**
	 * 初始化布局 0 - 黑 1 - 白
	 */
	private void initBitmap() {
		mItemBitmaps = new ArrayList<Bitmap>();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.black);
		mItemBitmaps.add(bitmap);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white);
		mItemBitmaps.add(bitmap);
	}

	/**
	 * 获取多个参数的最小值
	 * 
	 * @param params
	 * @return
	 */
	private int min(int... params) {
		int min = params[0];
		for (int param : params) {
			if (param < min)
				min = param;
		}
		return min;
	}

	// 动画
	private AlphaAnim topRotate3d;
	private AlphaAnim bottomRotate3d;
	private AlphaAnim liftRotate3d;
	private AlphaAnim rigthRotate3d;
	private AlphaAnim centerRotate3d;

	private ImageView topImageView;// 上
	private ImageView bottomImageView;// 下
	private ImageView liftImageView;// 左
	private ImageView rightImageView;// 右
	private ImageView centerImageView;// 中

	private int centerId;

	private View view;

	@Override
	public void onClick(View v) {
		view = v;
		turnView(v);
	}

	public void setLineAndColmun(int line, int colmun) {
		this.mLine = line;
		this.mColumn = colmun;
	}

	/**
	 * 旋转点击的view
	 * 
	 * @param v
	 */
	private void turnView(View v) {
		centerId = v.getId();
		centerImageView = mGamePintuItems[centerId - 1];
		centerRotate3d = getAnim(centerImageView, 0);
		changeColor(centerImageView);

		// 上
		if ((centerId - mColumn) > 0) {
			topImageView = mGamePintuItems[centerId - mColumn - 1];
			topRotate3d = getAnim(topImageView, 0);
			changeColor(topImageView);
		}
		// 下
		if ((v.getId() + mColumn) <= (mColumn * mLine)) {
			bottomImageView = mGamePintuItems[centerId + mColumn - 1];
			bottomRotate3d = getAnim(bottomImageView, 0);
			changeColor(bottomImageView);
		}
		// 左
		if ((centerId - 1) % mColumn != 0) {
			liftImageView = mGamePintuItems[centerId - 2];
			liftRotate3d = getAnim(liftImageView, 0);
			changeColor(liftImageView);
		}
		// 右
		if (centerId % mColumn != 0) {
			rightImageView = mGamePintuItems[centerId];
			rigthRotate3d = getAnim(rightImageView, 0);
			changeColor(rightImageView);
		}

		// 增加步数
		addStep();
		// 开始动画
		startAnims();
	}

	private void startAnims() {

		if (topImageView != null)
			topImageView.startAnimation(topRotate3d);
		if (bottomImageView != null)
			bottomImageView.startAnimation(bottomRotate3d);
		if (liftImageView != null)
			liftImageView.startAnimation(liftRotate3d);
		if (rightImageView != null)
			rightImageView.startAnimation(rigthRotate3d);

		centerImageView.startAnimation(centerRotate3d);
		centerRotate3d.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {

				centerImageView = null;

				topImageView = null;
				bottomImageView = null;
				liftImageView = null;
				rightImageView = null;

				// 判断游戏是否成功

				isSuccess();
			}

		});

	}

	private boolean GAME_IS_SUCCESS = false;

	/**
	 * 判断游戏是否成功
	 */
	protected void isSuccess() {
		String tag = mGamePintuItems[0].getTag().toString();
		for (int i = 0; i < mGamePintuItems.length; i++) {
			String temp = mGamePintuItems[i].getTag().toString();
			if (!tag.equals(temp) || GAME_IS_SUCCESS) {
				return;
			}
		}
		GAME_IS_SUCCESS = true;
		mFilpBoardListener.success();
	}

	private void changeColor(ImageView imageView) {
		if (imageView.getTag().toString().equals("0")) {
			imageView.setImageBitmap(mItemBitmaps.get(1));

			imageView.setTag("1");
		} else {
			imageView.setImageBitmap(mItemBitmaps.get(0));
			imageView.setTag("0");
		}
	}

	public AlphaAnim getAnim(ImageView imageView, int state) {
		AlphaAnim rotate3d = new AlphaAnim(state);
		imageView.measure(0, 0);
		rotate3d.setDuration(900);
		return rotate3d;
	}

}
