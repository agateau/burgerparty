package com.agateau.burgerparty;

public class DesktopAdController implements AdController {
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
