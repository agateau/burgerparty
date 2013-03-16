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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
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
	private Label mScoreLabel;
	private Actor mGameOverOverlay;
	private Image mWorkbench;
	private Image mBubble;
	private Array<Customer> mCustomers = new Array<Customer>();

	public WorldView(BurgerPartyGame game, World world, TextureAtlas atlas, Skin skin) {
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		mGame = game;
		mWorld = world;
		mAtlas = atlas;
		mSkin = skin;
		mBackgroundRegion = atlas.findRegion("background");

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

		float posX = width / 2;
		float posY = mWorkbench.getTop();
		for (int n = 0; n < mCustomers.size; n++) {
			Customer customer = mCustomers.get(n);
			customer.setScale(1 - 0.1f * n / mCustomers.size);
			customer.setPosition(posX, posY);
			posX += customer.getWidth() * 0.1 * customer.getScaleX();
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateTimerDisplay();
		updateScoreLabel();
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

	private void setupCustomers() {
		for (int x = 0; x < mWorld.getCustomerCount(); ++x) {
			Customer customer = new Customer(mAtlas);
			addActor(customer);
			mCustomers.add(customer);
		}
		mCustomers.reverse();
	}

	private void setupWorkbench() {
		TextureRegion region = mAtlas.findRegion("workbench");
		mWorkbench = new Image(region);
	}

	private void setupTargetBurgerStackView() {
		mTargetBurgerStackView = new BurgerStackView(mWorld.getTargetBurgerStack(), mAtlas);
		mBubble = new Image(mAtlas.findRegion("bubble"));
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

	private void setupAnchors() {
		moveActor(mScoreLabel, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		moveActor(mTimerDisplay, Anchor.TOP_LEFT, mScoreLabel, Anchor.BOTTOM_LEFT);
		moveActor(mWorkbench, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
		moveActor(mBubble, Anchor.TOP_RIGHT, this, Anchor.TOP_RIGHT, -1, -1);
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

	private void showGameOverOverlay() {
		mGameOverOverlay = new GameOverOverlay(mGame, mAtlas, mSkin);
		addActor(mGameOverOverlay);
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
				Actions.moveTo(-mDoneBurgerStackView.getWidth(), mDoneBurgerStackView.getY(), 0.4f, Interpolation.pow2In),
				Actions.removeActor()
			)
		);
		Customer customer = mCustomers.removeIndex(0);
		customer.addAction(
			Actions.sequence(
				Actions.delay(BurgerStackView.ADD_ACTION_DURATION),
				Actions.moveTo(-customer.getWidth(), customer.getY(), 0.4f, Interpolation.pow2In),
				Actions.removeActor()
			)
		);
	}

	private void createNewBurgerStackView() {
		setupBurgerStackView();
		moveActor(mBurgerStackView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 1);
		invalidate();
	}

	private void showLevelFinishedOverlay() {
		addActor(new LevelFinishedOverlay(mGame, mWorld, mAtlas, mSkin));
	}
}