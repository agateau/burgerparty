package com.agateau.burgerparty.model;

import java.util.LinkedList;
import java.util.Set;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.ConnectionManager;
import com.agateau.burgerparty.utils.Counter;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
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
		public int deltaScore;
		public int deltaCoinCount;
	}
	public Signal0 burgerFinished = new Signal0();
	public Signal1<Score> mealFinished = new Signal1<Score>();
	public Signal0 levelFailed = new Signal0();
	public Signal0 trashing = new Signal0();

	private static final int COMBO_SCORE = 1000;
	private static final int HAPPY_SCORE = 4000;
	private static final int NEUTRAL_SCORE = 2000;
	private static final int ANGRY_SCORE = 1000;

	private static final int HAPPY_COIN_COUNT = 3;
	private static final int NEUTRAL_COIN_COUNT = 2;
	private static final int ANGRY_COIN_COUNT = 1;

	public World(Level level) {
		mLevel = level;
		mCustomers = level.definition.createCustomers();
		int worldIndex = level.getLevelWorld().getIndex();
		mBurgerGenerator = new BurgerGenerator(worldIndex, mLevel.definition.getBurgerItems());
		mMealExtraGenerator = new MealExtraGenerator(mLevel.definition.getExtraItems());
		mBurgerInventory.setItems(level.definition.getBurgerItems());
		mMealExtraInventory.setItems(level.definition.getExtraItems());

		final int STAR_COUNT = 3;
		mStarCost = HAPPY_COIN_COUNT * mCustomers.size / STAR_COUNT - 1;

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

	public LevelWorld getLevelWorld() {
		return mLevel.getLevelWorld();
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

	public int getCoinCount() {
		return mCoinCount;
	}

	public int getMaximumCoinCount() {
		return HAPPY_COIN_COUNT * mCustomers.size;
	}

	public int getStarCost() {
		return mStarCost;
	}

	public LevelResult getLevelResult() {
		return new LevelResult(mLevel, mScore, mCoinCount,  getMaximumCoinCount(), mStarCost, mRemainingSeconds);
	}

	public int getTargetComplexity() {
		return mTargetBurger.getItems().size() + mTargetMealExtra.getItems().size();
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
		mItemAddedCounter.start();
		mMealDoneCounter.start();
		generateTargetBurger();
		generateTargetMealExtra();
	}

	private void generateTargetBurger() {
		int count = mCustomers.get(mActiveCustomerIndex).getBurgerSize();
		LinkedList<BurgerItem> items = mBurgerGenerator.run(count);
		mTargetBurger.setItems(items);
		mTargetBurger.resetArrow();
	}

	private void generateTargetMealExtra() {
		Set<MealItem> items = mMealExtraGenerator.run();
		mTargetMealExtra.setItems(items);
	}

	private void onBurgerItemAdded() {
		restartItemTimer();
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
		restartItemTimer();
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
			mTargetBurger.resetArrow();
		}
	}

	private void onBurgerFinished() {
		mTargetBurger.hideArrow();
		burgerFinished.emit();
	}

	private void onMealFinished() {
		Gdx.app.log("World", "Meal done in " + mMealDoneCounter.restart() + "ms");
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
			score.deltaCoinCount = HAPPY_COIN_COUNT;
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
				score.deltaScore = HAPPY_SCORE + COMBO_SCORE * count;
				score.message = count + "x combo!";
			} else {
				score.type = Score.Type.HAPPY;
				score.deltaScore = HAPPY_SCORE;
				score.message = "Happy customer!";
			}
		} else if (mood == Customer.Mood.NEUTRAL) {
			score.type = Score.Type.NEUTRAL;
			score.deltaScore = NEUTRAL_SCORE;
			score.deltaCoinCount = NEUTRAL_COIN_COUNT;
		} else {
			score.type = Score.Type.ANGRY;
			score.deltaScore = ANGRY_SCORE;
			score.deltaCoinCount = ANGRY_COIN_COUNT;
		}
		mScore += score.deltaScore;
		mCoinCount += score.deltaCoinCount;
		mealFinished.emit(score);
	}

	private void restartItemTimer() {
		Gdx.app.log("World", "Item added in " + mItemAddedCounter.restart() + "ms");
	}

	private ConnectionManager mMealConnections = new ConnectionManager();
	private BurgerGenerator mBurgerGenerator;
	private MealExtraGenerator mMealExtraGenerator;
	private Counter mItemAddedCounter = new Counter();
	private Counter mMealDoneCounter = new Counter();

	private Timer mTimer = new Timer();

	private Level mLevel;
	private int mStarCost;

	private Inventory mBurgerInventory = new Inventory();
	private Inventory mMealExtraInventory = new Inventory();

	private Burger mBurger;
	private MealExtra mMealExtra;

	private Burger mTargetBurger = new Burger();
	private MealExtra mTargetMealExtra = new MealExtra();

	private Array<Customer> mCustomers = new Array<Customer>();
	private int mActiveCustomerIndex = 0;
	private int mRemainingSeconds;
	private int mScore = 0;
	private int mCoinCount = 0;

	private boolean mIsTrashing = false; // Set to true when we are in the middle of a trash animation
}
