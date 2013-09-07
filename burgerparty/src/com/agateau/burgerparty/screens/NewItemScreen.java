package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.Bubble;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class NewItemScreen extends BurgerPartyScreen {
	private static final float DISPLAY_DURATION = 3;
	private static final float FADE_IN_DURATION = 1;
	private static final float FADE_OUT_DURATION = 1;

	public Signal0 done = new Signal0();

	public NewItemScreen(BurgerPartyGame game, int levelWorld, String newItemName) {
		super(game, Kernel.getSkin());

		String levelDir = "levels/" + String.valueOf(levelWorld + 1);
		String bgName = levelDir + "/newitem-bg";
		String fgName = levelDir + "/newitem-fg";
		mBgImage = new Image(Kernel.getTextureAtlas().findRegion(bgName));
		mBgImage.setFillParent(true);
		mFgGroup = new WidgetGroup();
		mFgImage = new Image(Kernel.getTextureAtlas().findRegion(fgName));

		setupBubble(levelDir, newItemName);

		mBubble.setPosition(-mBubble.getWidth(), mFgImage.getHeight() / 2);

		getStage().addActor(mBgImage);
		getStage().addActor(mFgGroup);
		mFgGroup.addActor(mFgImage);
		mFgGroup.addActor(mBubble);

		mFgGroup.setPosition(800, 0);
	}

	private void setupBubble(String levelDir, String newItemName) {
		mBubble = new Bubble(Kernel.getTextureAtlas().createPatch(levelDir + "/newitem-bubble"));
		mBubble.setColor(1, 1, 1, 0);

		mBubbleContent = new AnchorGroup();
		mBubble.setChild(mBubbleContent);

		mBubbleLabel = new Label("New item unlocked!", Kernel.getSkin(), "bubble-text");

		mItemImage = new Image(Kernel.getTextureAtlas().findRegion("mealitems/" + newItemName + "-inventory"));

		mBubbleContent.setSize(
			mBubbleLabel.getWidth(),
			mBubbleLabel.getHeight() + UiUtils.SPACING + mItemImage.getHeight());

		mBubbleContent.addRule(mBubbleLabel, Anchor.TOP_CENTER, mBubbleContent, Anchor.TOP_CENTER, 0, 0);
		mBubbleContent.addRule(mItemImage, Anchor.BOTTOM_CENTER, mBubbleContent, Anchor.BOTTOM_CENTER, 0, 0);

		UiUtils.notifyResizeToFitParent(mBubbleContent);
	}

	private static class TimeLine {
		private static class ActorAction {
			Actor actor;
			Action action;
		}
		public void addAction(Actor actor, Action action) {
			ActorAction aa = new ActorAction();
			aa.actor = actor;
			aa.action = Actions.sequence(
				action,
				Actions.run(new Runnable() {
					@Override
					public void run() {
						startNext();
					}
				})
			);
			mList.add(aa);
		}
		public void run() {
			startNext();
		}
		private void startNext() {
			if (mList.size == 0) {
				return;
			}
			ActorAction aa = mList.removeIndex(0);
			aa.actor.addAction(aa.action);
		}
		private Array<ActorAction> mList = new Array<ActorAction>();
	}

	public void show() {
		super.show();
		TimeLine tl = new TimeLine();
		Actor root = getStage().getRoot();
		tl.addAction(
			root,
			Actions.sequence(
				Actions.alpha(0),
				Actions.alpha(1, FADE_IN_DURATION)
			)
		);

		tl.addAction(
			mFgGroup,
			Actions.moveTo(400, 0, FADE_IN_DURATION, Interpolation.pow5Out)
		);

		tl.addAction(
			mBubble,
			Actions.alpha(1, FADE_IN_DURATION)
		);

		tl.addAction(
			root,
			Actions.sequence(
				Actions.delay(DISPLAY_DURATION),
				Actions.alpha(0, FADE_OUT_DURATION),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						done.emit();
					}
				})
			)
		);
		tl.run();
	}

	@Override
	public void onBackPressed() {
		done.emit();
	}

	private Image mBgImage;
	private WidgetGroup mFgGroup;
	private Image mFgImage;
	private Bubble mBubble;
	private AnchorGroup mBubbleContent;
	private Label mBubbleLabel;
	private Image mItemImage;
}
