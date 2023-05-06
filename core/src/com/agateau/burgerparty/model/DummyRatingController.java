package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.NLog;

public class DummyRatingController implements RatingController {
    @Override
    public void rate() {
        NLog.i("Show rate dialog");
    }
}
