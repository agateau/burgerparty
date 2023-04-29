package com.agateau.burgerparty.utils;

public interface Signal {
    public interface Handler {
    }

    void disconnect(Handler handler);
}
