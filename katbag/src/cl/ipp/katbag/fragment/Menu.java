package cl.ipp.katbag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cl.ipp.katbag.R;

public class Menu extends Fragment {

	public static View v = null;
	public Fragment mFragment;
	public LinearLayout board, add, edit, info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_menu, container, false);
		return v;
	}
}
