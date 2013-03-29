package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class World {
	public Signal0 stackFinished = new Signal0();
	public Signal1<LevelResult> levelFinished = new Signal1<LevelResult>();
	public Signal0 levelFailed = new Signal0();

	private Timer mTimer = new Timer();

	private Level mLevel;
	private Inventory mInventory;
	private BurgerStack mBurgerStack;
	private BurgerStack mTargetBurgerStack;

	private int mCustomerCount;
	private int mRemainingSeconds;
	private int mTrashedCount = 0;

	public World(Level level) {
		mLevel = level;
		mCustomerCount = mLevel.definition.customerCount;
		mInventory = new Inventory(level.definition.inventoryItems);
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
			mTrashedCount++;
			mBurgerStack.trash();
		}
	}

	public int getRemainingSeconds() {
		return mRemainingSeconds;
	}

	public int getCustomerCount() {
		return mCustomerCount;
	}

	public int getTrashedCount() {
		return mTrashedCount;
	}

	public int getDuration() {
		return mLevel.definition.duration - mRemainingSeconds;
	}

	public void start() {
		mRemainingSeconds = mLevel.definition.duration;
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

	public void pause() {
		mTimer.stop();
	}

	public void resume() {
		mTimer.start();
	}

	private void generateTarget() {
		Array<String> names = new Array<String>(mLevel.definition.inventoryItems);
		int count = MathUtils.random(mLevel.definition.minStackSize, mLevel.definition.maxStackSize);

		mTargetBurgerStack.clear();

		mTargetBurgerStack.addItem(BurgerItem.get("bottom"));

		// Generate content, make sure items cannot appear two times consecutively
		String lastName = new String();
		for (; count > 0; count--) {
			int index = MathUtils.random(names.size - 1);
			String name = names.removeIndex(index);
			if (!lastName.isEmpty()) {
				names.add(lastName);
			}
			lastName = name;
			mTargetBurgerStack.addItem(BurgerItem.get(name));
		}

		mTargetBurgerStack.addItem(BurgerItem.get("top"));
	}

	private void handleDoneStack() {
		mCustomerCount--;
		if (mCustomerCount > 0) {
			mBurgerStack = new BurgerStack();
			generateTarget();
			stackFinished.emit();
		} else {
			mTimer.stop();
			LevelResult result = createLevelResult();
			levelFinished.emit(result);
		}
	}

	private LevelResult createLevelResult() {
		LevelResult result = new LevelResult();
		for (Objective obj: mLevel.definition.objectives) {
			result.addObjectiveResult(obj.computeResult(this));
		}
		return result;
	}
}
