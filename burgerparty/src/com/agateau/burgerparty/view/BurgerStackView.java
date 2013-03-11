package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerStack;
import com.agateau.burgerparty.model.BurgerItem;

import com.agateau.burgerparty.view.TextureDict;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BurgerStackView {
	private BurgerStack mStack;
	private TextureDict mTextureDict;

	private final float Offset = 600;
	private final float Overlap = 15;

	public BurgerStackView(BurgerStack stack, TextureDict textureDict) {
		mStack = stack;
		mTextureDict = textureDict;
	}

	public void drawSprites(SpriteBatch spriteBatch) {
		float posY = 0;
		for(BurgerItem item: mStack.getItems()) {
			Texture texture = mTextureDict.getByName(item.getName());
			float posX = Offset - texture.getWidth() / 2;
			spriteBatch.draw(texture, posX, posY);
			posY += texture.getHeight() - Overlap;
		}
	}
}
