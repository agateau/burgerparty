package com.agateau.burgerparty.model;

/**
 * Handles rating or support
 */
public interface RatingController {
    String getActionTitle();

    String getActionDescription();

    void rate();
}
