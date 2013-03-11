package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerStack;
import com.agateau.burgerparty.model.BurgerItem;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BurgerStackView extends Actor {
	private BurgerStack mStack;
	private TextureAtlas mAtlas;

	private final float Overlap = 15;

	public BurgerStackView(BurgerStack stack, TextureAtlas atlas) {
		mStack = stack;
		mAtlas = atlas;
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		float width = getWidth();
		float maxItemWidth = mAtlas.findRegion("burgeritems/bottom").getRegionWidth();
		float scale = Math.min(width / maxItemWidth, 1);

		float posY = getY();
		for(BurgerItem item: mStack.getItems()) {
			TextureRegion texture = mAtlas.findRegion("burgeritems/" + item.getName());
			float textureW = texture.getRegionWidth() * scale;
			float textureH = texture.getRegionHeight() * scale;
			float posX = getX() + (width - textureW) / 2;
			spriteBatch.draw(texture, posX, posY, textureW, textureH);
			posY += textureH - Overlap * scale;
		}
	}
}
