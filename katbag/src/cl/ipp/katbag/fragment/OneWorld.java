package cl.ipp.katbag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

public class OneWorld extends Fragment {
	
	static View v = null;
	public MainActivity mainActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_select_type, container, false);
		mainActivity = (MainActivity) super.getActivity();
		
		return v;
	}
}
