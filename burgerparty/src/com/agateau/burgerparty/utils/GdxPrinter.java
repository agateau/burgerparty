package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.NLog.Printer;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

/**
 * Implementation of Printer which uses Gdx.app logging facilities
 *
 * @author aurelien
 */
public class GdxPrinter extends Printer {
    @Override
    protected void doPrint(int level, String tag, String message) {
        if (level == Application.LOG_DEBUG) {
            Gdx.app.debug(tag, message);
        } else if (level == Application.LOG_INFO) {
            Gdx.app.log(tag, message);
        } else { // LOG_ERROR
            Gdx.app.error(tag, message);
        }
    }
}