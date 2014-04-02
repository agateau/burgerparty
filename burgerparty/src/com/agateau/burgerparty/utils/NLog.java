package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.TimeUtils;

public class NLog {
    private final Printer mPrinter;
    private final String mTag;

    public static abstract class Printer {
        public Printer() {
            mStartTime = TimeUtils.nanoTime();
        }

        public synchronized void print(int level, String tag, Object obj, Object... args) {
            final float NANOSECS = 1000 * 1000 * 1000;
            final long timeDelta = TimeUtils.nanoTime() - mStartTime;
            final String timeStamp = String.format("%.3f ", timeDelta / NANOSECS);
            final String format = obj == null ? "(null)" : obj.toString();
            final String message = timeStamp + (args.length > 0 ? String.format(format,args) : format);
            doPrint(level, tag, message);
        }

        protected abstract void doPrint(int level, String tag, String message);

        private long mStartTime;

    }

    public NLog(Printer printer, String tag) {
        mPrinter = printer;
        mTag = tag;
    }

    public void d(Object obj, Object...args) {
        mPrinter.print(Application.LOG_DEBUG, mTag, obj, args);
    }

    public void i(Object obj, Object...args) {
        mPrinter.print(Application.LOG_INFO, mTag, obj, args);
    }

    public void e(Object obj, Object...args) {
        mPrinter.print(Application.LOG_ERROR, mTag, obj, args);
    }

    public NLog create(String tag) {
        return new NLog(mPrinter, mTag + "." + tag);
    }

    //// Static
    public static NLog getRoot() {
        if (sRoot == null) {
            init(new DefaultPrinter(), "(root)");
        }
        return sRoot;
    }

    public static void init(Printer printer) {
        sRoot = new NLog(printer, "(root)");
    }

    public static void init(Printer printer, String tag) {
        sRoot = new NLog(printer, tag);
    }

    public static NLog createForClass(Object obj) {
        return getRoot().create(obj.getClass().getSimpleName());
    }

    public static NLog createForInstance(Object obj) {
        return getRoot().create(obj.toString() + "(" + obj.hashCode() + ")");
    }

    private static NLog sRoot = null;
    ////

    /**
     * Implementation of Printer which logs to System.err
     *
     * @author aurelien
     */
    public static class DefaultPrinter extends Printer {
        @Override
        protected void doPrint(int level, String tag, String message) {
            String levelString;
            if (level == Application.LOG_DEBUG) {
                levelString = "D";
            } else if (level == Application.LOG_INFO) {
                levelString = "I";
            } else { // LOG_ERROR
                levelString = "E";
            }
            System.err.printf("%s/%s %s\n", tag, levelString, message);
        }
    }
}
