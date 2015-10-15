package com.admob.custom.InMobiInterstitial;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.InMobiInterstitial.InterstitialAdListener;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.InMobiSdk.Education;
import com.inmobi.sdk.InMobiSdk.Ethnicity;
import com.inmobi.sdk.InMobiSdk.Gender;
import com.inmobi.sdk.InMobiSdk.LogLevel;

public class InMobiInterstitialCustomEvent implements CustomEventInterstitial {
    private static String TAG = "InMobiInterCustomEvent";
    private InMobiInterstitial inmobiInterstitial;
    private CustomEventInterstitialListener interstitialListener;
    private static boolean isAppIntialize = false;
    private JSONObject serverParams;

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener listener, String serverParameter,
                                      MediationAdRequest mediationAdRequest, Bundle customEventExtras) {
        Log.e(TAG, "Request InMobi Interstitial ad");
        Activity activity = null;
        String accountId = "";
        long placementId = 0l;
        try {
            serverParams = new JSONObject(serverParameter);
            accountId = serverParams.getString("accountId");
            placementId = serverParams.getLong("placementId");
        } catch (Exception e) {
            Log.e(TAG, "Could not parse server parameters");
            e.printStackTrace();
        }
        this.interstitialListener = listener;

        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            // You may also pass in an Activity Context in the localExtras map
            // and retrieve it here.
        }
        if (activity == null) {
            interstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
            return;
        }

        if (!isAppIntialize) {
            InMobiSdk.setLogLevel(LogLevel.DEBUG);
            InMobiSdk.init(activity, accountId);
            isAppIntialize = true;
        }


        inmobiInterstitial = new InMobiInterstitial(activity, placementId, new InterstitialAdListener() {

            @Override
            public void onUserLeftApplication(InMobiInterstitial arg0) {
                interstitialListener.onAdLeftApplication();
            }

            @Override
            public void onAdRewardActionCompleted(InMobiInterstitial arg0,
                                                  Map<Object, Object> arg1) {
                Log.d(TAG, "InMobi interstitial onRewardActionCompleted.");

                Iterator<Object> iterator = arg1.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = arg1.get(key).toString();
                    Log.d("Rewards: ", key+":"+value);
                }
            }

            @Override
            public void onAdLoadSucceeded(InMobiInterstitial arg0) {
                interstitialListener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(InMobiInterstitial arg0,
                                       InMobiAdRequestStatus arg1) {
                switch (arg1.getStatusCode()) {
                    case INTERNAL_ERROR:
                        interstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;

                    case REQUEST_INVALID:
                        interstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                        break;

                    case NETWORK_UNREACHABLE:
                        interstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
                        break;

                    case NO_FILL:
                        interstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                        break;

                    default:
                        interstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;
                }
            }

            @Override
            public void onAdDisplayed(InMobiInterstitial arg0) {
                interstitialListener.onAdOpened();
            }

            @Override
            public void onAdDismissed(InMobiInterstitial arg0) {
                interstitialListener.onAdClosed();
            }

            @Override
            public void onAdInteraction(InMobiInterstitial arg0,
                                        Map<Object, Object> arg1) {
                interstitialListener.onAdClicked();
            }
        });


        Map<String, String> map = new HashMap<String, String>();
        map.put("tp", "c_admob_ce");
        inmobiInterstitial.setExtras(map);
        inmobiInterstitial.setKeywords("keywords");
        inmobiInterstitial.load();
    }

    @Override
    public void showInterstitial() {
        if(inmobiInterstitial.isReady()) {
            inmobiInterstitial.show();
        }
    }

}

