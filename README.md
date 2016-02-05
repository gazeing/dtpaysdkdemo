# DTpay SDK user guide
#####The step-by-step guide for integrating Appia and DTpay features to you app 

##DTpay integration guide
####Firstly, add library to your project 
- Download dtpay.aar file from our website.
- Add it to your project via Android studio File|New|New Module...|Import .JAR/.AAR Package
- Add dependencies to your main module:
```
dependencies {
 compile project(':SDKLibrary-release')
// for library dependency
 compile 'com.android.support:appcompat-v7:23.1.0'
 compile 'com.google.android.gms:play-services:8.3.0'
 compile 'com.google.code.gson:gson:2.4'
 compile 'com.squareup.retrofit:retrofit:2.0.0-beta2'
 compile 'com.squareup.retrofit:converter-gson:2.0.0-beta2'
 compile 'com.squareup.okhttp:logging-interceptor:2+'
}
```

#####Alternatively, you can add one line to your dependencies:

`compile 'DTPaySDK:SDKLibrary:0.9.9'`
 
####After that:
#####Declare a dtpay client:
 `DTPayClient client;`

#####You have to initialise your client before use it:
```
 private void initDTPay() {
      client = new DTPayClientBuilder(this)
              .setDTPayResponseListner(this)
              .build();
  }
```
#####DTPayResponseListner will be required to get notification from api, you also can set your own UI and viewmodel via the following methonds of DTPayClientBuilder:  

 ```
 public DTPayClientBuilder setPresenter(DTPayPrestenter presenter)
 public DTPayClientBuilder setViewModel(DTPayViewModel vm)
 ```
If they are not called, default UI and viewmodel will be used.
 
![Alt text](/snap/1.png?raw=true "default UI")
![Alt text](/snap/2.png?raw=true "default UI")
![Alt text](/snap/3.png?raw=true "default UI")

 
#####Then, you can use the following methods to make purchases and check their status:
```
 client.markPurchase(client.getVirtualGoods("Magic Sword", 792));//the magic number "792" here actually is the tariff id. 
                                                                 //it shows the price of item
                                                                 //you can find it from price list
 client.checkBillingStatus(reference);
```
[price list](http://pay.fortumo.com/mobile_payments/654669bc7608ac16fc53861bda88c12b.xml)

You will set a `DTPayResponseListner` here to get result:
```
public class DTPayActivity extends AppCompatActivity implements DTPayResponseListner {
...
  @Override
    public void OnPurchaseResponse(PurchaseResponse response) {
        ...//do you logic
    }

    @Override
    public void OnBillingResponse(BillingStatusResponse response) {
        ...//do you logic
    }

    @Override
    public void OnError(String Error) {
       ...//do you error logic
    }


```
`PurchaseResponse` response code meaning:
```
    public enum ResponseCode
    {
        Ok,
        Error,
        AuthenticationFailed
    }
```

`BillingStatusResponse` response code meaning:
```
    public enum SuccessStatus
    {
        Success,
        Failed,
        NotFound,
        Pending
    }

```

##Appia integration guide
First of all, add library aar file to your project (same as DtPay).

Then, using Appia client to show Appwall and banner ad:
Configure Appia client:
```
AppiaAccessor.setContext(this);
SDK_DATA_USER_DATA_DEFAULT_URL = AppiaAccessor.getSdkDataUserDataUrl();
SDK_DATA_USER_EVENT_DEFAULT_URL = AppiaAccessor.getSdkDataUserEventUrl();
ADSERVER_DEFAULT_URL = AppiaAccessor.getAdServerUrl();
APPWALL_DEFAULT_URL = AppiaAccessor.getAppWallUrl();

SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
String siteIdDefault = this.getString(R.string.configure_app_wall_siteid_new);
int siteId = settings.getInt("siteId", Integer.parseInt(siteIdDefault));

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
AppiaAccessor.enableCacheIndicator(indicator); //To assist QA
AppiaAccessor.enableTrackOpens(trackOpens);
```
 
Show popup Appwall:
```
updateAppWallSetup();
Appia.getAppia(this).displayWall(this,new AdParameters(), Appia.WallDisplayType.POPUP);
``` 
Show Fullscreen Appwall:
```
updateAppWallSetup();
Appia.getAppia(this).displayWall(this, new AdParameters(), Appia.WallDisplayType.FULL_SCREEN);
```
Show popup banner ad:
```
BannerAdSize iSize = getBannerSize();
Appia appia = Appia.getAppia(this);
appia.displayInterstitial(this, new AdParameters(), iSize);
``` 
 
Show embedded Appwall:
```
public static View getAppWall(Activity activity)
{
  View appWall = null;
  WallResult wr = Appia.getAppia(activity).getWall(activity, new AdParameters());
 if (wr.hasError())
  {
      AppiaAdView adView = new AppiaAdView(activity);
  WebView wv = adView.getWebView();
  wv.loadData(getFailedAppWallHtml(), "text/html", null);
  appWall = adView;
  }
  else appWall = wr.getView();
 return appWall;
}
``` 
Show embedded banner ad:
```
public static View getBannerAd(Activity activity)
{  
   View bannerAdView = null;
 
 //Get existing settings from file
 SharedPreferences settings = activity.getSharedPreferences(AppiaActivity.PREFS_NAME, 0);
 int bannerSize = settings.getInt("banner_sizing", 0);
 BannerAdSize bannerAdSize = BannerAdSize.values()[bannerSize];

 mBannerAdResult = Appia.getAppia(activity).getBannerAd(activity, new AdParameters(), bannerAdSize);
 
 if (mBannerAdResult.hasError())
 {
    AppiaAdView adView = new AppiaAdView(activity);
  WebView wv = adView.getWebView();
  wv.loadData(getFailedBannerHtml(), "text/html", null);
  bannerAdView = adView;
 }
 else
  bannerAdView = mBannerAdResult.getView();
 
 return bannerAdView;
}
```
