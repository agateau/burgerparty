package com.agateau.burgerparty.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CollisionMask {
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