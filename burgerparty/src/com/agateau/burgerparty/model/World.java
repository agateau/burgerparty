package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.Signal3;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;

public class World {
	public enum ScoreType {
		COMPLETED_HAPPY,
		COMPLETED_NEUTRAL,
		COMPLETED_ANGRY,
		COMBO
	};
	public Signal0 burgerFinished = new Signal0();
	public Signal0 mealFinished = new Signal0();
	public Signal1<LevelResult> levelFinished = new Signal1<LevelResult>();
	public Signal0 levelFailed = new Signal0();
	public Signal0 trashing = new Signal0();
	public Signal3<ScoreType, Integer, Integer> scored = new Signal3<ScoreType, Integer, Integer>();

	private static final int COMPLETED_HAPPY_SCORE = 4000;
	private static final int COMPLETED_NEUTRAL_SCORE = 2000;
	private static final int COMPLETED_ANGRY_SCORE = 1000;

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private Timer mTimer = new Timer();

	private Level mLevel;

	private Inventory mBurgerInventory;
	private Inventory mMealExtraInventory;

	private Burger mBurger;
	private MealExtra mMealExtra;

	private Burger mTargetBurger = new Burger();
	private MealExtra mTargetMealExtra = new MealExtra();

	private Array<Customer> mCustomers = new Array<Customer>();
	private int mActiveCustomerIndex = 0;
	private int mRemainingSeconds;
	private int mTrashedCount = 0;
	private int mScore = 0;

	private boolean mIsTrashing = false; // Set to true when we are in the middle of a trash animation

	public World(Level level) {
		mLevel = level;
		for (String name: mLevel.definition.customers) {
			mCustomers.add(new Customer(name));
		}
		Array<String> allBurgerItems = new Array<String>(level.definition.burgerItems);
		allBurgerItems.add(level.definition.topBurgerItem);
		if (level.definition.topBurgerItem != level.definition.bottomBurgerItem) {
			allBurgerItems.add(level.definition.bottomBurgerItem);
		}
		mBurgerInventory = new Inventory(allBurgerItems);
		mMealExtraInventory = new Inventory(level.definition.extraItems);
		setupMeal();
	}

	private void setupMeal() {
		mBurger = new Burger();
		mBurger.burgerItemAdded.connect(mHandlers, new Signal1.Handler<BurgerItem>() {
			@Override
			public void handle(BurgerItem item) {
				onBurgerItemAdded();
			}
		});

		mMealExtra = new MealExtra();
		mMealExtra.itemAdded.connect(mHandlers, new Signal1.Handler<MealItem>() {
			@Override
			public void handle(MealItem item) {
				onMealItemAdded(item);
			}
		});
	}

