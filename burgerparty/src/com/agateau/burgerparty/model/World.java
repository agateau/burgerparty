package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class World {
	public Signal0 burgerFinished = new Signal0();
	public Signal0 mealFinished = new Signal0();
	public Signal1<LevelResult> levelFinished = new Signal1<LevelResult>();
	public Signal0 levelFailed = new Signal0();

	private Timer mTimer = new Timer();

	private Level mLevel;

	private Inventory mBurgerInventory;
	private Inventory mMealExtraInventory;

	private Burger mBurger = new Burger();
	private MealExtra mMealExtra = new MealExtra();

	private Burger mTargetBurger = new Burger();
	private MealExtra mTargetMealExtra = new MealExtra();

	private int mCustomerCount;
	private int mRemainingSeconds;
	private int mTrashedCount = 0;

	public World(Level level) {
		mLevel = level;
		mCustomerCount = mLevel.definition.customerCount;
		mBurgerInventory = new Inventory(level.definition.burgerItems);
		mMealExtraInventory = new Inventory(level.definition.extraItems);
	}
	
	public Inventory getBurgerInventory() {
		return mBurgerInventory;
	}

	public Inventory getMealExtraInventory() {
		return mMealExtraInventory;
	}

	public Burger getBurger() {
		return mBurger;
	}
	
	public Burger getTargetBurger() {
		return mTargetBurger;
	}

	public MealExtra getMealExtra() {
		return mMealExtra;
	}

	public MealExtra getTargetMealExtra() {
		return mTargetMealExtra;
	}

	public void addItem(MealItem item) {
		if (item.getType() == MealItem.Type.BURGER) {
			addBurgerItem((BurgerItem)item);
		} else {
			addExtraItem(item);
		}
	}

	private void addBurgerItem(BurgerItem item) {
		mBurger.addItem(item);
		Burger.Status status = mBurger.checkStatus(mTargetBurger);
		if (status == Burger.Status.DONE) {
			if (mTargetMealExtra.isEmpty()) {
				onMealFinished();
			} else {
				onBurgerFinished();
			}
		} else if (status == Burger.Status.WRONG) {
			mTrashedCount++;
			mBurger.trash();
		}
	}

	private void addExtraItem(MealItem item) {
		if (!mMealExtra.isMissing(mTargetMealExtra, item)) {
			Gdx.app.log("World.addExtraItem", "Wrong extra item " + item.getName());
			return;
		}
		mMealExtra.addItem(item);
		if (mMealExtra.equals(mTargetMealExtra)) {
			onMealFinished();
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
		generateTargetBurger();
		generateTargetMealExtra();
	}

	private void generateTargetBurger() {
		Array<String> names = new Array<String>(mLevel.definition.burgerItems);
		names.removeValue("top", false);
		names.removeValue("bottom", false);
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

	private void generateTargetMealExtra() {
		Array<String> names = new Array<String>(mLevel.definition.extraItems);
		mTargetMealExtra.clear();
		if (names.size == 0) {
			return;
		}
		int count = MathUtils.random(1, mLevel.definition.extraItems.size - 1);

		for (; count > 0; count--) {
			int index = MathUtils.random(names.size - 1);
			String name = names.removeIndex(index);
			mTargetMealExtra.addItem(MealItem.get(name));
		}
	}

	private void onBurgerFinished() {
		burgerFinished.emit();
	}

	private void onMealFinished() {
		mCustomerCount--;
		if (mCustomerCount > 0) {
			mBurger = new Burger();
			mMealExtra = new MealExtra();
			generateTarget();
			mealFinished.emit();
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
