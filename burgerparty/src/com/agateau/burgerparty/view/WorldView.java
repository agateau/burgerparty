package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Customer;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.InventoryView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class WorldView extends AbstractWorldView {
	private static final float TARGET_BURGER_PADDING = 24;
	private static final float SCROLL_PADDING = 24;

	public WorldView(GameScreen screen, BurgerPartyGame game, World world) {
		super(game.getAssets(), world.getLevelWorld().getDirName());
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		mGameScreen = screen;
		mGame = game;
		mWorld = world;
		mAtlas = mAssets.getTextureAtlas();
		mSkin = mAssets.getSkin();
		mCustomerFactory = new CustomerViewFactory(mAtlas, Gdx.files.internal("customerparts.xml"));

		setupCustomers();
		setupTargetMealView();
		setupInventoryView();
		setupHud();
		setupCoinView();
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
		mWorld.getBurger().itemAdded.connect(mHandlers, new Signal1.Handler<MealItem>() {
			@Override
			public void handle(MealItem item) {
				onBurgerItemAdded();
			}
		});

		scheduleGoToNextCustomer();
	}

	private void scheduleGoToNextCustomer() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (getWidth() == 0) {
					// This happens for some reason when NewItemScreen is shown before level starts
					NLog.i("WorldView.scheduleGoToNextCustomer.Runnable: Not ready yet, rescheduling");
					scheduleGoToNextCustomer();
				} else {
					goToNextCustomer();
				}
			}
		});
	}

	public void onTrashing() {
		mInventoryView.setInventory(mWorld.getBurgerInventory());
		Timer.schedule(
			new Timer.Task() {
				@Override
				public void run() {
					mWorld.markTrashingDone();
					scrollTo(0);
				}
			}, MealView.TRASH_ACTION_DURATION);
	}

	public void onBurgerItemAdded() {
		BurgerView view = mMealView.getBurgerView();
		float top = UiUtils.toAscendantCoordinates(this, view, new Vector2(0, view.getHeight())).y + SCROLL_PADDING;
		float offset = Math.max(0, getScrollOffset() + top - getHeight());
		scrollTo(offset);
	}

	public void onBackPressed() {
		pause();
	}

	public void pause() {
		if (mGameScreen.getOverlay() != null) {
			// This can happen when called from GameScreen.pause()
			return;
		}
		mWorld.pause();
		mGameScreen.setOverlay(new PauseOverlay(this, mGame, mAtlas, mSkin));
	}

	public void resume() {
		mGameScreen.setOverlay(null);
		mWorld.resume();
	}

	public InventoryView getInventoryView() {
		return mInventoryView;
	}

	protected void onResized() {
		if (mActiveCustomerView != null) {
			updateBubbleGeometry();
		}
		updateCustomerPositions();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateTimerDisplay();
	}

	private void setupCustomers() {
		for (Customer customer: mWorld.getCustomers()) {
			CustomerView customerView = mCustomerFactory.create(customer);
			customerView.setX(300);
			customerView.setColor(1, 1, 1, 0);
			customerView.setScale(0.1f);
			mWaitingCustomerViews.add(customerView);
		}
		// Add actors starting from the end of the list so that the Z order is correct
		// (mWaitingCustomerViews[0] is in front of mWaitingCustomerViews[1])
		for (int i = mWaitingCustomerViews.size - 1; i >= 0; --i) {
			mCustomersLayer.addActor(mWaitingCustomerViews.get(i));
		}
	}

	private void setupTargetMealView() {
		mBubble = new Bubble(mAtlas.createPatch("ui/bubble-callout-left"));
		mCustomersLayer.addActor(mBubble);

		mTargetMealView = new MealView(mWorld.getTargetBurger(), mWorld.getTargetMealExtra(), mAtlas, mAssets.getSoundAtlas(), mAssets.getAnimScriptLoader(), false);
		mTargetMealView.getBurgerView().setPadding(TARGET_BURGER_PADDING);
		mTargetMealView.getMealExtraView().setOverlapping(false);

		mTargetMealScrollPane = new MealViewScrollPane(mTargetMealView, mAssets.getTextureAtlas());
		mTargetMealScrollPane.setScale(0.5f, 0.5f);

		mBubble.setChild(mTargetMealScrollPane);
		mBubble.setVisible(false);
	}

	private void setupInventoryView() {
		mInventoryView.setInventory(mWorld.getBurgerInventory());
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
		scrollTo(0);
		mMealView = new MealView(mWorld.getBurger(), mWorld.getMealExtra(), mAtlas, mAssets.getSoundAtlas(), mAssets.getAnimScriptLoader(), true);
		slideInMealView(mMealView);
	}

	private void setupHud() {
		mHudImage = new Image(mAtlas.findRegion("ui/hud-bg"));
		mScoreDisplay = new Label("0", mSkin, "score");
		updateScoreDisplay();
		mTimerDisplay = new Label("0", mSkin, "timer");
		mPauseButton = new Image(mAtlas.findRegion("ui/pause"));

		ClickListener listener = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				pause();
			}
		};
		for (Actor actor: new Actor[]{mHudImage, mTimerDisplay, mScoreDisplay, mPauseButton}) {
			actor.setTouchable(Touchable.enabled);
			actor.addListener(listener);
		}
	}

	private void setupCoinView() {
		int maxCoinCount = mWorld.getMaximumCoinCount();
		mCoinView = new CoinView(mAssets, mWorld.getStarCost(), maxCoinCount);
	}

	private void setupAnchors() {
		mHudLayer.addRule(mHudImage, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT, 0, 0);
		mHudLayer.addRule(mPauseButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT, 0.7f, -0.6f);
		mHudLayer.addRule(mTimerDisplay, Anchor.CENTER_LEFT, mPauseButton, Anchor.CENTER_LEFT, 1.2f, 0);
		mHudLayer.addRule(mScoreDisplay, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT, 0.7f, -1.6f);
		mHudLayer.addRule(mCoinView, Anchor.BOTTOM_LEFT, mCounter, Anchor.BOTTOM_LEFT, 1, 1);
	}

	private void updateScoreDisplay() {
		String txt = String.format("%07d", mWorld.getScore());
		mScoreDisplay.setText(txt);
		UiUtils.adjustToPrefSize(mScoreDisplay);
	}

	private void updateCoinView() {
		mCoinView.setCoinCount(mWorld.getCoinCount());
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
			mGame.getAssets().getSoundAtlas().findSound("tick").play();
		}
		mTimerDisplay.setText(txt);
		UiUtils.adjustToPrefSize(mTimerDisplay);
	}

	private void showGameOverOverlay() {
		mGameScreen.setOverlay(new GameOverOverlay(mGame, mAtlas, mSkin));
	}

	private void slideDoneMealView(Runnable toDoAfter) {
		mDoneMealView = mMealView;
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

	private void onBurgerFinished() {
		mInventoryView.setInventory(mWorld.getMealExtraInventory());
		scrollTo(0);
	}

	private void onMealFinished(World.Score score) {
		scrollTo(0);
		mActiveCustomerView.getCustomer().markServed();
		updateScoreDisplay();
		updateCoinView();
		float x = mMealView.getX() + mMealView.getBurgerView().getWidth() / 2;
		float y = mMealView.getY() + mMealView.getBurgerView().getHeight();
		new ScoreFeedbackActor(this, x, y, score, mAssets.getSkin(), mAssets.getAnimScriptLoader());
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
		mGameScreen.setOverlay(new LevelFinishedOverlay(mGame, result, mAtlas, mSkin));
	}

	private void goToNextCustomer() {
		setupMealView();
		mInventoryView.setInventory(mWorld.getBurgerInventory());
		mActiveCustomerView = mWaitingCustomerViews.removeIndex(0);
		mActiveCustomerView.getCustomer().markActive(mWorld.getTargetComplexity());
		updateCustomerPositions();
	}

	private void updateCustomerPositions() {
		Array<CustomerView> customerViews = new Array<CustomerView>(mWaitingCustomerViews);
		if (mActiveCustomerView != null) {
			customerViews.insert(0, mActiveCustomerView);
		}
		float centerX = getWidth() / 2;
		float posY = MathUtils.ceil(mCounter.getTop() - 4);
		float delay = 0;
		int rank = 0;
		for(CustomerView customerView: customerViews) {
			float width = customerView.getWidth();
			float posX = centerX - width / 2;
			float scale = 1f - rank / 25f;
			customerView.addAction(
				Actions.sequence(
					Actions.moveTo(customerView.getX(), posY), // Force posY to avoid getting from under the counter at startup
					Actions.delay(delay),
					Actions.parallel(
						Actions.moveTo(MathUtils.ceil(posX), posY, 0.3f, Interpolation.sineOut),
						Actions.scaleTo(scale, scale, 0.3f),
						Actions.alpha(1, 0.3f, Interpolation.exp10)
					)
				)
			);
			centerX -= width / (3 + 2 * rank);
			delay += 0.2;
			rank += 1;
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
		updateBubbleGeometry();
	}

	private void updateBubbleGeometry() {
		mBubble.setPosition(MathUtils.ceil(mActiveCustomerView.getRight() - 10), MathUtils.ceil(mActiveCustomerView.getY() + 50));
		// Adjust scroll pane so that it does not grow outside of screen
		Vector2 coord = UiUtils.toChildCoordinates(this, mTargetMealScrollPane, new Vector2(0, getHeight()));
		mTargetMealScrollPane.setMaximumHeight(coord.y);
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private GameScreen mGameScreen;
	private BurgerPartyGame mGame;
	private World mWorld;
	private TextureAtlas mAtlas;
	private Skin mSkin;
	private MealView mMealView;
	private MealView mDoneMealView;
	private MealViewScrollPane mTargetMealScrollPane;
	private MealView mTargetMealView;
	private Label mTimerDisplay;
	private Label mScoreDisplay;
	private Image mHudImage;
	private Image mPauseButton;
	private Bubble mBubble;
	private CustomerViewFactory mCustomerFactory;
	private Array<CustomerView> mWaitingCustomerViews = new Array<CustomerView>();
	private CustomerView mActiveCustomerView;
	private CoinView mCoinView;
}
