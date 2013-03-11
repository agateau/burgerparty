package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;
import com.badlogic.gdx.utils.TimeUtils;

public class World {
	private Inventory mInventory;
	private BurgerStack mBurgerStack;
	private BurgerStack mTargetBurgerStack;
	private long mStartTime;
	private int mScore;

	static final int MAX_DURATION_SECS = 15;

	public World() {
		mInventory = new Inventory();
		mBurgerStack = new BurgerStack();
		mTargetBurgerStack = new BurgerStack();
	}
	
	public Inventory getInventory() {
		return mInventory;
	}

	public BurgerStack getBurgerStack() {
		return mBurgerStack;
	}
	
	public BurgerStack getTargetBurgerStack() {
		return mTargetBurgerStack;
	}

	public void checkStackStatus() {
		if (mBurgerStack.sameAs(mTargetBurgerStack)) {
			increaseScore();
			restart();
		}
	}

	public void restart() {
		mStartTime = TimeUtils.nanoTime();
		mBurgerStack.clear();
		generateTarget();
	}

	public int getRemainingSeconds() {
		int deltaSecs = (int)((TimeUtils.nanoTime() - mStartTime) / (1000 * 1000 * 1000));
		return Math.max(0, MAX_DURATION_SECS - deltaSecs);
	}

	public int getScore() {
		return mScore;
	}

	private void generateTarget() {
		final String[] names = {"steak", "salad", "cheese", "tomato"};
		int count = 2 + (int)(4 * Math.random());

		mTargetBurgerStack.clear();

		mTargetBurgerStack.addItem(new BurgerItem("bottom"));

		for (; count >= 0; count--) {
			String name = names[(int)(Math.random() * names.length)];
			mTargetBurgerStack.addItem(new BurgerItem(name));
		}

		mTargetBurgerStack.addItem(new BurgerItem("top"));
	}

	private void increaseScore() {
		mScore += 100 * getRemainingSeconds();
	}
}
