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

	private static final int ADD_STEP = 0x111;// ��������
	private static final int GAME_SUCCESS = 0x112;// ��Ϸ�ɹ�

	// mColumn * mLine ������
	private int mColumn = 4; // ��
	private int mLine = 4; // ��
	// �������ڱ߾�
	private int mPadding;
	// ÿ��Сͼ֮��ľ���
	private int mMagin = 5;
	// �����Ŀ��
	private int mWidth;
	// ��Ϸ�ܲ���
	private int steps = 0;
	// ��Ϸ��ͼƬ
	private List<Bitmap> mItemBitmaps;

	public interface GameFilpBoardListener {
		// �����ı�
		void stepChange(int step);

		// �Ƿ�ɹ�
		void success();
	}

	private GameFilpBoardListener mFilpBoardListener;

	/**
	 * ���ûص��ӿ�
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
	 * ���캯��
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

	private boolean once;// �Ƿ��ǵ�һ�ν���
	private ImageView[] mGamePintuItems;// Item
	private int mItemWidth;// ÿ��Item�Ŀ�Ⱥ͸߶�

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ȡ��͸ߵ���Сֵ
		mWidth = getMeasuredWidth();
		if (!once) {
			// ��ʼ��ͼƬ
			initBitmap();
			// ��ʼ��ImageView��Item��
			initItem();
			once = true;
		}
		setMeasuredDimension(mWidth, getMeasuredHeight());
	}

	/**
	 * ���Ӳ���
	 */
	private void addStep() {
		steps++;
		handler.sendEmptyMessage(ADD_STEP);
	}

	/**
	 * ���ò���
	 * 
	 * @param steps
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}

	/**
	 * ����һ��
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
	 * ���¿�ʼ
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
	 * ��һ��
	 */
	public void nextLevel(int mLine, int mColumn) {
		this.mColumn = mColumn;
		this.mLine = mLine;
		restart();
	}

	/**
	 * ��ȡ���������
	 * 
	 * @return ���������
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
	 * ��ʼ��ÿһ��Item
	 */
	private void initItem() {
		mItemWidth = (mWidth - mPadding * 2 - mMagin * (mColumn - 1)) / mColumn;
		mGamePintuItems = new ImageView[mColumn * mLine];
		// ����Item������Rule��ÿ��Item֮��Ĺ�ϵ��
		for (int i = 0; i < mGamePintuItems.length; i++) {

			int tag = MainActivity.levelData.get(i);

			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(tag));
			mGamePintuItems[i] = item;
			item.setId(i + 1);
			// ��Item��tag�д洢��Index
			item.setTag(tag + "");
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mItemWidth, mItemWidth);
			// �������һ�У������ұ߾࣬ͨ��rightMargin
			if ((i + 1) % mColumn != 0) {
				lp.rightMargin = mMagin;
			}
			// ���ǵ�һ��
			if (i % mColumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}
			// ���ǵ�һ��,����topMargin����Rule
			if ((i + 1) > mColumn) {
				lp.topMargin = mMagin;
				lp.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mColumn].getId());
			}
			addView(item, lp);
		}
	}

	/**
	 * ��ʼ������ 0 - �� 1 - ��
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
	 * ��ȡ�����������Сֵ
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

	// ����
	private AlphaAnim topRotate3d;
	private AlphaAnim bottomRotate3d;
	private AlphaAnim liftRotate3d;
	private AlphaAnim rigthRotate3d;
	private AlphaAnim centerRotate3d;

	private ImageView topImageView;// ��
	private ImageView bottomImageView;// ��
	private ImageView liftImageView;// ��
	private ImageView rightImageView;// ��
	private ImageView centerImageView;// ��

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
	 * ��ת�����view
	 * 
	 * @param v
	 */
	private void turnView(View v) {
		centerId = v.getId();
		centerImageView = mGamePintuItems[centerId - 1];
		centerRotate3d = getAnim(centerImageView, 0);
		changeColor(centerImageView);

		// ��
		if ((centerId - mColumn) > 0) {
			topImageView = mGamePintuItems[centerId - mColumn - 1];
			topRotate3d = getAnim(topImageView, 0);
			changeColor(topImageView);
		}
		// ��
		if ((v.getId() + mColumn) <= (mColumn * mLine)) {
			bottomImageView = mGamePintuItems[centerId + mColumn - 1];
			bottomRotate3d = getAnim(bottomImageView, 0);
			changeColor(bottomImageView);
		}
		// ��
		if ((centerId - 1) % mColumn != 0) {
			liftImageView = mGamePintuItems[centerId - 2];
			liftRotate3d = getAnim(liftImageView, 0);
			changeColor(liftImageView);
		}
		// ��
		if (centerId % mColumn != 0) {
			rightImageView = mGamePintuItems[centerId];
			rigthRotate3d = getAnim(rightImageView, 0);
			changeColor(rightImageView);
		}

		// ���Ӳ���
		addStep();
		// ��ʼ����
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

				// �ж���Ϸ�Ƿ�ɹ�

				isSuccess();
			}

		});

	}

	private boolean GAME_IS_SUCCESS = false;

	/**
	 * �ж���Ϸ�Ƿ�ɹ�
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
