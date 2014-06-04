/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagUtilities;
import cl.ipp.katbag.row_adapters.EditRowAdapter;

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
import com.mobeta.android.dslv.DragSortListView;

public class Edit extends SherlockFragment {
	
	private Tracker tracker;

	static View v = null;
	public DragSortListView editListView;
	public static MainActivity mainActivity;
	public TextView notRegister;
	public EditRowAdapter adapter;
	public static boolean editMode = false;
	public MenuItem menuItemEdit;
	public Fragment mFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_edit, container, false);
		mainActivity = (MainActivity) super.getActivity();

		notRegister = (TextView) v.findViewById(R.id.edit_not_register);
		editMode = false;
		loadListView();

		editListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!editMode) {
					TextView idApp = (TextView) view.findViewById(R.id.edit_row_id);
					TextView nameApp = (TextView) view.findViewById(R.id.edit_row_name);
					TextView typeApp = (TextView) view.findViewById(R.id.edit_row_type_app);

					// initialize parameters of add class
					Add.id_app = Long.valueOf(idApp.getText().toString());
					Add.name_app_text = nameApp.getText().toString();

					Bundle bundle = new Bundle();
					bundle.putString("type_app", typeApp.getText().toString());

					mFragment = new Add();
					mFragment.setArguments(bundle);
					FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
					t.replace(R.id.fragment_main_container, mFragment);
					t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					t.addToBackStack(mFragment.getClass().getSimpleName());
					t.commit();
				}
			}
		});

		// id exposes
		editListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView idApp = (TextView) arg1.findViewById(R.id.edit_row_id);
				KatbagUtilities.message(mainActivity.context, getString(R.string.edit_get_id) + " " + idApp.getText().toString());
				return false;
			}
		});

		return v;
	}

	public void loadListView() {
		editListView = (DragSortListView) v.findViewById(R.id.edit_list_view);
		List<String> idList = new ArrayList<String>();
		idList.clear();

		idList = mainActivity.katbagHandler.selectAllApps();

		if (idList.size() <= 0) {
			notRegister.setVisibility(View.VISIBLE);

		} else {
			notRegister.setVisibility(View.GONE);

			Parcelable state = editListView.onSaveInstanceState();
			adapter = new EditRowAdapter(v.getContext(), R.layout.row_edit, idList);

			editListView.setRemoveListener(onRemove);
			editListView.setAdapter(adapter);
			editListView.onRestoreInstanceState(state);
		}
	}

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			String item = (String) adapter.getItem(which);
			adapter.remove(item);

			String d[] = item.split("&&");

			mainActivity.katbagHandler.deleteAppForId(Long.parseLong(d[0]));

			adapter.notifyDataSetChanged();
			editListView.refreshDrawableState();
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.edit, menu);
		menuItemEdit = menu.findItem(R.id.edit_dropdown_menu_edit);

		menuItemEdit.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (editMode) {
					editMode = false;
					menuItemEdit.setIcon(R.drawable.ic_action_edit);
				} else {
					editMode = true;
					menuItemEdit.setIcon(R.drawable.ic_action_accept);
				}

				loadListView();

				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(R.string.title_activity_edit);

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}
}