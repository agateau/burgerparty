package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

public class AbstractWorldView extends AnchorGroup {
	private static final float SLIDE_IN_ANIM_DURATION = 0.2f;

	public AbstractWorldView(Assets assets, String worldDirName) {
		mAssets = assets;
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		setupLayers();
		setupDecor();
		setWorldDirName(worldDirName);
	}

	public void setWorldDirName(String worldDirName) {
		TextureAtlas atlas = mAssets.getTextureAtlas();

		mBackgroundRegion = atlas.findRegion(worldDirName + "background");

		TextureRegion region = atlas.findRegion(worldDirName + "workbench");
		mWorkbench.setDrawable(new TextureRegionDrawable(region));
		mWorkbench.setHeight(region.getRegionHeight());
		mWorkbench.invalidate();

		mInventoryView.setWorldDirName(worldDirName);
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
			for (AnchorGroup layer: mLayers) {
				layer.setSize(width, height);
			}
			mInventoryView.setWidth(width);
			mWorkbench.setBounds(0, mInventoryView.getHeight(), width, mWorkbench.getHeight());
			mWorkbench.invalidate();
		}

		super.layout();

		if (resized) {
			onResized();
		}
	}

	protected float getScrollOffset() {
		return mScrollOffset;
	}

	protected void scrollTo(float offset) {
		if (mScrollOffset == offset) {
			return;
		}
		mScrollOffset = offset;
		mCustomersLayer.addAction(Actions.moveTo(0, -mScrollOffset, 0.2f));
		mCounterLayer.addAction(Actions.moveTo(0, -mScrollOffset, 0.2f));
		layout();
	}

	protected void onResized() {
	}

	private AnchorGroup createLayer() {
		AnchorGroup layer = new AnchorGroup();
		layer.setSpacing(UiUtils.SPACING);
		layer.setTouchable(Touchable.childrenOnly);
		addActor(layer);
		mLayers.add(layer);
		return layer;
	}

	private void setupLayers() {
		mCustomersLayer = createLayer();
		mCounterLayer = createLayer();
		mInventoryLayer = createLayer();
		mHudLayer = createLayer();
	}

	private void setupDecor() {
		mWorkbench = new Image();
		mWorkbench.setScaling(Scaling.stretch);
		mCounterLayer.addActor(mWorkbench);

		mInventoryView = new InventoryView(mAssets.getTextureAtlas());
		mInventoryLayer.addActor(mInventoryView);
	}

	protected void slideInMealView(MealView view) {
		view.setPosition(-view.getWidth(), mWorkbench.getY() + 0.48f * UiUtils.SPACING);
		view.addAction(Actions.moveTo((getWidth() - view.getWidth()) / 2, view.getY(), SLIDE_IN_ANIM_DURATION, Interpolation.pow2Out));
		mCounterLayer.addActor(view);
	}

	protected Assets mAssets;

	private Array<AnchorGroup> mLayers = new Array<AnchorGroup>();
	protected AnchorGroup mCustomersLayer;
	protected AnchorGroup mCounterLayer;
	protected AnchorGroup mInventoryLayer;
	protected AnchorGroup mHudLayer;

	protected InventoryView mInventoryView;
	protected Image mWorkbench;
	protected float mWidth = -1;
	protected float mHeight = -1;

	private TextureRegion mBackgroundRegion;
	private float mScrollOffset = 0;
}
