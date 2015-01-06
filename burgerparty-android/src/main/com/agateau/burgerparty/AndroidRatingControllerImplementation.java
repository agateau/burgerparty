package com.agateau.burgerparty;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.agateau.burgerparty.model.RatingController;
import com.agateau.burgerparty.utils.NLog;

public class AndroidRatingControllerImplementation implements RatingController.Implementation {
    private static final String APP_ID = "com.agateau.burgerparty";
    private final Activity mMainActivity;

    public AndroidRatingControllerImplementation(Activity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public void rate() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        for (String uri : StoreConstants.INTENT_BASE_URIS) {
            intent.setData(Uri.parse(uri + APP_ID));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                mMainActivity.startActivity(intent);
                return;
            } catch (ActivityNotFoundException e) {
                NLog.i("No activity for %s", uri);
            }
        }
        NLog.e("No store available for rating");
    }
}
