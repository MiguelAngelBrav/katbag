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
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.row_adapters.PagesRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mobeta.android.dslv.DragSortListView;

public class Pages extends SherlockFragment {

	static View v = null;
	public static MainActivity mainActivity;
	public MenuItem menuItemEdit, menuItemNew;
	public static long id_app = -1;
	public TextView notRegister;
	public DragSortListView pagesListView;
	public PagesRowAdapter adapter;
	public static boolean editMode = false;
	public Fragment mFragment;
	public static final String DEFAULT_COLOR = "-16750951";
	private ArrayList<String> develop = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_pages, container, false);
		mainActivity.supportInvalidateOptionsMenu();

		// rescues parameters
		Bundle bundle = getArguments();
		if (bundle != null) {
			id_app = bundle.getLong("id_app");
		}

		notRegister = (TextView) v.findViewById(R.id.page_not_register);
		editMode = false;
		loadListView();

		pagesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!editMode) {
					TextView pageld = (TextView) view.findViewById(R.id.page_row_id);

					Bundle bundle = new Bundle();
					bundle.putLong("id_app", id_app);
					bundle.putLong("id_page", Long.valueOf(pageld.getText().toString()));
					bundle.putString("name_page", String.valueOf(position));

					mFragment = new OnePage();
					mFragment.setArguments(bundle);
					FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
					t.replace(R.id.fragment_main_container, mFragment);
					t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					t.addToBackStack(mFragment.getClass().getSimpleName());
					t.commit();
				}
			}
		});

		return v;
	}

	public void loadListView() {
		pagesListView = (DragSortListView) v.findViewById(R.id.pages_list_view);
		List<String> idList = new ArrayList<String>();
		idList.clear();

		idList = mainActivity.katbagHandler.selectPagesForIdApp(id_app);

		if (idList.size() <= 0) {
			notRegister.setVisibility(View.VISIBLE);

		} else {
			notRegister.setVisibility(View.GONE);

			Parcelable state = pagesListView.onSaveInstanceState();
			adapter = new PagesRowAdapter(v.getContext(), mainActivity, R.layout.row_pages, idList);

			pagesListView.setAdapter(adapter);
			pagesListView.setRemoveListener(onRemove);
			pagesListView.setDragScrollProfile(ssProfile);
			pagesListView.setDropListener(onDrop);
			pagesListView.onRestoreInstanceState(state);
		}
	}

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {

		@Override
		public void remove(int which) {
			String item = (String) adapter.getItem(which);
			adapter.remove(item);
			mainActivity.katbagHandler.deletePageForId(id_app, Long.parseLong(item));

			develop.clear();
			develop = mainActivity.katbagHandler.selectDevelopBookForIdAppAndPageId(id_app, Long.parseLong(item));
			for (int i = 0; i < develop.size(); i++) {
				String[] line = develop.get(i).split("&&");
				mainActivity.katbagHandler.deleteDevelopForId(Long.parseLong(line[0]));
			}

			adapter.notifyDataSetChanged();
			pagesListView.refreshDrawableState();
		}
	};

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {

				String item = (String) adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);

				// reIndent();

				adapter.notifyDataSetChanged();
				pagesListView.refreshDrawableState();
			}
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) pagesListView.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.pages, menu);
		menuItemNew = menu.findItem(R.id.pages_dropdown_menu_new);
		menuItemEdit = menu.findItem(R.id.pages_dropdown_menu_edit);

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

		menuItemNew.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				editMode = false;
				menuItemEdit.setIcon(R.drawable.ic_action_edit);
				mainActivity.katbagHandler.insertPage(id_app, "", 0);
				loadListView();
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(Add.name_app_text + " - " + getString(R.string.title_activity_pages));

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		super.onResume();
	}
}
