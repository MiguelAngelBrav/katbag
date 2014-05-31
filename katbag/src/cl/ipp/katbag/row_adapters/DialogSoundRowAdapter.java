/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.row_adapters;

import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cl.ipp.katbag.R;

public class DialogSoundRowAdapter extends ArrayAdapter<String> {

	private List<String> names;
	private List<String> items;
	private Context context;
	private MediaPlayer mPlayer;

	public DialogSoundRowAdapter(Context context, int layoutResourceId, List<String> items, List<String> names) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
		this.names = names;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_dialog_sound, parent, false);
		}

		TextView name = (TextView) v.findViewById(R.id.dialog_sound_row_name);
		if (name != null) {
			name.setText(Html.fromHtml(names.get(position)));
		}

		ImageView preview = (ImageView) v.findViewById(R.id.dialog_sound_preview);
		preview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int sound = context.getResources().getIdentifier(items.get(position), "raw", context.getPackageName());
				;
				stopPlayer();

				mPlayer = MediaPlayer.create(context, sound);
				mPlayer.start();
			}
		});

		return v;
	}

	public void stopPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}
}
