package com.tz.filpboard.anim;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AlphaAnim extends Animation {

	private int state = 0;

	public AlphaAnim(int state) {
		this.state = state;
	}

	/*
	 * interpolatedTime 动画从0开始到1结束 t 动画的属性
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		Matrix m = t.getMatrix();
		m.setTranslate((float) (Math.sin(interpolatedTime * 10) * 15), 0);
		if (state == 0) {
			t.setAlpha(interpolatedTime);
		} else {
			t.setAlpha(1 - interpolatedTime);
		}

		super.applyTransformation(interpolatedTime, t);
	}
}
