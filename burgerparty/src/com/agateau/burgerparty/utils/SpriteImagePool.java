package com.agateau.burgerparty.utils;

import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool;

public class SpriteImagePool<T extends SpriteImage> extends Pool<T> {
	public Signal1<T> removalRequested = new Signal1<T>();

	public SpriteImagePool(Class<? extends T> type) {
		mType = type;
		mDrawable = null;
		mMask = null;
	}

	public SpriteImagePool(Class<? extends T> type, TextureRegion region) {
		mType = type;
		mDrawable = new TextureRegionDrawable(region);
		mMask = new CollisionMask(region);
	}

	@Override
	public T newObject() {
		final T obj;
		try {
			obj = mType.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		if (mDrawable != null && mMask != null) {
			obj.init(mDrawable, mMask);
		}
		obj.removalRequested.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				removalRequested.emit(obj);
			}
		});
		return obj;
	}

	private Class<? extends T> mType;
	private Drawable mDrawable;
	private CollisionMask mMask;

	private HashSet<Object> mHandlers = new HashSet<Object>();
}
