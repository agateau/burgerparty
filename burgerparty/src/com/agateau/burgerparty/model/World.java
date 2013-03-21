package com.agateau.burgerparty.model;

import java.util.Random;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal0;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class World {
	public Signal0 stackFinished = new Signal0();
	public Signal0 levelFinished = new Signal0();
	public Signal0 levelFailed = new Signal0();
	private Level mLevel;
	private Inventory mInventory;
	private BurgerStack mBurgerStack;
	private BurgerStack mTargetBurgerStack;
	private int mCustomerCount;
	private int mScore;
	private Timer mTimer = new Timer();
	private int mRemainingSeconds;

	public World(Level level) {
		mLevel = level;
		mCustomerCount = mLevel.customerCount;
		mInventory = new Inventory(level.inventoryItems);
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
		BurgerStack.Status status = mBurgerStack.checkStatus(mTargetBurgerStack);
		if (status == BurgerStack.Status.DONE) {
			handleDoneStack();
		} else if (status == BurgerStack.Status.WRONG) {
			mBurgerStack.trash();
		}
	}

	public int getRemainingSeconds() {
		return mRemainingSeconds;
	}

	public int getScore() {
		return mScore;
	}

	public int getCustomerCount() {
		return mCustomerCount;
	}

	public void start() {
		mRemainingSeconds = mLevel.duration;
		Timer.Task task = new Timer.Task() {
			@Override
			public void run() {
				mRemainingSeconds--;
				if (mRemainingSeconds == 0) {
					cancel();
					levelFailed.emit();
				}
			}
		};
		mTimer.scheduleTask(task, 1, 1);
		generateTarget();
	}

	private void generateTarget() {
		Random random = new Random();
		Array<String> names = new Array<String>(mLevel.inventoryItems);
		int count = mLevel.minStackSize + random.nextInt(mLevel.maxStackSize - mLevel.minStackSize + 1);

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

	private void handleDoneStack() {
		mScore += 100 * mBurgerStack.getSize();
		mCustomerCount--;
		if (mCustomerCount > 0) {
			mBurgerStack = new BurgerStack();
			generateTarget();
			stackFinished.emit();
		} else {
			mTimer.stop();
			levelFinished.emit();
		}
	}
}
