package com.agateau.burgerparty.utils;

import roboguice.util.Strings;

import com.badlogic.gdx.Gdx;

public class NLog {
	public static void d(Object s1, Object... args) {
		final String s = Strings.toString(s1);
		final String message = args.length > 0 ? String.format(s,args) : s;
		sPrinter.d(message);
	}

	public static void i(Object s1, Object... args) {
		final String s = Strings.toString(s1);
		final String message = args.length > 0 ? String.format(s,args) : s;
		sPrinter.i(message);
	}

	public static void e(Object s1, Object... args) {
		final String s = Strings.toString(s1);
		final String message = args.length > 0 ? String.format(s,args) : s;
		sPrinter.e(message);
	}

	public static void init(Printer printer) {
		sPrinter = printer;
	}

	public static abstract class Printer {
		Printer(String tag) {
			mTag = tag;
		}

		public abstract void d(String message);
		public abstract void i(String message);
		public abstract void e(String message);
	
		protected final String mTag;
	}

	public static class GdxPrinter extends Printer {
		public GdxPrinter(String tag) {
			super(tag);
		}

		@Override
		public void d(String message) {
			Gdx.app.debug(mTag, message);
		}

		@Override
		public void i(String message) {
			Gdx.app.log(mTag, message);
		}

		@Override
		public void e(String message) {
			Gdx.app.error(mTag, message);
		}
	}

	private static Printer sPrinter;
}
