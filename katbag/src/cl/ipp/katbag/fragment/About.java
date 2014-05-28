package cl.ipp.katbag.fragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

public class About extends Fragment {

	static View v = null;
	public MainActivity mainActivity;

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
	}
}
