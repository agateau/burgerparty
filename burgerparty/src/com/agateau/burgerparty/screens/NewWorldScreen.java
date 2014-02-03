package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.XmlReader;

public class NewWorldScreen extends BurgerPartyScreen {
	private int mWorldIndex;
	private float mDuration;
	private float mTime = 0;

	private float mDotInterval;
	private float mLastDotTime = 0;
	private TextureRegion mDotRegion;

	private CatmullRomSpline<Vector2> mBSpline = new CatmullRomSpline<Vector2>();
	private Vector2 mTmpV = new Vector2();
	private Image mPlane;

	public NewWorldScreen(BurgerPartyGame game, int worldIndex) {
		super(game);
		mWorldIndex = worldIndex;

		setBackgroundActor(new Image(getTextureAtlas().findRegion("newworld/map")));
		mDotRegion = getTextureAtlas().findRegion("newworld/dot");
		createPlane();
		loadXml();
		createRefreshHelper();
	}

	@Override
	public void onBackPressed() {
		startNextLevel();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		boolean done = act(delta);
		if (done) {
			startNextLevel();
		}
	}

	private boolean act(float delta) {
		mTime += delta;
		if (mTime > mDuration) {
			mTime = mDuration;
			return true;
		}
		mBSpline.valueAt(mTmpV, mTime / mDuration);
		mPlane.setPosition(mTmpV.x * getStage().getWidth(), mTmpV.y * getStage().getHeight());
		if (mTime - mLastDotTime > mDotInterval) {
			addDot();
			mLastDotTime = mTime;
		}
		return false;
	}

	private void addDot() {
		Image image = new Image(mDotRegion);
		mBSpline.valueAt(mTmpV, (mTime - mDotInterval / 2) / mDuration);
		image.setPosition(mTmpV.x * getStage().getWidth(), mTmpV.y * getStage().getHeight());
		getStage().addActor(image);
		mPlane.toFront();
	}

	private void createPlane() {
		mPlane = new Image(getTextureAtlas().findRegion("newworld/plane"));
		mPlane.setOrigin(mPlane.getImageWidth() / 2, mPlane.getImageHeight() / 2);
		getStage().addActor(mPlane);
	}

	private void loadXml() {
		XmlReader.Element rootElement = FileUtils.parseXml(FileUtils.assets("levels/" + (mWorldIndex + 1) + "/newworld.xml"));
		mDuration = rootElement.getFloatAttribute("duration");
		mDotInterval = rootElement.getFloatAttribute("dotInterval");

		XmlReader.Element pointsElement = rootElement.getChildByName("points");
		Vector2[] points = new Vector2[pointsElement.getChildCount()];
		int idx = 0;
		for (XmlReader.Element pointElement: pointsElement.getChildrenByName("point")) {
			points[idx++] = new Vector2(pointElement.getFloatAttribute("x"), pointElement.getFloatAttribute("y"));
		}
		assert(points.length >= 2);
		mBSpline.set(points, true);
	}

	private void createRefreshHelper() {
		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showNewWorldScreen(mWorldIndex);
			}
		};
	}

	private void startNextLevel() {
		getGame().startLevel(mWorldIndex, 0);
	}
}
