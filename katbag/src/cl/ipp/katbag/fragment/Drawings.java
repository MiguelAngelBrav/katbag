package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.row_adapters.DrawingsRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.mobeta.android.dslv.DragSortListView;

public class Drawings extends SherlockFragment {
	
	static View v = null;
	public static MainActivity mainActivity;
	public MenuItem menuItemEdit, menuItemNew; 
	public static long id_app = -1;
	public TextView notRegister;
	public DragSortListView drawingsListView;
	public DrawingsRowAdapter adapter;
	public static boolean editMode = false;
	public Fragment mFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_drawings, container, false);
		mainActivity.supportInvalidateOptionsMenu();
		
		// rescues parameters
		Bundle bundle = getArguments();
        if(bundle != null){
    		id_app = bundle.getLong("id_app");
        }
        
        notRegister = (TextView) v.findViewById(R.id.drawings_not_register); 
        loadListView();
        
        drawingsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!editMode) {
					TextView idDrawing = (TextView) view.findViewById(R.id.drawing_row_id);
									
					Bundle bundle = new Bundle();
					bundle.putLong("id_drawing", Long.valueOf(idDrawing.getText().toString()));
					bundle.putString("name_drawing", idDrawing.getText().toString());
									
					mFragment = new OneDrawing();
					mFragment.setArguments(bundle);
					FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
					t.replace(R.id.fragment_main_container, mFragment);
					t.addToBackStack(mFragment.getClass().getSimpleName());
					t.commit();
				}
			}
		});
        
		return v;
	}
	
	public void loadListView() {	
		drawingsListView = (DragSortListView) v.findViewById(R.id.drawings_list_view);
		List<String> idList = new ArrayList<String>();
		idList.clear();
		
		idList = mainActivity.katbagHandler.selectDrawingsForIdApp(id_app);
			
		if (idList.size() <= 0) {
			notRegister.setVisibility(View.VISIBLE);
			
		} else {
			notRegister.setVisibility(View.GONE);

			adapter = new DrawingsRowAdapter(
					v.getContext(), 
					R.layout.row_drawings, 
					idList);
			
			drawingsListView.setRemoveListener(onRemove);
			drawingsListView.setAdapter(adapter);	
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.drawing, menu);
		menuItemNew = menu.findItem(R.id.drawings_dropdown_menu_new);
		menuItemEdit = menu.findItem(R.id.drawings_dropdown_menu_edit);
		
		menuItemEdit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				editMode();
				if (!editMode) {
					loadListView();
				}
				return true;
			}
		});
		
		menuItemNew.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				editMode = false;
				menuItemEdit.setIcon(R.drawable.ic_action_edit);
				mainActivity.katbagHandler.insertDrawing(id_app);
				loadListView();
				return true;
			}
		});
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	public final void editMode() {
		Log.d("editMode", "editMode: " + editMode);
		
		View vc;
		ImageView arrow;
		ImageView remove;
		
		if (editMode) {
			editMode = false;
			menuItemEdit.setIcon(R.drawable.ic_action_edit);
		} else {
			editMode = true;
			menuItemEdit.setIcon(R.drawable.ic_action_accept);
		}
			
		if (drawingsListView.getChildCount() == 0) {
			loadListView();
			
		} else {
			for (int i = 0; i < drawingsListView.getChildCount(); i++) {
				vc = (View) drawingsListView.getChildAt(i);
				arrow = (ImageView) vc.findViewById(R.id.drawing_row_image_arrow);
				remove = (ImageView) vc.findViewById(R.id.drawing_row_image_remove);
				
				if (editMode) {
					arrow.setVisibility(View.GONE);
					remove.setVisibility(View.VISIBLE);
				} else {
					arrow.setVisibility(View.VISIBLE);
					remove.setVisibility(View.GONE);
				}
			}	
		}
	}
		
	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			Log.d("remove", "remove which:" + which);
			
			String item = (String) adapter.getItem(which);
			adapter.remove(item);	
			mainActivity.katbagHandler.deleteDrawingForId(item);
			
			adapter.notifyDataSetChanged();
			drawingsListView.refreshDrawableState();
		}
	};
		
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_drawings);
	}
}
