package com.agateau.burgerparty.utils;

public class IntegerListGameStat extends ListGameStat<Integer> {
    @Override
    protected Integer itemForString(String string) {
        return Integer.getInteger(string);
    }

    @Override
    protected String stringForItem(Integer value) {
        return String.valueOf(value);
    }
}
