package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Customer;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.InventoryView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

public class WorldView extends AnchorGroup {
	HashSet<Object> mHandlers = new HashSet<Object>();

	private TextureRegion mBackgroundRegion;
	private BurgerPartyGame mGame;
	private World mWorld;
	private TextureAtlas mAtlas;
	private Skin mSkin;
	private InventoryView mInventoryView;
	private MealView mMealView;
	private MealView mDoneMealView;
	private MealView mTargetMealView;
	private Label mTimerDisplay;
	private Label mScoreDisplay;
	private Image mPauseButton;
	private Image mWorkbench;
	private Bubble mBubble;
	private CustomerViewFactory mCustomerFactory;
	private Array<CustomerView> mWaitingCustomerViews = new Array<CustomerView>();
	private CustomerView mActiveCustomerView;
	private PauseOverlay mPauseOverlay;

	private float mWidth = -1;
	private float mHeight = -1;

	public WorldView(BurgerPartyGame game, World world, TextureAtlas atlas, Skin skin) {
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		mGame = game;
		mWorld = world;
		mAtlas = atlas;
		mSkin = skin;
		mBackgroundRegion = atlas.findRegion(world.getLevelWorldDirName() + "background");
		mCustomerFactory = new CustomerViewFactory(atlas, Gdx.files.internal("customerparts.xml"));

		setupCustomers();
		setupWorkbench();
		setupTargetMealView();
		setupInventoryView();
		setupScoreDisplay();
		setupTimerDisplay();
		setupAnchors();

		mWorld.burgerFinished.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				onBurgerFinished();
			}
		});
		mWorld.mealFinished.connect(mHandlers, new Signal1.Handler<World.Score>() {
			public void handle(World.Score score) {
				onMealFinished(score);
			}
		});
		mWorld.getMealExtra().trashed.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				onMealExtraTrashed();
			}
		});
		mWorld.levelFailed.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				showGameOverOverlay();
			}
		});
		mWorld.trashing.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				onTrashing();
			}
		});

		goToNextCustomer();
	}

	public void onTrashing() {
		Timer.schedule(
			new Timer.Task() {
				@Override
				public void run() {
					mWorld.markTrashingDone();
				}
			}, MealView.TRASH_ACTION_DURATION);
	}

	public void pause() {
		mWorld.pause();
		mPauseOverlay = new PauseOverlay(this, mGame, mAtlas, mSkin);
		addActor(mPauseOverlay);
	}

	public void resume() {
		removeActor(mPauseOverlay);
		mPauseOverlay = null;
		mWorld.resume();
	}

	public InventoryView getInventoryView() {
		return mInventoryView;
	}

	@Override
	public void layout() {
		float width = getWidth();
		float height = getHeight();
		boolean resized = width != mWidth || height != mHeight;
		mWidth = width;
		mHeight = height;

		if (resized) {
			mInventoryView.setWidth(width);
			mWorkbench.setWidth(width);
			mWorkbench.invalidate();
		}

		super.layout();

		if (!resized) {
			return;
		}
		if (mActiveCustomerView != null) {
			showBubble();
		}
		updateCustomerPositions();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateTimerDisplay();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		batch.draw(mBackgroundRegion, 0, 0, getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	private void setupCustomers() {
		for (Customer customer: mWorld.getCustomers()) {
			CustomerView customerView = mCustomerFactory.create(customer);
			customerView.setX(-customerView.getWidth());
			mWaitingCustomerViews.add(customerView);
		}
		// Add actors starting from the end of the list so that the Z order is correct
		// (mWaitingCustomerViews[0] is in front of mWaitingCustomerViews[1])
		for (int i = mWaitingCustomerViews.size - 1; i >= 0; --i) {
			addActor(mWaitingCustomerViews.get(i));
		}
	}

	private void setupWorkbench() {
		TextureRegion region = mAtlas.findRegion(mWorld.getLevelWorldDirName() + "workbench");
		mWorkbench = new Image(region);
		mWorkbench.setScaling(Scaling.stretch);
	}

	private void setupTargetMealView() {
		mBubble = new Bubble(mAtlas);
		addActor(mBubble);
		mTargetMealView = new MealView(mWorld.getTargetBurger(), mWorld.getTargetMealExtra(), mAtlas, false);
		mTargetMealView.getBurgerView().setPadding(16);
		mTargetMealView.setScale(0.5f, 0.5f);
		mBubble.setChild(mTargetMealView);
		mBubble.setVisible(false);
	}

	private void setupInventoryView() {
		mInventoryView = new InventoryView(mWorld.getBurgerInventory(), mWorld.getLevelWorldDirName(), mAtlas);
		addActor(mInventoryView);
		mInventoryView.itemSelected.connect(mHandlers, new Signal1.Handler<MealItem>() {
			@Override
			public void handle(MealItem item) {
				if (!mWorld.isTrashing()) {
					mMealView.addItem(item);
				}
			}
		});
	}

	private void setupMealView() {
		mMealView = new MealView(mWorld.getBurger(), mWorld.getMealExtra(), mAtlas, true);
		// We add an anchor rule in this setup method because it is called
		// for each customer
		addRule(mMealView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 0);
		invalidate();
	}

	private void setupScoreDisplay() {
		mScoreDisplay = new Label("0", mSkin, "lcd-font", "lcd-color");
		mScoreDisplay.setAlignment(Align.left);
		updateScoreDisplay();
	}

	private void setupTimerDisplay() {
		mTimerDisplay = new Label("0", mSkin, "lcd-font", "lcd-color");
		mTimerDisplay.setAlignment(Align.center);
		mPauseButton = new Image(mAtlas.findRegion("pause"));
		mPauseButton.setTouchable(Touchable.enabled);
		mPauseButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				pause();
			}
		});
	}

	private void setupAnchors() {
		addRule(mScoreDisplay, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		addRule(mPauseButton, Anchor.TOP_RIGHT, this, Anchor.TOP_RIGHT);
		addRule(mTimerDisplay, Anchor.TOP_RIGHT, mPauseButton, Anchor.TOP_LEFT, -0.5f, 0);
		addRule(mWorkbench, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
	}

	private void updateScoreDisplay() {
		String txt = String.format("%07d", mWorld.getScore());
		mScoreDisplay.setText(txt);
		UiUtils.adjustToPrefSize(mScoreDisplay);
	}

	private void updateTimerDisplay() {
		int total = mWorld.getRemainingSeconds();
		int minutes = total / 60;
		int seconds = total % 60;
		String txt = String.format("%d:%02d", minutes, seconds);
		if (txt.contentEquals(mTimerDisplay.getText())) {
			return;
		}
		if (total >= 20) {
			mTimerDisplay.setColor(Color.WHITE);
		} else {
			mTimerDisplay.setColor(Color.RED);
			mTimerDisplay.addAction(Actions.color(Color.WHITE, 0.5f));
		}
		mTimerDisplay.setText(txt);
		UiUtils.adjustToPrefSize(mTimerDisplay);
	}

	private void showGameOverOverlay() {
		addActor(new GameOverOverlay(mGame, mAtlas, mSkin));
	}

	private void slideDoneMealView(Runnable toDoAfter) {
		mDoneMealView = mMealView;
		removeRulesForActor(mDoneMealView);
		mDoneMealView.addAction(
			Actions.sequence(
				Actions.moveTo(getWidth(), mDoneMealView.getY(), 0.4f, Interpolation.pow2In),
				Actions.removeActor()
			)
		);
		mBubble.setVisible(false);
		mActiveCustomerView.addAction(
			Actions.sequence(
				Actions.moveTo(getWidth(), mActiveCustomerView.getY(), 0.4f, Interpolation.pow2In),
				Actions.run(toDoAfter),
				Actions.removeActor()
			)
		);
		mActiveCustomerView = null;
	}

	private void onMealExtraTrashed() {
		mInventoryView.setInventory(mWorld.getBurgerInventory());
	}

	private void onBurgerFinished() {
		mInventoryView.setInventory(mWorld.getMealExtraInventory());
	}

	private void onMealFinished(World.Score score) {
		mActiveCustomerView.getCustomer().setState(Customer.State.SERVED);
		updateScoreDisplay();
		float x = mMealView.getX() + mMealView.getBurgerView().getWidth() / 2;
		float y = mMealView.getY() + mMealView.getBurgerView().getHeight();
		new ScoreFeedbackActor(this, x, y, score);
		slideDoneMealView(new Runnable() {
			@Override
			public void run() {
				if (mWaitingCustomerViews.size > 0) {
					goToNextCustomer();
				} else {
					showLevelFinishedOverlay();
				}
			}
		});
	}

	private void showLevelFinishedOverlay() {
		LevelResult result = mWorld.getLevelResult();
		addActor(new LevelFinishedOverlay(mGame, result, mAtlas, mSkin));
	}

	private void goToNextCustomer() {
		setupMealView();
		mInventoryView.setInventory(mWorld.getBurgerInventory());
		mActiveCustomerView = mWaitingCustomerViews.removeIndex(0);
		mActiveCustomerView.getCustomer().setState(Customer.State.ACTIVE);
		updateCustomerPositions();
	}

	private void updateCustomerPositions() {
		if (mWidth == -1) {
			// Wait until we have been resized to correct sizes
			return;
		}
		Array<CustomerView> customerViews = new Array<CustomerView>(mWaitingCustomerViews);
		if (mActiveCustomerView != null) {
			customerViews.insert(0, mActiveCustomerView);
		}
		float centerX = getWidth() / 2;
		float posY = MathUtils.ceil(mWorkbench.getTop() - 4);
		final float padding = 10;
		float delay = 0;
		for(CustomerView customerView: customerViews) {
			float width = customerView.getWidth();
			customerView.addAction(
				Actions.sequence(
					Actions.moveTo(customerView.getX(), posY), // Force posY to avoid getting from under the workbench at startup
					Actions.delay(delay),
					Actions.moveTo(MathUtils.ceil(centerX - width / 2), posY, 0.3f, Interpolation.sineOut)
				)
			);
			centerX -= width + padding;
			delay += 0.1;
		}
		if (mActiveCustomerView != null) {
			Action doShowBubble = Actions.run(new Runnable() {
				@Override
				public void run() {
					showBubble();
				}
			});
			mActiveCustomerView.addAction(Actions.after(doShowBubble));
		}
	}

	private void showBubble() {
		mBubble.setVisible(true);
		mBubble.setPosition(MathUtils.ceil(mActiveCustomerView.getRight() - 10), MathUtils.ceil(mActiveCustomerView.getY() + 50));
		mBubble.updateGeometry();
	}
}