package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class NewItemScreen extends BurgerPartyScreen {
	private static final int ITEM_X = 400;
	private static final int ITEM_Y = 20;
	private static final float DISPLAY_DURATION = 3;
	private static final float FADE_IN_DURATION = 1;
	private static final float FADE_OUT_DURATION = 1;

	public Signal0 done = new Signal0();

	public NewItemScreen(BurgerPartyGame game, int levelWorld, String newItemName) {
		super(game, Kernel.getSkin());

		String bgName = "levels/" + String.valueOf(levelWorld + 1) + "/newitem-bg";
		String fgName = "levels/" + String.valueOf(levelWorld + 1) + "/newitem-fg";
		mBgImage = new Image(Kernel.getTextureAtlas().findRegion(bgName));
		mBgImage.setFillParent(true);
		mFgGroup = new WidgetGroup();
		mFgImage = new Image(Kernel.getTextureAtlas().findRegion(fgName));

		mItemImage = new Image(Kernel.getTextureAtlas().findRegion("mealitems/" + newItemName));

		getStage().addActor(mBgImage);
		getStage().addActor(mFgGroup);
		mFgGroup.addActor(mFgImage);
		mFgGroup.addActor(mItemImage);

		mFgGroup.setPosition(800, 0);

		mItemImage.setPosition(ITEM_X - mItemImage.getWidth() / 2, ITEM_Y);
	}

	public void show() {
		super.show();
		Actor root = getStage().getRoot();
		root.addAction(Actions.alpha(0));
		root.addAction(
			Actions.sequence(
				Actions.alpha(1, FADE_IN_DURATION),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						fadeFgGroup();
					}
				}),
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
	}

	private void fadeFgGroup() {
		mFgGroup.addAction(
				Actions.parallel(
					Actions.moveTo(0, 0, FADE_IN_DURATION, Interpolation.pow5Out)
				)
			);
	}

	@Override
	public void onBackPressed() {
		done.emit();
	}

	private Image mBgImage;
	private WidgetGroup mFgGroup;
	private Image mFgImage;
	private Image mItemImage;
}
