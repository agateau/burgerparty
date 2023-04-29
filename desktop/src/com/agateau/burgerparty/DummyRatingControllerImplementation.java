package com.agateau.burgerparty;

import com.agateau.burgerparty.model.RatingController;
import com.agateau.burgerparty.utils.NLog;

public class DummyRatingControllerImplementation implements RatingController.Implementation {
    @Override
    public void rate() {
        NLog.i("Show rate dialog");
    }
}
