package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class NLog {
	public static void d(Object s1, Object... args) {
		sPrinter.print(Application.LOG_DEBUG, s1, args);
	}

	public static void i(Object s1, Object... args) {
		sPrinter.print(Application.LOG_INFO, s1, args);
	}

	public static void e(Object s1, Object... args) {
		sPrinter.print(Application.LOG_ERROR, s1, args);
	}

	public static void init(Printer printer) {
		sPrinter = printer;
	}

	public static abstract class Printer {
		Printer(String tag) {
			mTag = tag;
		}

		public abstract void print(int level, Object obj, Object... args);
	
		protected final String mTag;
	}

	public static class GdxPrinter extends Printer {
		public GdxPrinter(String tag) {
			super(tag);
		}

		@Override
		public void print(int level, Object obj, Object... args) {
			final String format = obj == null ? "(null)" : obj.toString();
			final String message = args.length > 0 ? String.format(format,args) : format;
			if (level == Application.LOG_DEBUG) {
				Gdx.app.debug(mTag, message);
			} else if (level == Application.LOG_INFO) {
				Gdx.app.log(mTag, message);
			} else { // LOG_ERROR
				Gdx.app.error(mTag, message);
			}
		}
	}

	private static Printer sPrinter;
}
