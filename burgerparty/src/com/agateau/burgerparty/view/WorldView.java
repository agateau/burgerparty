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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

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
	private Label mScoreLabel;
	private Actor mGameOverOverlay;
	private CustomerIndicator mCustomerIndicator;
	private Image mWorkbench;
	private Image mCustomer;
	private Image mBubble;

	public WorldView(BurgerPartyGame game, World world, TextureAtlas atlas, Skin skin) {
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		mGame = game;
		mWorld = world;
		mAtlas = atlas;
		mSkin = skin;
		mBackgroundRegion = atlas.findRegion("background");

		setupCustomer();
		setupWorkbench();
		setupTargetBurgerStackView();
		setupInventoryView();
		setupTimerDisplay();
		setupScoreLabel();
		setupCustomerIndicator();
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
	}

	public InventoryView getInventoryView() {
		return mInventoryView;
	}

	@Override
	public void layout() {
		float width = getWidth();

		mInventoryView.setWidth(width);
		mWorkbench.setWidth(width);

		float targetSize = mBubble.getWidth() - 60;
		mTargetBurgerStackView.setScale(Math.min(targetSize / mTargetBurgerStackView.getWidth(), 1));

		super.layout();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateTimerDisplay();
		updateScoreLabel();
		updateCustomerIndicator();
		if (mWorld.getRemainingSeconds() == 0 && mGameOverOverlay == null) {
			showGameOverOverlay();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		batch.draw(mBackgroundRegion, 0, 0, getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	private void setupCustomer() {
		TextureRegion region = mAtlas.findRegion("customer");
		mCustomer = new Image(region);
		mBubble = new Image(mAtlas.findRegion("bubble"));
	}

	private void setupWorkbench() {
		TextureRegion region = mAtlas.findRegion("workbench");
		mWorkbench = new Image(region);
	}

	private void setupTargetBurgerStackView() {
		mTargetBurgerStackView = new BurgerStackView(mWorld.getTargetBurgerStack(), mAtlas);
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
	}

	private void setupScoreLabel() {
		mScoreLabel = new Label("", mSkin);
		mScoreLabel.setAlignment(Align.left);
	}

	private void setupCustomerIndicator() {
		mCustomerIndicator = new CustomerIndicator(mAtlas);
	}

	private void setupAnchors() {
		moveActor(mScoreLabel, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		moveActor(mTimerDisplay, Anchor.TOP_LEFT, mScoreLabel, Anchor.BOTTOM_LEFT);
		moveActor(mCustomerIndicator, Anchor.TOP_LEFT, mTimerDisplay, Anchor.BOTTOM_LEFT);
		moveActor(mCustomer, Anchor.TOP_LEFT, mInventoryView, Anchor.TOP_CENTER, -2, 12);
		moveActor(mWorkbench, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
		moveActor(mBubble, Anchor.BOTTOM_LEFT, mCustomer, Anchor.TOP_RIGHT, -1, -5);
		moveActor(mTargetBurgerStackView, Anchor.BOTTOM_RIGHT, mBubble, Anchor.BOTTOM_RIGHT, -1, 1);
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

	private void updateCustomerIndicator() {
		mCustomerIndicator.setCount(mWorld.getCustomerCount());
		mCustomerIndicator.setScale(0.5f);
	}

	private void showGameOverOverlay() {
		mGameOverOverlay = new GameOverOverlay(mGame, mAtlas, mSkin);
		addActor(mGameOverOverlay);
	}

	private void showDoneFeedback() {
		showDoneActor();
		mDoneBurgerStackView = mBurgerStackView;
		removeRulesForActor(mDoneBurgerStackView);
		mDoneBurgerStackView.addAction(
			Actions.sequence(
				Actions.delay(BurgerStackView.ADD_ACTION_DURATION),
				Actions.moveTo(getWidth(), mDoneBurgerStackView.getY(), 0.4f, Interpolation.pow2In),
				Actions.removeActor()
			)
		);
		setupBurgerStackView();
		moveActor(mBurgerStackView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 1);
		invalidate();
	}
	
	private void showDoneActor() {
		TextureRegion region = mAtlas.findRegion("done");
		Image doneActor = new Image(region);
		doneActor.setTouchable(Touchable.disabled);

		float centerX = mBurgerStackView.getX() + mBurgerStackView.getWidth() * mBurgerStackView.getScaleX() / 2;
		float centerY = mBurgerStackView.getY() + getHeight() / 2;

		float width = doneActor.getWidth();
		float height = doneActor.getHeight();
		doneActor.setBounds(centerX - width / 2, centerY - height / 2, width, height);

		addActor(doneActor);
		doneActor.addAction(
			Actions.sequence(
				Actions.alpha(0),
				Actions.delay(BurgerStackView.ADD_ACTION_DURATION),
				Actions.alpha(1),
				Actions.delay(0.3f),
				Actions.fadeOut(0.05f),
				Actions.removeActor()
			)
		);
	}

	private void showLevelFinishedOverlay() {
		addActor(new LevelFinishedOverlay(mGame, mWorld, mAtlas, mSkin));
	}
}