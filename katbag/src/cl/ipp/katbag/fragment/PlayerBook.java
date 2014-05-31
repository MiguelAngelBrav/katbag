/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.UnderlinePageIndicator;

public class PlayerBook extends SherlockFragment {

	private Tracker tracker;
	
	static LinearLayout v = null;
	public static MainActivity mainActivity;
	public long id_app = -1;
	public String name_app_text = "";
	public String type_app = "";
	public int numPages = 0;
	public MenuItem menuItemPrevious, menuItemNext;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private UnderlinePageIndicator mIndicator;

	public ArrayList<String> page = new ArrayList<String>();
	private MediaPlayer mPlayer;
	public static final int PAGE_SOUND_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = (LinearLayout) inflater.inflate(R.layout.fragment_player_book, container, false);
		mainActivity = (MainActivity) super.getActivity();

		// rescues parameters
		Bundle bundle = getArguments();
		if (bundle != null) {
			id_app = bundle.getLong("id_app");
			name_app_text = bundle.getString("name_app");
		}

		numPages = mainActivity.katbagHandler.countPagesForIdApp(id_app);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) v.findViewById(R.id.pager);
		// mPager.setPageTransformer(true, new DepthPageTransformer());
		mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		mIndicator = (UnderlinePageIndicator) v.findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setFades(false);

		setPageSound(0);

		mIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setPageSound(position);
			}
		});

		return v;
	}

	public void setPageSound(int pageNumber) {
		stopPlayer();
		page.clear();
		page = mainActivity.katbagHandler.selectOnePageForIdAndOrder(id_app, pageNumber);

		if (page.size() > 0) {
			if (page.get(PAGE_SOUND_ID) != null) {
				playSound(page.get(PAGE_SOUND_ID));
			}
		}
	}

	public void playSound(String identifier) {
		if (!identifier.contentEquals("")) {
			int sound = mainActivity.getResources().getIdentifier(identifier, "raw", mainActivity.getPackageName());
			stopPlayer();
			mPlayer = MediaPlayer.create(mainActivity.context, sound);
			mPlayer.start();
		}
	}

	public void stopPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.player_book, menu);

		menuItemPrevious = menu.findItem(R.id.player_book_previous_item);
		menuItemNext = menu.findItem(R.id.player_book_next_item);

		menuItemPrevious.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
				return true;
			}
		});

		menuItemNext.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mPager.setCurrentItem(mPager.getCurrentItem() + 1);
				return false;
			}
		});
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return SlidePageBookFragment.create(id_app, position);
		}

		@Override
		public int getCount() {
			return numPages;
		}
	}

	@Override
	public void onResume() {
		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		mainActivity.getSupportActionBar().setTitle(name_app_text);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}

	@Override
	public void onPause() {
		stopPlayer();
		System.gc();
		super.onPause();
	}

	@Override
	public void onStop() {
		stopPlayer();
		System.gc();
		super.onStop();
	}
}
