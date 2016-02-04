package com.digitalturbine.dtpaysdkdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalturbine.dtpaysdk.appia.sdk.AdParameters;
import com.digitalturbine.dtpaysdk.appia.sdk.Appia;
import com.digitalturbine.dtpaysdk.appia.sdk.AppiaAccessor;
import com.digitalturbine.dtpaysdk.appia.sdk.AppiaLogger;
import com.digitalturbine.dtpaysdk.appia.sdk.BannerAdSize;
import com.digitalturbine.dtpaysdk.appia.sdk.UserData;
import com.digitalturbine.dtpaysdk.dtpay.DTPayClient;

public class AppiaActivity extends AppCompatActivity {

    TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        super.onCreate(savedInstanceState);



        //Initialize the SDK
        configureAppia();


        setContentView(R.layout.activity_appia);
        text1 = (TextView) findViewById(R.id.text1);
        text1.setText("...");

        // app being opened for the first time. if savedInstanceState is not null, we've likely
        // had a screen orientation change.
        if (savedInstanceState == null)
            appia.startSession();
    }


    public void showPopupBannerAd(View view){
        Toast.makeText(this, "Show banner ads", Toast.LENGTH_LONG).show();

        BannerAdSize iSize = getBannerSize();

        AppiaLogger.i(TAG, String.format(
                "Showing interstitial with attributes [width=%d height=%d adTypeId=%d]",
                iSize.getWidth(),
                iSize.getHeight(),
                iSize.getAdTypeId()));

        Appia appia = Appia.getAppia(this);
        appia.displayInterstitial(this, new AdParameters(), iSize);
    }

    public void showPopupAppWall(View view){
        Toast.makeText(this,"Show popup appwall",Toast.LENGTH_LONG).show();

        updateAppWallSetup();
        Appia.getAppia(this).displayWall(this,new AdParameters(), Appia.WallDisplayType.POPUP);
    }

    public void showFullscreenAppWall(View view)
    {
        Toast.makeText(this,"Show fullscreen appwall",Toast.LENGTH_LONG).show();

        updateAppWallSetup();
        Appia.getAppia(this).displayWall(this, new AdParameters(), Appia.WallDisplayType.FULL_SCREEN);
    }

    public void showEmbeddedAppWall(View view)
    {
        updateAppWallSetup();

        Intent i = null;


        i = new Intent(this, EmbeddedAppWall.class);

        startActivity(i);
    }

    public void showEmbeddedBanner(View view)
    {
        BannerAdSize iSize = getBannerSize();

        Intent i = null;

        i = new Intent(this, EmbeddedBannerAd.class);

        startActivity(i);
    }

    public void sendUserData(View view)
    {
        UserData userData = makeUserData();
        Appia.getAppia(this).sendUserData(this, userData);
    }

    public void sendEvent(View v) {
        String event = "event 1";
        String result = Appia.getAppia(this).trackEvent(event);
        text1.setText(result);
    }

    private UserData makeUserData() {
        UserData res = new UserData();

        //test data
        res.getSex().setValue(UserData.Sex.MALE);
        res.getAge().setValue(25);
        res.getChildren().setNumberOfChildren(2);
        res.getEducation().setValue(UserData.Education.MASTERS);
        res.getIncome().setValue(50000);
        res.getKeywords().setValueUnknown(true);
        res.getMaritalStatus().setValue(UserData.MaritalStatus.DIVORCED);
        res.getPolicitalAffiliation().setValue(UserData.PoliticalAffiliation.DEMOCRAT);

        res.getZipCode().setValueUnknown(true);


        return res;
    }

    private void configureAppia()
    {
        AppiaAccessor.setContext(this);

        SDK_DATA_USER_DATA_DEFAULT_URL = AppiaAccessor.getSdkDataUserDataUrl();
        SDK_DATA_USER_EVENT_DEFAULT_URL = AppiaAccessor.getSdkDataUserEventUrl();
        ADSERVER_DEFAULT_URL = AppiaAccessor.getAdServerUrl();
        APPWALL_DEFAULT_URL = AppiaAccessor.getAppWallUrl();


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        String siteIdDefault = this.getString(R.string.configure_app_wall_siteid_new);
        int siteId = settings.getInt("siteId", Integer.parseInt(siteIdDefault));

        AppiaLogger.i(TAG, "Configure Appia: SiteId = " + siteId);

        String appWallUrl =
                settings.getString("app_wall_url", AppiaAccessor.getAppWallUrl());

        String adServerUrl =
                settings.getString("ad_server_url", AppiaAccessor.getAdServerUrl());

        String sdkDataUserDataUrl =
                settings.getString("sdk_data_user_data_url", AppiaAccessor.getSdkDataUserDataUrl());

        String sdkDataUserEventUrl =
                settings.getString("sdk_data_user_event_url", AppiaAccessor.getSdkDataUserEventUrl());

        boolean indicatorDef = Boolean.parseBoolean(this.getString(R.string.cache_cache_indicator_toggle_default));
        boolean indicator = settings.getBoolean("cache_enable_indicator", indicatorDef);

        boolean trackOpensDef = Boolean.parseBoolean(this.getString(R.string.cache_track_opens_toggle_default));
        boolean trackOpens = settings.getBoolean("cache_track_opens", trackOpensDef);

        appia = Appia.getAppia(this);

        appia.setSiteId(siteId);

        AppiaAccessor.configAdServerUrl(adServerUrl);
        AppiaAccessor.configAppWallUrl(appWallUrl);
        AppiaAccessor.configSdkDataUserDataUrl(sdkDataUserDataUrl);
        AppiaAccessor.configSdkDataUserEventUrl(sdkDataUserEventUrl);
        AppiaAccessor.enableCacheIndicator(indicator); 	//To assist QA
        AppiaAccessor.enableTrackOpens(trackOpens);
    }

    private void updateAppWallSetup()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean hwAccelDef = Boolean.parseBoolean(this.getString(R.string.configure_app_wall_enable_hw_accel_default));
        boolean hwAccel = settings.getBoolean("app_wall_enable_hw_accel", hwAccelDef);

        Appia appia = Appia.getAppia(this);
        appia.setHardwareAcceleratedWall(hwAccel);
    }


    private BannerAdSize getBannerSize()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int bannerSize = settings.getInt("banner_sizing", 6);

        return BannerAdSize.values()[bannerSize];
    }



    private static final String TAG = "appia.app";
    public final static String EXTRA_PARCELABLE = "com.appia.app.pojo.AppWallConfig";
    public final static String GET_ADS_REQUEST_EXTRA = "com.appia.app.GET_ADS_REQUEST_EXTRA";
    public final static String GET_BANNER_SCALING_EXTRA = "com.appia.app.GET_BANNER_SCALING_EXTRA";
    public final static String GET_BANNER_SIZING_EXTRA = "com.appia.app.GET_BANNER_SIZING_EXTRA";
    public final static String USER_DATA_SEND_URL_EXTRA = "com.appia.app.USER_DATA_SEND_URL_EXTRA";
    public final static String USER_EVENT_SEND_URL_EXTRA = "com.appia.app.USER_EVENT_SEND_URL_EXTRA";

    public static final String PREFS_NAME = "AppWallPrefsFile";
    public static final String BANNER_SIZES_PREFS_NAME = "BannerSizesPrefsFile";

    protected static Appia appia;

    //Save URLs as initially loaded by the underlying SDK before we override
    private static String SDK_DATA_USER_DATA_DEFAULT_URL;
    private static String SDK_DATA_USER_EVENT_DEFAULT_URL;
    private static String ADSERVER_DEFAULT_URL;
    private static String APPWALL_DEFAULT_URL;

}
