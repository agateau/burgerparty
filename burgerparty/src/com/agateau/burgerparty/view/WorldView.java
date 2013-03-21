package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.BurgerItem;
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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorldView extends AnchorGroup {
	HashSet<Object> mHandlers = new HashSet<Object>();

	private TextureRegion mBackgroundRegion;
	private BurgerPartyGame mGame;
	private World mWorld;
	private TextureAtlas mAtlas;
	private Skin mSkin;
	private InventoryView mInventoryView;
	private BurgerStackView mBurgerStackView;
	private BurgerStackView mDoneBurgerStackView;
	private BurgerStackView mTargetBurgerStackView;
	private Label mTimerDisplay;
	private TextButton mPauseButton;
	private Label mScoreLabel;
	private Image mWorkbench;
	private Image mBubble;
	private ComposableCustomerFactory mCustomerFactory;
	private Array<Customer> mWaitingCustomers = new Array<Customer>();
	private Customer mActiveCustomer;
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
		mBackgroundRegion = atlas.findRegion("background");
		mCustomerFactory = new ComposableCustomerFactory(atlas);

		setupCustomers();
		setupWorkbench();
		setupTargetBurgerStackView();
		setupInventoryView();
		setupTimerDisplay();
		setupScoreLabel();
		setupBurgerStackView();
		setupAnchors();

		mWorld.stackFinished.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				showDoneFeedback();
			}
		});
		mWorld.levelFinished.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				showLevelFinishedOverlay();
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
		updateScoreLabel();
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
	}

	private void setupTargetBurgerStackView() {
		mBubble = new Image(mAtlas.findRegion("bubble"));
		addActor(mBubble);
		mTargetBurgerStackView = new BurgerStackView(mWorld.getTargetBurgerStack(), mAtlas);
		addActor(mTargetBurgerStackView);
		mBubble.setVisible(false);
		mTargetBurgerStackView.setVisible(false);
	}

	private void setupInventoryView() {
		mInventoryView = new InventoryView(mWorld.getInventory(), mAtlas);
		addActor(mInventoryView);
		mInventoryView.burgerItemAdded.connect(mHandlers, new Signal1.Handler<BurgerItem>() {
			@Override
			public void handle(BurgerItem item) {
				mWorld.getBurgerStack().addItem(item);
				mWorld.checkStackStatus();
			}
		});
	}

	private void setupBurgerStackView() {
		mBurgerStackView = new BurgerStackView(mWorld.getBurgerStack(), mAtlas);
	}

	private void setupTimerDisplay() {
		mTimerDisplay = new Label("", mSkin);
		mTimerDisplay.setAlignment(Align.center);
		mPauseButton = new TextButton("P", mSkin);
		mPauseButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				pause();
			}
		});
	}

	private void setupScoreLabel() {
		mScoreLabel = new Label("", mSkin);
		mScoreLabel.setAlignment(Align.left);
	}

	private void setupAnchors() {
		moveActor(mScoreLabel, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		moveActor(mPauseButton, Anchor.TOP_RIGHT, this, Anchor.TOP_RIGHT);
		moveActor(mTimerDisplay, Anchor.TOP_RIGHT, mPauseButton, Anchor.TOP_LEFT, -1, 0);
		moveActor(mWorkbench, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
		moveActor(mBurgerStackView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 1);
	}

	private void updateTimerDisplay() {
		int total = mWorld.getRemainingSeconds();
		int minutes = total / 60;
		int seconds = total % 60;
		String txt = String.format("%d:%02d", minutes, seconds);
		mTimerDisplay.setText(txt);
		UiUtils.adjustToPrefSize(mTimerDisplay);
	}

	private void updateScoreLabel() {
		String txt = String.format("SCORE: %07d", mWorld.getScore());
		mScoreLabel.setText(txt);
		UiUtils.adjustToPrefSize(mScoreLabel);
	}

	private void showGameOverOverlay() {
		addActor(new GameOverOverlay(mGame, mAtlas, mSkin));
	}

	private void showDoneFeedback() {
		slideDoneBurgerStackView();
		createNewBurgerStackView();
	}

	private void slideDoneBurgerStackView() {
		mDoneBurgerStackView = mBurgerStackView;
		removeRulesForActor(mDoneBurgerStackView);
		mDoneBurgerStackView.addAction(
			Actions.sequence(
				Actions.delay(BurgerStackView.ADD_ACTION_DURATION),
				Actions.moveTo(getWidth(), mDoneBurgerStackView.getY(), 0.4f, Interpolation.pow2In),
				Actions.removeActor()
			)
		);
		mBubble.setVisible(false);
		mTargetBurgerStackView.setVisible(false);
		mActiveCustomer.addAction(
			Actions.sequence(
				Actions.delay(BurgerStackView.ADD_ACTION_DURATION),
				Actions.moveTo(getWidth(), mActiveCustomer.getY(), 0.4f, Interpolation.pow2In),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						goToNextCustomer();
					}
				}),
				Actions.removeActor()
			)
		);
		mActiveCustomer = null;
	}

	private void createNewBurgerStackView() {
		setupBurgerStackView();
		moveActor(mBurgerStackView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 1);
		invalidate();
	}

	private void showLevelFinishedOverlay() {
		addActor(new LevelFinishedOverlay(mGame, mWorld, mAtlas, mSkin));
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
		float posY = mWorkbench.getTop() - 4;
		final float padding = 10;
		float delay = 0;
		for(Customer customer: customers) {
			float width = customer.getWidth();
			customer.addAction(
				Actions.sequence(
					Actions.moveTo(customer.getX(), posY), // Force posY to avoid getting from under the workbench at startup
					Actions.delay(delay),
					Actions.moveTo(centerX - width / 2, posY, 0.3f, Interpolation.sineOut)
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
		mTargetBurgerStackView.setVisible(true);
		mBubble.setPosition(mActiveCustomer.getRight() - 10, mActiveCustomer.getY() + 50);
		float targetSize = mBubble.getWidth() - 60;
		mTargetBurgerStackView.setScale(Math.min(targetSize / mTargetBurgerStackView.getWidth(), 1));
		mTargetBurgerStackView.setPosition(mBubble.getX() + 40, mBubble.getY() + 10);
	}
}