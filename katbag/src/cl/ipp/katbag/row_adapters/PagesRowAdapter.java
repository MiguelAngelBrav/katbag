/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.row_adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.fragment.Pages;

public class PagesRowAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private Context context;
	private MainActivity mainActivity;

	public PagesRowAdapter(Context context, MainActivity mainActivity, int layoutResourceId, List<String> items) {
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
			v = vi.inflate(R.layout.row_pages, parent, false);
		}

		TextView id = (TextView) v.findViewById(R.id.page_row_id);
		if (id != null) {
			id.setText(items.get(position));
		}

		TextView name = (TextView) v.findViewById(R.id.page_row_name);
		if (name != null) {
			name.setText(context.getString(R.string.pages_row_name) + " " + position);
		}

		TextView subHumaName = (TextView) v.findViewById(R.id.page_row_sub_name);
		if (subHumaName != null) {
			subHumaName.setText("id:" + items.get(position));
		}

		ImageView arrow = (ImageView) v.findViewById(R.id.page_row_image_arrow);
		ImageView remove = (ImageView) v.findViewById(R.id.page_row_image_remove);
		ImageView drag = (ImageView) v.findViewById(R.id.page_row_image_drag);

		if (Pages.editMode) {
			arrow.setVisibility(View.GONE);
			remove.setVisibility(View.VISIBLE);
			drag.setVisibility(View.VISIBLE);
		} else {
			arrow.setVisibility(View.VISIBLE);
			remove.setVisibility(View.GONE);
			drag.setVisibility(View.GONE);
		}

		return v;
	}

	@Override
	public void insert(String object, int index) {
		List<String> list = new ArrayList<String>();

		list.clear();
		list = mainActivity.katbagHandler.selectPagesForIdApp(Pages.id_app);

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

			mainActivity.katbagHandler.updatePageOrder(Pages.id_app, Long.parseLong(arr[0]), order);
		}

		super.insert(object, index);
	}
}
