package com.agateau.burgerparty;

public class DummyAdSystem implements AdSystem {
    @Override
    public void preloadAd() {
    }

    @Override
    public boolean isAdAvailable() {
        return true;
    }

    @Override
    public void showAd(Runnable after) {
        after.run();
    }
}
