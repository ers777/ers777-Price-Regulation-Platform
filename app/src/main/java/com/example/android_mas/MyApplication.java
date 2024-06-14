package com.example.android_mas;

import android.app.Application;
import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51POF7HAGMczK0Nk2qRccHkZuFzah9gBEhtkh1JndSpWuIu2kkvPCvYUTrzqjxcpl9NiFkXhnWvMV1Tw8OLqsx2pG00NmtKXIgC"
        );
    }
}
