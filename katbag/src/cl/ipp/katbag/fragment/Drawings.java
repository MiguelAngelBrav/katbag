package cl.ipp.katbag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

public class Drawings extends Fragment {
	
	static View v = null;
	public MainActivity mainActivity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_drawings, container, false);
		
		return v;
	}
}

