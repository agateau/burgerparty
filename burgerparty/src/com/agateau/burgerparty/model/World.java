package com.agateau.burgerparty.model;

import java.util.Random;

import com.agateau.burgerparty.model.Inventory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class World {
	private Inventory mInventory;
	private BurgerStack mBurgerStack;
	private BurgerStack mTargetBurgerStack;
	private long mStartTime;
	private int mScore;

	static final int MAX_DURATION_SECS = 15;
	static final int MIN_ITEMS = 2;
	static final int MAX_ITEMS = 6;

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
		Random random = new Random();
		Array<String> names = new Array<String>();
		names.add("steak");
		names.add("salad");
		names.add("cheese");
		names.add("tomato");
		int count = MIN_ITEMS + random.nextInt(MAX_ITEMS - MIN_ITEMS + 1);

		mTargetBurgerStack.clear();

		mTargetBurgerStack.addItem(new BurgerItem("bottom"));

		// Generate content, make sure items cannot appear two times consecutively
		String lastName = new String();
		for (; count > 0; count--) {
			int index = random.nextInt(names.size);
			String name = names.removeIndex(index);
			if (!lastName.isEmpty()) {
				names.add(lastName);
			}
			lastName = name;
			mTargetBurgerStack.addItem(new BurgerItem(name));
		}

		mTargetBurgerStack.addItem(new BurgerItem("top"));
	}

	private void increaseScore() {
		mScore += 100 * getRemainingSeconds();
	}
}
