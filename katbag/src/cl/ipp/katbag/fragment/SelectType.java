package cl.ipp.katbag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

public class SelectType extends Fragment implements OnClickListener {
	
	static View v = null;
	public static MainActivity mainActivity;
	public RelativeLayout type_app_game, type_app_book, type_app_comic;
	public Fragment mFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_select_type, container, false);
		mainActivity = (MainActivity) super.getActivity();
		
		type_app_game = (RelativeLayout) v.findViewById(R.id.type_app_game);
		type_app_book = (RelativeLayout) v.findViewById(R.id.type_app_book);
		type_app_comic = (RelativeLayout) v.findViewById(R.id.type_app_comic);
		
		type_app_game.setOnClickListener(this);
		type_app_book.setOnClickListener(this);
		type_app_comic.setOnClickListener(this);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		String type_app = "";
		mainActivity.hideSoftKeyboard();
		
		switch (v.getId()) {
		case R.id.type_app_game:
			type_app = "game";
			break;

		case R.id.type_app_book:
			type_app = "book";
			break;
			
		case R.id.type_app_comic:
			type_app = "comic";
			break;
		}
				
		Bundle bundle = new Bundle();
		bundle.putString("type_app", type_app);
		
		// initialize parameters of add class
		Add.id_app = -1;
		Add.name_app_text = "";  
		
		mFragment = new Add();
		mFragment.setArguments(bundle);
		FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		t.commit();			
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    mainActivity.getSupportActionBar().setTitle(R.string.title_activity_select_type);
	}	
}
