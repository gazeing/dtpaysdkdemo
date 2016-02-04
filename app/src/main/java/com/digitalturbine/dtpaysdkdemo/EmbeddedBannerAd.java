package com.digitalturbine.dtpaysdkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitalturbine.dtpaysdk.appia.sdk.AdParameters;
import com.digitalturbine.dtpaysdk.appia.sdk.Appia;
import com.digitalturbine.dtpaysdk.appia.sdk.AppiaAdView;
import com.digitalturbine.dtpaysdk.appia.sdk.BannerAdResult;
import com.digitalturbine.dtpaysdk.appia.sdk.BannerAdSize;
import com.digitalturbine.dtpaysdk.appia.sdk.WallResult;

public class EmbeddedBannerAd extends Activity {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static BannerAdResult mBannerAdResult;
	private LinearLayout webViewPlaceholder;
	
	private static boolean mIsShown = false;
	int lastViewClickedID = 0;
	
	public static boolean getIsShown() {
		return mIsShown;
	}
	
	public  static void setIsShown(boolean isShown) {
		mIsShown = isShown;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (getIsShown() && getBannerAdResult() != null) {
			getBannerAdResult().didDisappear();
			setIsShown(false);
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_embedded_banner_ad);
		
		initUI();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			lastViewClickedID = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			if (lastViewClickedID > 0)
				loadScreen(lastViewClickedID);	
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, lastViewClickedID);
	}

	
	private void initUI()
	{
		webViewPlaceholder = (LinearLayout)findViewById(R.id.placeholder);
		
		LinearLayout.LayoutParams wvLayoutParams =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        wvLayoutParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        wvLayoutParams.weight = 1;
        wvLayoutParams.setMargins(0, 0, 0, 0);
        
        webViewPlaceholder.addView(
        		getSimpleTextView("Use the buttons to change pages..."), 
        		wvLayoutParams);		       
	}
	
	public void doImageButtonClick(View view)
	{
		finish();
	}
	
	public void doButtonClick(View view) {
		loadScreen(view.getId());
	}
	
	public void loadScreen(int id)
	{
		lastViewClickedID = id;
		webViewPlaceholder.removeViewAt(0);
		View v = null;
		
		switch (id)
		{
			case R.id.buttonNew: 		v = getSimpleTextView("New page"); break;
			case R.id.buttonEdit: 		v = getSimpleTextView("Edit page"); break;
			case R.id.buttonAbout: 		v = getSimpleTextView("About page"); break;
			case R.id.buttonProfile: 	v = getSimpleTextView("Profile page"); break;
			case R.id.buttonChat: 		v = getSimpleTextView("Chat page"); break;
			case R.id.buttonMoreApps: 	
			{
				//get the banner ad here so ads are refreshed
				v = getBannerAd(this);
				
				getBannerAdResult().willAppear();
				setIsShown(true);
				
				break;
			}
			default:
				if (getIsShown() && getBannerAdResult() != null) {
					getBannerAdResult().didDisappear();
					setIsShown(false);
				}

				v = getSimpleTextView("Some unidentifed page"); break;
		}

		webViewPlaceholder.addView(v);
	}
	
	private View getSimpleTextView(String text)
	{
		TextView textView = new TextView(this);
		textView.setGravity(Gravity.CENTER);
		textView.setText(text);
		
		return textView;
	}
	
	public static View getBannerAd(Activity activity)
	{	
		View bannerAdView = null;
		
	   	//Get existing settings from file
	    SharedPreferences settings = activity.getSharedPreferences(AppiaActivity.PREFS_NAME, 0);
	    int bannerSize = settings.getInt("banner_sizing", 0);
		BannerAdSize bannerAdSize = BannerAdSize.values()[bannerSize];
		
		//Appia.getAppia(activity).cacheBannerAd(activity, ConfigureAdParametersActivity.adParameters, bannerAdSize);

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
	
	public static BannerAdResult getBannerAdResult() {
		return mBannerAdResult;
	}
	
	private static String getFailedBannerHtml()
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		b.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		b.append("<tr><td><div style=\"text-align: center\">");
		b.append("<font face=\"verdana\" color=\"grey\" size=\"2\">Unable to load ad.</font>");
		b.append("</div></td></tr>");
		b.append("</table>");
		b.append("</html>");
		
		return b.toString().replace("%", "%25");
	}
	
}
