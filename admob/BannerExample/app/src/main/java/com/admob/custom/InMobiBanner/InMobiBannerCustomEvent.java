package com.admob.custom.InMobiBanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.google.android.gms.example.bannerexample.R;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiBanner.BannerAdListener;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.InMobiSdk.LogLevel;

public class InMobiBannerCustomEvent implements CustomEventBanner {
    private static String TAG = "InMobiBannerCustomEvent";
    private static boolean isAppIntialize = false;
    private InMobiBanner inmobiAdView;
    private CustomEventBannerListener bannerListener;
    private JSONObject serverParams;
    private Activity activity = null;
    private RelativeLayout adContainer = null;
    private RelativeLayout.LayoutParams params= null;
    /**
     * The event is being destroyed. Perform any necessary cleanup here.
     */
    @Override
    public void onDestroy() {

    }

    /**
     * The app is being paused. This call will only be forwarded to the adapter if the developer
     * notifies mediation that the app is being paused.
     */
    @Override
    public void onPause() {
        // The sample ad network doesn't have an onPause method, so it does nothing.
    }

    /**
     * The app is being resumed. This call will only be forwarded to the adapter if the developer
     * notifies mediation that the app is being resumed.
     */
    @Override
    public void onResume() {
        // The sample ad network doesn't have an onResume method, so it does nothing.
    }

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener listener,
                                String serverParameter,
                                final AdSize size,
                                MediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {
        String accountId = "";
        long placementId = 0L;

        Log.e(TAG, "Request InMobi Banner ads");
        try {
            serverParams = new JSONObject(serverParameter);
            placementId = serverParams.getLong("placementId");
            accountId = serverParams.getString("accountId");
        } catch (Exception e) {
            Log.e(TAG, "Could not parse server parameters");
            Log.e(TAG, serverParameter + " : " + e.toString());
            e.printStackTrace();
        }
        this.bannerListener = listener;

        if (context instanceof Activity) {
            activity = (Activity) context;
            adContainer = (RelativeLayout) activity.findViewById(R.id.ad_container);
        } else {
            // You may also pass in an Activity Context in the localExtras map
            // and retrieve it here.
        }
        if (activity == null) {
            bannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
            return;
        }

        if (!isAppIntialize) {
            InMobiSdk.init(activity, accountId);
            isAppIntialize = true;
        }

        InMobiSdk.setLogLevel(LogLevel.DEBUG);
        inmobiAdView = new InMobiBanner(activity, placementId);
        inmobiAdView.setEnableAutoRefresh(false);
        params = createBottomHorizontalLayoutParam(size);

        Map<String, String> map = new HashMap<String, String>();
        map.put("tp", "c_admob_ce");
        inmobiAdView.setExtras(map);
        inmobiAdView.setKeywords("keywords");
        inmobiAdView.setListener(new BannerAdListener() {

            @Override
            public void onUserLeftApplication(InMobiBanner arg0) {
                bannerListener.onAdLeftApplication();
            }

            @Override
            public void onAdRewardActionCompleted(InMobiBanner arg0,
                                                  Map<Object, Object> arg1) {
                Log.d(TAG, "InMobi Banner onRewardActionCompleted.");

                Iterator<Object> iterator = arg1.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = arg1.get(key).toString();
                    Log.d("Rewards: ", key + ":" + value);
                }
            }

            @Override
            public void onAdLoadSucceeded(InMobiBanner adView) {

                Log.d(TAG, "InMobi ad loaded Success");

                // We need to call removeAllViews() from adview's parent,
                // which would eventually handle on bannerListner
                adContainer.removeAllViews();
                RelativeLayout relative = new RelativeLayout(activity);
                relative.addView(adView, params);
                bannerListener.onAdLoaded(relative);
            }

            @Override
            public void onAdLoadFailed(InMobiBanner arg0, final InMobiAdRequestStatus arg1) {
                Log.e(TAG, "InMobi ad Failed!" + arg1.getMessage());
                Log.e(TAG, arg1.getMessage());
                switch (arg1.getStatusCode()) {
                    case INTERNAL_ERROR:
                        bannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;

                    case REQUEST_INVALID:
                        bannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                        break;

                    case NETWORK_UNREACHABLE:
                        bannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
                        break;

                    case NO_FILL:
                        bannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                        break;

                    default:
                        bannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;
                }
            }

            @Override
            public void onAdDisplayed(InMobiBanner arg0) {
                bannerListener.onAdOpened();
            }

            @Override
            public void onAdDismissed(InMobiBanner arg0) {
                bannerListener.onAdClosed();
            }

            @Override
            public void onAdInteraction(InMobiBanner arg0,
                                        Map<Object, Object> arg1) {
                bannerListener.onAdClicked();
            }
        });

        adContainer.addView(inmobiAdView, params);
        inmobiAdView.load();
    }

    private int toPixelUnits(int dipUnit) {
        if(activity!=null) {
            float density = activity.getResources().getDisplayMetrics().density;
            return Math.round(dipUnit * density);
        }
        return 0;
    }

    private RelativeLayout.LayoutParams createBottomHorizontalLayoutParam(AdSize size){
        int width = toPixelUnits(size.getWidth());
        int height = toPixelUnits(size.getHeight());

        RelativeLayout.LayoutParams bannerLayoutParams =
                new RelativeLayout.LayoutParams(width, height);
        bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        return bannerLayoutParams;
    }
}