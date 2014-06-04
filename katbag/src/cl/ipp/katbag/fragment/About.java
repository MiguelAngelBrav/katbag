/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.fragment;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class About extends Fragment {

	private Tracker tracker;
	
	static View v = null;
	public MainActivity mainActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_about, container, false);

		return v;
	}

	@Override
	public void onResume() {
		String title = getString(R.string.about_version);
		try {
			title = getString(R.string.about_version) + mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mainActivity.getSupportActionBar().setTitle(title);

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}
}
