package com.agateau.burgerparty.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class SpriteImage extends Image {
	public static class CollisionMask {
		private static int ALPHA_THRESHOLD = 128;
		public CollisionMask(TextureRegion region) {
			Texture texture = region.getTexture();
			TextureData data = texture.getTextureData();

			data.prepare();
			Pixmap pixmap = data.consumePixmap();
			int startX = region.getRegionX();
			int startY = region.getRegionY();
			mWidth = region.getRegionWidth();
			mHeight = region.getRegionHeight();
			mBits = new boolean[mHeight][mWidth];
			for (int y = 0; y < mHeight; ++y) {
				for (int x = 0; x < mWidth; ++x) {
					int px = pixmap.getPixel(startX + x, startY + y);
					mBits[mHeight - y - 1][x] = (px & 255) > ALPHA_THRESHOLD;
				}
			}
			data.disposePixmap();
		}

		public boolean collide(CollisionMask other, int dx, int dy) {
			int otherX, otherY;
			for (int y = 0; y < mHeight; ++y) {
				otherY = y - dy;
				if (otherY < 0 || otherY >= other.mHeight) {
					continue;
				}
				for (int x = 0; x < mWidth; ++x) {
					if (!mBits[y][x]) {
						continue;
					}
					otherX = x - dx;
					if (otherX < 0 || otherX >= other.mWidth) {
						continue;
					}
					if (other.mBits[otherY][otherX]) {
						return true;
					}
				}
			}
			return false;
		}

		public void save(FileHandle handle) {
			final Pixmap pixmap = new Pixmap(mWidth, mHeight, Format.RGBA8888);
			int white = 0xffffffff;
			int black = 0x000000ff;
			for (int y = 0; y < mHeight; ++y) {
				for (int x = 0; x < mWidth; ++x) {
					pixmap.drawPixel(x, y, mBits[y][x] ? white : black);
				}
			}
	
			PixmapIO.writePNG(handle, pixmap);
			pixmap.dispose();
		}

		private final int mWidth;
		private final int mHeight;
		private final boolean mBits[][];
	}

	public SpriteImage() {
	}

	public SpriteImage(TextureRegion region) {
		super(region);
		mMask = new CollisionMask(region);
	}

	public SpriteImage(TextureRegion region, CollisionMask mask) {
		super(region);
		mMask = mask;
	}

	public SpriteImage(Drawable drawable, CollisionMask mask) {
		super(drawable);
		mMask = mask;
	}

	public void init(Drawable drawable, CollisionMask mask) {
		setDrawable(drawable);
		mMask = mask;
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public static boolean collide(SpriteImage i1, SpriteImage i2) {
		if (!boundCollide(i1, i2)) {
			return false;
		}
		return i1.mMask.collide(i2.mMask, (int)(i2.getX() - i1.getX()), (int)(i2.getY() - i1.getY()));
	}

	public static boolean boundCollide(Actor a1, Actor a2) {
		if (!a1.isVisible()) {
			return false;
		}
		if (!a2.isVisible()) {
			return false;
		}
		float a1Left = a1.getX();
		float a1Right = a1.getRight();
		float a1Bottom = a1.getY();
		float a1Top = a1.getTop();
		float a2Left = a2.getX();
		float a2Right = a2.getRight();
		float a2Bottom = a2.getY();
		float a2Top = a2.getTop();
		if (a1Right < a2Left) {
			return false;
		}
		if (a2Right < a1Left) {
			return false;
		}
		if (a1Top < a2Bottom) {
			return false;
		}
		if (a2Top < a1Bottom) {
			return false;
		}
		return true;
	}

	private CollisionMask mMask;
}
