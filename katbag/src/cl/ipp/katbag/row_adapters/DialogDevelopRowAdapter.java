/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.row_adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cl.ipp.katbag.R;

public class DialogDevelopRowAdapter extends ArrayAdapter<String> {

	private List<String> names;
	private Context context;

	public DialogDevelopRowAdapter(Context context, int layoutResourceId, List<String> items, List<String> names) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.names = names;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_dialog_develop, parent, false);
		}

		TextView name = (TextView) v.findViewById(R.id.dialog_develop_row_name);
		if (name != null) {
			name.setText(Html.fromHtml(names.get(position)));
		}

		return v;
	}
}
