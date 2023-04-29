package com.agateau.burgerparty.utils;

public class StringListGameStat extends ListGameStat<String> {
    @Override
    protected String itemForString(String string) {
        return string;
    }

    @Override
    protected String stringForItem(String value) {
        return value;
    }
}
