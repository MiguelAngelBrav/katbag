package cl.ipp.katbag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class Player extends Fragment {
	
	static View v = null;
	public static MainActivity mainActivity;
	public RelativeLayout type_app_game, type_app_book, type_app_comic;
	public Fragment mFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_player, container, false);
		mainActivity = (MainActivity) super.getActivity();
		
		return v;
	}

}
