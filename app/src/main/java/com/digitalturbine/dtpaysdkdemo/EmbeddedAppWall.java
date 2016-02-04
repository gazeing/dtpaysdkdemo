package com.digitalturbine.dtpaysdkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.digitalturbine.dtpaysdk.appia.sdk.WallResult;

public class EmbeddedAppWall extends Activity {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private LinearLayout webViewPlaceholder;
	private View appWall;
	int lastViewClickedID = 0;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_embedded_app_wall);
		
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

	public void doButtonClick(View view)
	{
		loadScreen(view.getId());
	}
	
	public void loadScreen(int id) {
		webViewPlaceholder.removeViewAt(0);
		lastViewClickedID = id;
		
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
				//get the AppWall here so ads are refreshed
				v = getAppWall(this);
				break;
			}
			default:
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
        else
        	appWall = wr.getView();
		
        return appWall;
	}
	
	private static String getFailedAppWallHtml()
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		b.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		b.append("<tr><td><div style=\"text-align: center\">");
		b.append("<font face=\"verdana\" color=\"grey\" size=\"2\">Unable to load more applications.</font>");
		b.append("</div></td></tr>");
		b.append("</table>");
		b.append("</html>");
		
		return b.toString().replace("%", "%25");
	}
	
}
