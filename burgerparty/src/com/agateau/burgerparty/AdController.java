package com.agateau.burgerparty;

public interface AdController {
    void preloadAd();
    boolean isAdAvailable();
    void showAd(Runnable after);
}
