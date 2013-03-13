package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerStack;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BurgerStackView extends Group {
	private BurgerStack mStack;
	private TextureAtlas mAtlas;
	private float mNextY;

	private final float OVERLAP = 15;
	private final float ADD_ACTION_HEIGHT = 100;
	private final float ADD_ACTION_DURATION = 0.2f;
	
	private final float TRASH_ACTION_DURATION = 0.5f;

	public BurgerStackView(BurgerStack stack, TextureAtlas atlas) {
		mStack = stack;
		mAtlas = atlas;
		float maxWidth = mAtlas.findRegion("burgeritems/bottom").getRegionWidth();
		setWidth(maxWidth);

		mNextY = 0;

		mStack.burgerItemAdded.connect(new Signal1.Handler<BurgerItem>() {
			public void handle(BurgerItem item) {
				addItem(item);
			}
		});

		mStack.cleared.connect(new Signal0.Handler() {
			public void handle() {
				mNextY = 0;
				clear();
			}
		});
		mStack.trashed.connect(new Signal0.Handler() {
			public void handle() {
				trash();
			}
		});	}

	private void addItem(BurgerItem item) {
		TextureRegion region = mAtlas.findRegion("burgeritems/" + item.getName());
		Image image = new Image(region);
		float regionW = region.getRegionWidth();
		float regionH = region.getRegionHeight();
		float posX = (getWidth() - regionW) / 2;

		image.setBounds(posX, mNextY + ADD_ACTION_HEIGHT, regionW, regionH);
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		addActor(image);

		image.addAction(Actions.alpha(0));
		image.addAction(Actions.parallel(
			Actions.moveBy(0, -ADD_ACTION_HEIGHT, ADD_ACTION_DURATION, Interpolation.pow2In),
			Actions.fadeIn(ADD_ACTION_DURATION)
			));

		mNextY += regionH - OVERLAP;
	}

	private void trash() {
		mNextY = 0;
		for (Actor actor: getChildren()) {
			float xOffset = (float)(Math.random() * 200 - 100);
			float rotation = xOffset;
			actor.addAction(
				Actions.sequence(
					Actions.parallel(
						Actions.moveBy(xOffset, 0, TRASH_ACTION_DURATION),
						Actions.moveBy(0, -200, TRASH_ACTION_DURATION, Interpolation.pow2In),
						Actions.scaleTo(0.5f, 0.5f, TRASH_ACTION_DURATION),
						Actions.rotateBy(rotation, TRASH_ACTION_DURATION),
						Actions.fadeOut(TRASH_ACTION_DURATION, Interpolation.pow5In)
					),
					Actions.removeActor()
				)
			);
		}
	}
}
