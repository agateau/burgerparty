package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;

public class AbstractWorldView extends AnchorGroup {
	public AbstractWorldView(LevelWorld world) {
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		setupDecor(world);
		setupAnchors();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		batch.draw(mBackgroundRegion, 0, 0, getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	@Override
	public void layout() {
		float width = getWidth();
		float height = getHeight();
		boolean resized = width != mWidth || height != mHeight;
		mWidth = width;
		mHeight = height;

		if (resized) {
			mInventoryView.setWidth(width);
			mWorkbench.setWidth(width);
			mWorkbench.invalidate();
		}

		super.layout();

		if (resized) {
			onResized();
		}
	}

	protected void onResized() {
	}

	private void setupDecor(LevelWorld world) {
		TextureAtlas atlas = Kernel.getTextureAtlas();
		String dirName = world.getDirName();

		mBackgroundRegion = atlas.findRegion(dirName + "background");

		TextureRegion region = atlas.findRegion(dirName + "workbench");
		mWorkbench = new Image(region);
		mWorkbench.setScaling(Scaling.stretch);

		mInventoryView = new InventoryView(dirName, atlas);
		addActor(mInventoryView);
	}

	private void setupAnchors() {
		addRule(mWorkbench, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
	}

	protected void createMealViewAnchorRule(MealView mealView) {
		addRule(mealView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 0.48f);
	}

	protected InventoryView mInventoryView;
	protected Image mWorkbench;
	protected float mWidth = -1;
	protected float mHeight = -1;

	private TextureRegion mBackgroundRegion;
}
