package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.InventoryView;

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

public class WorldView extends AnchorGroup {
	HashSet<Object> mHandlers = new HashSet<Object>();

	private TextureRegion mBackgroundRegion;
	private BurgerPartyGame mGame;
	private World mWorld;
	private TextureAtlas mAtlas;
	private Skin mSkin;
	private InventoryView mInventoryView;
	private BurgerView mBurgerView;
	private BurgerView mDoneBurgerView;
	private BurgerView mTargetBurgerView;
	private Label mTimerDisplay;
	private Image mPauseButton;
	private Image mWorkbench;
	private Bubble mBubble;
	private ComposableCustomerFactory mCustomerFactory;
	private Array<Customer> mWaitingCustomers = new Array<Customer>();
	private Customer mActiveCustomer;
	private PauseOverlay mPauseOverlay;
	private LevelResult mResult = null;

	private float mWidth = -1;
	private float mHeight = -1;

	public WorldView(BurgerPartyGame game, World world, TextureAtlas atlas, Skin skin) {
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		mGame = game;
		mWorld = world;
		mAtlas = atlas;
		mSkin = skin;
		mBackgroundRegion = atlas.findRegion("background");
		mCustomerFactory = new ComposableCustomerFactory(atlas);

		setupCustomers();
		setupWorkbench();
		setupTargetBurgerView();
		setupInventoryView();
		setupTimerDisplay();
		setupBurgerView();
		setupAnchors();

		mWorld.burgerFinished.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				onBurgerFinished();
			}
		});
		mWorld.levelFinished.connect(mHandlers, new Signal1.Handler<LevelResult>() {
			public void handle(LevelResult result) {
				onLevelFinished(result);
			}
		});
		mWorld.levelFailed.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				showGameOverOverlay();
			}
		});

		goToNextCustomer();
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
		if (mActiveCustomer != null) {
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
		for (int x = 0; x < mWorld.getCustomerCount(); ++x) {
			Customer customer = mCustomerFactory.create();
			addActor(customer);
			customer.setX(-customer.getWidth());
			mWaitingCustomers.add(customer);
		}
		// Reverse array so that customer with highest Z index is first
		mWaitingCustomers.reverse();
	}

	private void setupWorkbench() {
		TextureRegion region = mAtlas.findRegion("workbench");
		mWorkbench = new Image(region);
		mWorkbench.setScaling(Scaling.stretch);
	}

	private void setupTargetBurgerView() {
		mBubble = new Bubble(mAtlas);
		addActor(mBubble);
		mTargetBurgerView = new BurgerView(mWorld.getTargetBurger(), mAtlas);
		mTargetBurgerView.setScale(0.8f, 0.8f);
		mBubble.setChild(mTargetBurgerView);
		mBubble.setVisible(false);
	}

	private void setupInventoryView() {
		mInventoryView = new InventoryView(mWorld.getInventory(), mAtlas);
		addActor(mInventoryView);
		mInventoryView.itemSelected.connect(mHandlers, new Signal1.Handler<BurgerItem>() {
			@Override
			public void handle(BurgerItem item) {
				mWorld.addItem(item);
			}
		});
	}

	private void setupBurgerView() {
		mBurgerView = new BurgerView(mWorld.getBurger(), mAtlas);
	}

	private void setupTimerDisplay() {
		mTimerDisplay = new Label("0", mSkin);
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
		addRule(mPauseButton, Anchor.TOP_RIGHT, this, Anchor.TOP_RIGHT);
		addRule(mTimerDisplay, Anchor.TOP_RIGHT, mPauseButton, Anchor.TOP_LEFT, -0.5f, 0);
		addRule(mWorkbench, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
		addRule(mBurgerView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 1);
	}

	private void updateTimerDisplay() {
		int total = mWorld.getRemainingSeconds();
		int minutes = total / 60;
		int seconds = total % 60;
		String txt = String.format("%d:%02d", minutes, seconds);
		mTimerDisplay.setText(txt);
		UiUtils.adjustToPrefSize(mTimerDisplay);
	}

	private void showGameOverOverlay() {
		addActor(new GameOverOverlay(mGame, mAtlas, mSkin));
	}

	private void slideDoneBurgerView(Runnable toDoAfter) {
		mDoneBurgerView = mBurgerView;
		removeRulesForActor(mDoneBurgerView);
		mDoneBurgerView.addAction(
			Actions.sequence(
				Actions.delay(BurgerView.ADD_ACTION_DURATION),
				Actions.moveTo(getWidth(), mDoneBurgerView.getY(), 0.4f, Interpolation.pow2In),
				Actions.removeActor()
			)
		);
		mBubble.setVisible(false);
		mActiveCustomer.addAction(
			Actions.sequence(
				Actions.delay(BurgerView.ADD_ACTION_DURATION),
				Actions.moveTo(getWidth(), mActiveCustomer.getY(), 0.4f, Interpolation.pow2In),
				Actions.run(toDoAfter),
				Actions.removeActor()
			)
		);
		mActiveCustomer = null;
	}

	private void createNewBurgerView() {
		setupBurgerView();
		addRule(mBurgerView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 1);
		invalidate();
	}

	private void onBurgerFinished() {
		slideDoneBurgerView(new Runnable() {
			@Override
			public void run() {
				createNewBurgerView();
				goToNextCustomer();
			}
		});
	}

	private void onLevelFinished(LevelResult result) {
		mResult = result;
		mGame.onCurrentLevelFinished(result);
		slideDoneBurgerView(new Runnable() {
			@Override
			public void run() {
				addActor(new LevelFinishedOverlay(mGame, mResult, mAtlas, mSkin));
			}
		});
	}

	private void goToNextCustomer() {
		mActiveCustomer = mWaitingCustomers.removeIndex(0);
		updateCustomerPositions();
	}

	private void updateCustomerPositions() {
		if (mWidth == -1) {
			// Wait until we have been resized to correct sizes
			return;
		}
		Array<Customer> customers = new Array<Customer>(mWaitingCustomers);
		if (mActiveCustomer != null) {
			customers.insert(0, mActiveCustomer);
		}
		float centerX = getWidth() / 2;
		float posY = MathUtils.ceil(mWorkbench.getTop() - 4);
		final float padding = 10;
		float delay = 0;
		for(Customer customer: customers) {
			float width = customer.getWidth();
			customer.addAction(
				Actions.sequence(
					Actions.moveTo(customer.getX(), posY), // Force posY to avoid getting from under the workbench at startup
					Actions.delay(delay),
					Actions.moveTo(MathUtils.ceil(centerX - width / 2), posY, 0.3f, Interpolation.sineOut)
				)
			);
			centerX -= width + padding;
			delay += 0.1;
		}
		if (mActiveCustomer != null) {
			Action doShowBubble = Actions.run(new Runnable() {
				@Override
				public void run() {
					showBubble();
				}
			});
			mActiveCustomer.addAction(Actions.after(doShowBubble));
		}
	}

	private void showBubble() {
		mBubble.setVisible(true);
		mBubble.setPosition(MathUtils.ceil(mActiveCustomer.getRight() - 10), MathUtils.ceil(mActiveCustomer.getY() + 50));
		mBubble.updateGeometry();
	}
}