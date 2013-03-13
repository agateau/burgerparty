package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerStack;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BurgerStackView extends Group {
	private BurgerStack mStack;
	private TextureAtlas mAtlas;
	private float mNextY;

	private final float Overlap = 15;

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
	}

	private void addItem(BurgerItem item) {
		TextureRegion region = mAtlas.findRegion("burgeritems/" + item.getName());
		Image image = new Image(region);
		float regionW = region.getRegionWidth();
		float regionH = region.getRegionHeight();
		float posX = (getWidth() - regionW) / 2;

		image.setBounds(posX, mNextY, regionW, regionH);
		addActor(image);
		mNextY += regionH - Overlap;
	}
}
