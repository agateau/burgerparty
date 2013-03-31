package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class World {
	public Signal0 burgerFinished = new Signal0();
	public Signal1<LevelResult> levelFinished = new Signal1<LevelResult>();
	public Signal0 levelFailed = new Signal0();

	private Timer mTimer = new Timer();

	private Level mLevel;
	private Inventory mInventory;
	private Burger mBurger;
	private Burger mTargetBurger;

	private int mCustomerCount;
	private int mRemainingSeconds;
	private int mTrashedCount = 0;

	public World(Level level) {
		mLevel = level;
		mCustomerCount = mLevel.definition.customerCount;
		mInventory = new Inventory(level.definition.burgerItems);
		mBurger = new Burger();
		mTargetBurger = new Burger();
	}
	
	public Inventory getInventory() {
		return mInventory;
	}

	public Burger getBurger() {
		return mBurger;
	}
	
	public Burger getTargetBurger() {
		return mTargetBurger;
	}

	public void addItem(MealItem item) {
		assert(item.getType() == MealItem.Type.BURGER);
		mBurger.addItem((BurgerItem)item);
		Burger.Status status = mBurger.checkStatus(mTargetBurger);
		if (status == Burger.Status.DONE) {
			onBurgerFinished();
		} else if (status == Burger.Status.WRONG) {
			mTrashedCount++;
			mBurger.trash();
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
		Array<String> names = new Array<String>(mLevel.definition.burgerItems);
		int count = MathUtils.random(mLevel.definition.minBurgerSize, mLevel.definition.maxBurgerSize);

		mTargetBurger.clear();

		mTargetBurger.addItem(BurgerItem.get("bottom"));

		// Generate content, make sure items cannot appear two times consecutively
		String lastName = new String();
		for (; count > 0; count--) {
			int index = MathUtils.random(names.size - 1);
			String name = names.removeIndex(index);
			if (!lastName.isEmpty()) {
				names.add(lastName);
			}
			lastName = name;
			mTargetBurger.addItem(BurgerItem.get(name));
		}

		mTargetBurger.addItem(BurgerItem.get("top"));
	}

	private void onBurgerFinished() {
		mCustomerCount--;
		if (mCustomerCount > 0) {
			mBurger = new Burger();
			generateTarget();
			burgerFinished.emit();
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
