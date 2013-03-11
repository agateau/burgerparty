package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerStack;
import com.agateau.burgerparty.model.BurgerItem;

import com.agateau.burgerparty.view.TextureDict;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BurgerStackView extends Actor {
	private BurgerStack mStack;
	private TextureDict mTextureDict;

	private final float Overlap = 15;

	public BurgerStackView(BurgerStack stack, TextureDict textureDict) {
		mStack = stack;
		mTextureDict = textureDict;
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		float width = getWidth();
		float maxItemWidth = mTextureDict.getByName("bottom").getWidth();
		float scale = Math.min(width / maxItemWidth, 1);

		float posY = getY();
		for(BurgerItem item: mStack.getItems()) {
			Texture texture = mTextureDict.getByName(item.getName());
			float textureW = texture.getWidth() * scale;
			float textureH = texture.getHeight() * scale;
			float posX = getX() + (width - textureW) / 2;
			spriteBatch.draw(texture, posX, posY, textureW, textureH);
			posY += textureH - Overlap * scale;
		}
	}
}
