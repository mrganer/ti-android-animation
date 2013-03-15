package com.billdawson.timodules.animation;

/*
 * Copyright (C) 2013 William Dawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.appcelerator.kroll.annotations.Kroll;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;

@Kroll.proxy(creatableInModule = AndroidanimationModule.class)
public class AnimatorSetProxy extends AnimatorProxy {

	enum PlayOrder {
		SEQUENTIALLY, TOGETHER, UNKNOWN
	};

	@SuppressWarnings("unused")
	private static final String TAG = "AnimatorSetProxy";

	private AnimatorProxy[] mChildAnimations;
	private PlayOrder mPlayOrder = PlayOrder.UNKNOWN;

	@Override
	protected void buildAnimator() {

		if (mPlayOrder == PlayOrder.UNKNOWN) {
			throw new IllegalStateException(
					"playTogether or playSequentially must be called before using the AnimatorSet.");
		}

		AnimatorSet animator = (AnimatorSet) getAnimator();

		int childCount = mChildAnimations == null ? 0 : mChildAnimations.length;
		Animator[] children = new Animator[childCount];

		for (int i = 0; i < childCount; i++) {
			AnimatorProxy oneProxy = mChildAnimations[i];
			oneProxy.buildAnimator();
			children[i] = oneProxy.getAnimator();

		}

		if (animator == null) {
			animator = new AnimatorSet();
		}

		if (mPlayOrder == PlayOrder.SEQUENTIALLY) {
			animator.playSequentially(children);
		} else {
			animator.playTogether(children);
		}

		if (getTarget() != null) {
			animator.setTarget(getTarget());
		}

		setAnimator(animator);

		super.setCommonAnimatorProperties();

	}

	@Kroll.method
	public AnimatorProxy[] getChildAnimations() {
		return mChildAnimations;
	}

	@Kroll.method
	public void playSequentially(AnimatorProxy[] animations) {
		mChildAnimations = animations;
		mPlayOrder = PlayOrder.SEQUENTIALLY;
	}

	@Kroll.method
	public void playTogether(AnimatorProxy[] animations) {
		mChildAnimations = animations;
		mPlayOrder = PlayOrder.TOGETHER;
	}

}