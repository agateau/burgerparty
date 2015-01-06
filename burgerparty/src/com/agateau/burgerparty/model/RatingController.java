package com.agateau.burgerparty.model;

/**
 * Handles rating
 */
public class RatingController {
    public interface Implementation {
        void rate();
    }

    private Implementation mImplementation;

    public void setImplementation(Implementation implementation) {
        mImplementation = implementation;
    }

    public void rate() {
        mImplementation.rate();
    }
}
