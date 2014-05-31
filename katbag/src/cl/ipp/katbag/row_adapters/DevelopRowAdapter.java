/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.row_adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.fragment.Develop;

public class DevelopRowAdapter extends ArrayAdapter<String> {

	public List<String> items;
	private Context context;
	private MainActivity mainActivity;
	private ArrayList<String> dialogList = new ArrayList<String>();

	public DevelopRowAdapter(Context context, MainActivity mainActivity, int layoutResourceId, List<String> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.mainActivity = mainActivity;
		this.items = items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_develop, parent, false);
		}

		List<String> dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(items.get(position)));
		if (dev.size() > 0) {
			TextView humanName = (TextView) v.findViewById(R.id.develop_row_name);
			if (humanName != null) {
				humanName.setText(dev.get(1));
			}

			TextView subHumaName = (TextView) v.findViewById(R.id.develop_row_sub_name);
			if (subHumaName != null) {
				subHumaName.setText("(" + dev.get(7) + ") - pos:" + position + " - id:" + items.get(position));
			}
		}

		ImageView arrow = (ImageView) v.findViewById(R.id.develop_row_image_arrow);
		ImageView remove = (ImageView) v.findViewById(R.id.develop_row_image_remove);
		ImageView drag = (ImageView) v.findViewById(R.id.develop_row_image_drag);

		if (Develop.editMode) {
			arrow.setVisibility(View.GONE);
			remove.setVisibility(View.VISIBLE);
			drag.setVisibility(View.VISIBLE);
		} else {
			arrow.setVisibility(View.VISIBLE);
			remove.setVisibility(View.GONE);
			drag.setVisibility(View.GONE);
		}

		ImageView indent_line = (ImageView) v.findViewById(R.id.develop_row_image_indent_line);
		if (canIndent(dev.get(0), dev.get(2), position)) {
			indent_line.setBackgroundResource(R.drawable.katbag_icon_one_develop_indent);
		} else {
			indent_line.setBackgroundResource(R.drawable.katbag_icon_one_develop);
		}

		// determine color line level
		int level = Integer.parseInt(dev.get(7));
		int levelColor = 0;
		if ((level >= 0) && (level <= 9))
			levelColor = level;
		else if ((level >= 10) && (level <= 19))
			levelColor = level - 10;
		else if ((level >= 20) && (level <= 29))
			levelColor = level - 20;
		else if ((level >= 30) && (level <= 39))
			levelColor = level - 30;
		else if ((level >= 40) && (level <= 49))
			levelColor = level - 40;
		else if ((level >= 50) && (level <= 59))
			levelColor = level - 50;
		else if ((level >= 60) && (level <= 69))
			levelColor = level - 60;
		else if ((level >= 70) && (level <= 79))
			levelColor = level - 70;
		else if ((level >= 80) && (level <= 89))
			levelColor = level - 80;
		else if ((level >= 90) && (level <= 99))
			levelColor = level - 90;

		LinearLayout lineLevel = (LinearLayout) v.findViewById(R.id.develop_line_level);
		int resource = context.getResources().getIdentifier("develop_line_level_" + String.valueOf(levelColor), "color", mainActivity.getPackageName());
		lineLevel.setBackgroundResource(resource);

		lineLevel.setPadding(level * 13, 0, 0, 0);

		return v;
	}

	@Override
	public void insert(String object, int index) {
		List<String> list = new ArrayList<String>();

		list.clear();
		list = mainActivity.katbagHandler.selectDevelopForIdApp(Develop.id_app);

		int z = 0;
		int order = -1;
		for (int i = 0; i < list.size(); i++) {
			String[] arr = list.get(i).toString().split("&&");

			if (arr[0].contentEquals(object)) {
				order = index;
			} else {
				if (z == index)
					z++;
				order = z;
				z++;
			}

			mainActivity.katbagHandler.updateDevelopOrder(Long.parseLong(arr[0]), order);
		}

		super.insert(object, index);
	}

	public boolean canIndent(String statement, String value, int position) {
		dialogList.clear();
		if (statement.contentEquals("motion")) {

		} else if (statement.contentEquals("look")) {

		} else if (statement.contentEquals("sound")) {

		} else if (statement.contentEquals("control")) {
			dialogList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.control_indent)));

		} else if (statement.contentEquals("sensing")) {
			dialogList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.sensing_indent)));
		}

		boolean indent = false;
		if (dialogList.size() > 0) {
			for (int i = 0; i < dialogList.size(); i++) {
				if (value.contentEquals(dialogList.get(i))) {
					indent = true;
					break;
				}
			}
		}

		return indent;
	}

}
