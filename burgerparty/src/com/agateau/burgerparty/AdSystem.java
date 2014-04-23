package com.agateau.burgerparty;

public interface AdSystem {
    void preloadAd();
    boolean isAdAvailable();
    void showAd(Runnable after);
}
