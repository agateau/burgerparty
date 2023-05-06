package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.PlatformUtils;

import static com.greenyetilab.linguaj.Translator.tr;

/** RatingController opening my support page */
public class SupportRatingController implements RatingController {
    private static final String SUPPORT_URL = "https://agateau.com/support";

    @Override
    public String getActionTitle() {
        return tr("Support Burger Party");
    }

    @Override
    public String getActionDescription() {
        return tr("Like the game? Support my work!");
    }

    @Override
    public void rate() {
        NLog.i("Opening " + SUPPORT_URL);
        PlatformUtils.openURI(SUPPORT_URL);
    }
}
