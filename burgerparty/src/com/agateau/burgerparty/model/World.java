package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.ConnectionManager;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;

public class World {
	public static class Score {
		public enum Type {
			ANGRY,
			NEUTRAL,
			HAPPY,
			COMBO
		};
		public Type type;
		public String message = new String();
		public int delta;
	}
	public Signal0 burgerFinished = new Signal0();
	public Signal1<Score> mealFinished = new Signal1<Score>();
	public Signal0 levelFailed = new Signal0();
	public Signal0 trashing = new Signal0();

	private static final int COMBO_SCORE = 1000;
	private static final int HAPPY_SCORE = 4000;
	private static final int NEUTRAL_SCORE = 2000;
	private static final int ANGRY_SCORE = 1000;

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
		mMealConnections.disconnectAll();
		mBurger = new Burger();
		mBurger.itemAdded.connect(mMealConnections, new Signal1.Handler<MealItem>() {
			@Override
			public void handle(MealItem item) {
				onBurgerItemAdded();
			}
		});

		mMealExtra = new MealExtra();
		mMealExtra.itemAdded.connect(mMealConnections, new Signal1.Handler<MealItem>() {
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

	public LevelResult getLevelResult() {
		return new LevelResult(mLevel, mScore, mRemainingSeconds);
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
		mCustomers.get(mActiveCustomerIndex).pause();
	}

	public void resume() {
		mTimer.start();
		mCustomers.get(mActiveCustomerIndex).resume();
	}

	private void generateTarget() {
		generateTargetBurger();
		generateTargetMealExtra();
	}

	private void generateTargetBurger() {
		Array<String> names = new Array<String>(mLevel.definition.burgerItems);
		int count = MathUtils.random(mLevel.definition.minBurgerSize, mLevel.definition.maxBurgerSize);

		LinkedList<BurgerItem> items = new LinkedList<BurgerItem>();
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
		mTargetBurger.resetArrow();
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
			mBurger.trash();
			mIsTrashing = true;
			trashing.emit();
			mTargetBurger.resetArrow();
		} else {
			mTargetBurger.moveUpArrow();
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
			mBurger.trash();
			mMealExtra.trash();
			mIsTrashing = true;
			trashing.emit();
		}
	}

	private void onBurgerFinished() {
		mTargetBurger.hideArrow();
		burgerFinished.emit();
	}

	private void onMealFinished() {
		emitMealFinished();
		mActiveCustomerIndex++;
		if (mActiveCustomerIndex < mCustomers.size) {
			setupMeal();
			generateTarget();
		} else {
			mTimer.stop();
		}
	}

	private void emitMealFinished() {
		Customer.Mood mood = mCustomers.get(mActiveCustomerIndex).getMood();
		Score score = new Score();
		if (mood == Customer.Mood.HAPPY) {
			int count = 0;
			for (int i = mActiveCustomerIndex; i >= 0; --i) {
				if (mCustomers.get(i).getMood() == Customer.Mood.HAPPY) {
					count++;
				} else {
					break;
				}
			}
			if (count > 1) {
				score.type = Score.Type.COMBO;
				score.delta = HAPPY_SCORE + COMBO_SCORE * count;
				score.message = count + "x combo!";
			} else {
				score.type = Score.Type.HAPPY;
				score.delta = HAPPY_SCORE;
				score.message = "Happy customer!";
			}
		} else if (mood == Customer.Mood.NEUTRAL) {
			score.type = Score.Type.NEUTRAL;
			score.delta = NEUTRAL_SCORE;
		} else {
			score.type = Score.Type.ANGRY;
			score.delta = ANGRY_SCORE;
		}
		mScore += score.delta;
		mealFinished.emit(score);
	}

	private ConnectionManager mMealConnections = new ConnectionManager();
}