	public String getLevelWorldDirName() {
		return mLevel.getLevelWorld().getDirName();
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

	public int getRemainingSeconds() {
		return mRemainingSeconds;
	}

	public Array<Customer> getCustomers() {
		return mCustomers;
	}

	public int getTrashedCount() {
		return mTrashedCount;
	}

	public int getDuration() {
		return mLevel.definition.duration - mRemainingSeconds;
	}

	public boolean isTrashing() {
		return mIsTrashing;
	}

	public void markTrashingDone() {
		assert mIsTrashing;
		mIsTrashing = false;
	}

	public int getScore() {
		return mScore;
	}

	private void increaseScore(ScoreType scoreType, int value) {
		int old = mScore;
		mScore += value;
		scored.emit(scoreType, old, mScore);
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
		int count = MathUtils.random(mLevel.definition.minBurgerSize, mLevel.definition.maxBurgerSize);

		Array<BurgerItem> items = new Array<BurgerItem>();
		items.add(BurgerItem.get(mLevel.definition.bottomBurgerItem));

		// Generate content, make sure items cannot appear two times consecutively
		String lastName = new String();
		for (; count > 0; count--) {
			int index = MathUtils.random(names.size - 1);
			String name = names.removeIndex(index);
			if (!lastName.isEmpty()) {
				names.add(lastName);
			}
			lastName = name;
			items.add(BurgerItem.get(name));
		}
		items.add(BurgerItem.get(mLevel.definition.topBurgerItem));
		mTargetBurger.setItems(items);
	}

	private void generateTargetMealExtra() {
		Array<String> names = new Array<String>(mLevel.definition.extraItems);
		mTargetMealExtra.clear();
		if (names.size == 0) {
			return;
		}
		ObjectMap<MealItem.Type, Array<MealItem>> itemsForType = new ObjectMap<MealItem.Type, Array<MealItem>>();
		for(String name: names) {
			MealItem item = MealItem.get(name);
			Array<MealItem> lst = itemsForType.get(item.getType(), null);
			if (lst == null) {
				lst = new Array<MealItem>();
				itemsForType.put(item.getType(), lst);
			}
			lst.add(item);
		}
		// Pick one item per type
		Set<MealItem> items = new HashSet<MealItem>();
		for(Iterator<Array<MealItem>> it = itemsForType.values(); it.hasNext(); ) {
			Array<MealItem> lst = it.next();
			int index = MathUtils.random(lst.size - 1);
			items.add(lst.get(index));
		}
		mTargetMealExtra.setItems(items);
	}

	private void onBurgerItemAdded() {
		if (mIsTrashing) {
			// We reach this point when an item i2 was added right after a bad item i1:
			// i1 was spotted and trashing was started but i2 was already being added and
			// has just been added to mBurger. We must therefore clear mBurger to remove i2.
			mBurger.clear();
			return;
		}
		Burger.CompareResult compareResult = mBurger.compareTo(mTargetBurger);
		if (compareResult == Burger.CompareResult.SAME) {
			if (mTargetMealExtra.isEmpty()) {
				onMealFinished();
			} else {
				onBurgerFinished();
			}
		} else if (compareResult == Burger.CompareResult.DIFFERENT) {
			mTrashedCount++;
			mBurger.trash();
			mIsTrashing = true;
			trashing.emit();
		}
	}

	private void onMealItemAdded(MealItem item) {
		if (mIsTrashing) {
			mMealExtra.clear();
			return;
		}
		MealExtra.CompareResult result = mMealExtra.compareTo(mTargetMealExtra);
		if (result == MealExtra.CompareResult.SAME) {
			onMealFinished();
		} else if (result == MealExtra.CompareResult.DIFFERENT) {
			mTrashedCount++;
			mBurger.trash();
			mMealExtra.trash();
			mIsTrashing = true;
			trashing.emit();
		}
	}

	private void onBurgerFinished() {
		burgerFinished.emit();
	}

	private void onMealFinished() {
		adjustScore();
		mActiveCustomerIndex++;
		if (mActiveCustomerIndex < mCustomers.size) {
			setupMeal();
			generateTarget();
			mealFinished.emit();
		} else {
			mTimer.stop();
			LevelResult result = createLevelResult();
			levelFinished.emit(result);
		}
	}

	private void adjustScore() {
		Customer.Mood mood = mCustomers.get(mActiveCustomerIndex).getMood();
		ScoreType scoreType;
		int value;
		if (mood == Customer.Mood.HAPPY) {
			scoreType = ScoreType.COMPLETED_HAPPY;
			value = COMPLETED_HAPPY_SCORE;
		} else if (mood == Customer.Mood.NEUTRAL) {
			scoreType = ScoreType.COMPLETED_NEUTRAL;
			value = COMPLETED_NEUTRAL_SCORE;
		} else {
			scoreType = ScoreType.COMPLETED_ANGRY;
			value = COMPLETED_ANGRY_SCORE;
		}
		increaseScore(scoreType, value);
	}

	private LevelResult createLevelResult() {
		LevelResult result = new LevelResult();
		for (Objective obj: mLevel.definition.objectives) {
			result.addObjectiveResult(obj.computeResult(this));
		}
		return result;
	}
}
