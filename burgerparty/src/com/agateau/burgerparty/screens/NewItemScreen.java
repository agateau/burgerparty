package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.Bubble;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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

	private static class BgActor extends Image {
		private static float DEGREE_PER_SECOND = 20;
		public BgActor(TextureRegion region) {
			super(region);
			mShader = new ShaderProgram(FileUtils.assets("shaders/default-vert.glsl"), FileUtils.assets("shaders/new-item-frag.glsl"));
			if (!mShader.isCompiled()) {
				Gdx.app.error("NewItemScreen", mShader.getLog());
			}
		}
		@Override
		public void act(float delta) {
			mAngle += delta * DEGREE_PER_SECOND;
		}
		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (!mShader.isCompiled()) {
				return;
			}
			mShader.begin();
			mShader.setUniformf("resolution", getWidth(), getHeight());
			mShader.setUniformf("startAngle", mAngle);
			mShader.end();
			batch.setShader(mShader);
			super.draw(batch, parentAlpha);
			batch.setShader(null);
		}
		ShaderProgram mShader;
		private float mAngle = 0;
	}

	public NewItemScreen(BurgerPartyGame game, int levelWorld, MealItem item) {
		super(game);

		String levelDir = "levels/" + String.valueOf(levelWorld + 1);
		String bgName = levelDir + "/newitem-bg";
		String fgName = levelDir + "/newitem-fg";
		mBgActor = new BgActor(game.getAssets().getTextureAtlas().findRegion(bgName));
		setBackgroundActor(mBgActor);
		//mBgImage = new Image(game.getAssets().getTextureAtlas().findRegion(bgName));
		//mBgImage.setFillParent(true);
		mFgGroup = new WidgetGroup();
		mFgImage = new Image(game.getAssets().getTextureAtlas().findRegion(fgName));

		setupBubble(item);

		mBubble.setPosition(-mBubble.getWidth(), mFgImage.getHeight() / 2);

		getStage().addActor(mFgGroup);
		mFgGroup.addActor(mFgImage);
		mFgGroup.addActor(mBubble);

		mFgGroup.setPosition(800, 0);
		mFgGroup.setColor(1, 1, 1, 0);
	}

	private void setupBubble(MealItem newItem) {
		TextureAtlas atlas = getGame().getAssets().getTextureAtlas();
		mBubble = new Bubble(atlas.createPatch("ui/bubble-callout-right"));
		mBubble.setColor(1, 1, 1, 0);

		mBubbleContent = new AnchorGroup();
		mBubble.setChild(mBubbleContent);

		mBubbleLabel = new Label("New item unlocked!", getSkin(), "bubble-text");

		mItemImage = new Image(atlas.findRegion("mealitems/" + newItem.getPath() + "-inventory"));

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
			Actions.alpha(1)
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

	private Actor mBgActor;
	private WidgetGroup mFgGroup;
	private Image mFgImage;
	private Bubble mBubble;
	private AnchorGroup mBubbleContent;
	private Label mBubbleLabel;
	private Image mItemImage;
}
