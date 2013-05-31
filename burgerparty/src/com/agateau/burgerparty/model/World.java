package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;

public class World {
	public Signal0 burgerFinished = new Signal0();
	public Signal0 mealFinished = new Signal0();
	public Signal1<LevelResult> levelFinished = new Signal1<LevelResult>();
	public Signal0 levelFailed = new Signal0();

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private Timer mTimer = new Timer();

	private Level mLevel;

	private Inventory mBurgerInventory;
	private Inventory mMealExtraInventory;

	private Burger mBurger;
	private MealExtra mMealExtra;

	private Burger mTargetBurger = new Burger();
	private MealExtra mTargetMealExtra = new MealExtra();

	private int mRemainingCustomerCount;
	private int mRemainingSeconds;
	private int mTrashedCount = 0;

	public World(Level level) {
		mLevel = level;
		mRemainingCustomerCount = mLevel.definition.customers.size;
		Array<String> allBurgerItems = new Array<String>(level.definition.burgerItems);
		allBurgerItems.add(level.definition.topBurgerItem);
		allBurgerItems.add(level.definition.bottomBurgerItem);
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

	public Array<String> getCustomerList() {
		return mLevel.definition.customers;
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
		}
	}

	private void onMealItemAdded(MealItem item) {
		MealExtra.CompareResult result = mMealExtra.compareTo(mTargetMealExtra);
		if (result == MealExtra.CompareResult.SAME) {
			onMealFinished();
		} else if (result == MealExtra.CompareResult.DIFFERENT) {
			mTrashedCount++;
			mBurger.trash();
			mMealExtra.trash();
		}
	}

	private void onBurgerFinished() {
		burgerFinished.emit();
	}

	private void onMealFinished() {
		mRemainingCustomerCount--;
		if (mRemainingCustomerCount > 0) {
			setupMeal();
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
