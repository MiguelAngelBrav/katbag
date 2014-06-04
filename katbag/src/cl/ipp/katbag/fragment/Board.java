/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.row_adapters.BoardRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mobeta.android.dslv.DragSortListView;

public class Board extends SherlockFragment {
	
	private Tracker tracker;

	static View v = null;
	public DragSortListView boardListView;
	public static MainActivity mainActivity;
	public TextView notRegister;
	public BoardRowAdapter adapter;
	public Fragment mFragment;
	public List<String> idList = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_board, container, false);
		mainActivity = (MainActivity) super.getActivity();

		notRegister = (TextView) v.findViewById(R.id.board_not_register);

		boardListView = (DragSortListView) v.findViewById(R.id.board_list_view);
		boardListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView idApp = (TextView) view.findViewById(R.id.board_row_id);
				TextView typeApp = (TextView) view.findViewById(R.id.board_row_type);
				TextView nameApp = (TextView) view.findViewById(R.id.board_row_name);

				if (typeApp.getText().toString().contentEquals(MainActivity.TYPE_APP_GAME))
					mFragment = new Player();
				else
					mFragment = new PlayerBook();

				Bundle bundle = new Bundle();
				bundle.putLong("id_app", Long.parseLong(idApp.getText().toString()));
				bundle.putString("name_app", nameApp.getText().toString());
				bundle.putString("type_app", typeApp.getText().toString());
				bundle.putBoolean("editMode", false);

				mFragment.setArguments(bundle);
				FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
				t.replace(R.id.fragment_main_container, mFragment);
				t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				t.addToBackStack(mFragment.getClass().getSimpleName());
				t.commit();
			}
		});

		return v;
	}

	public void loadListView() {
		idList.clear();

		idList = mainActivity.katbagHandler.selectAllApps();

		if (idList.size() <= 0) {
			notRegister.setVisibility(View.VISIBLE);

		} else {
			notRegister.setVisibility(View.GONE);

			adapter = new BoardRowAdapter(v.getContext(), R.layout.row_board, idList);

			boardListView.setAdapter(adapter);
		}
	}

	@Override
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(R.string.title_activity_board);
		loadListView();

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}
}