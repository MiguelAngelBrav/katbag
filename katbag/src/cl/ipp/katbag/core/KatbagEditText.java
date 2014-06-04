/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.core;

import org.holoeverywhere.widget.EditText;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.RelativeLayout;
import cl.ipp.katbag.R;

public class KatbagEditText extends EditText {
	
	public Context context;
	
	public KatbagEditText(Context context) {
		super(context);
		this.setTextAppearance(context, R.style.katbag_edittext);
		this.context = context;
		Typeface face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.one_page_default_font));
		this.setTypeface(face);
	}
	
	public void setTextAlign(String align) {
		if (align.contentEquals("left"))
			this.setGravity(Gravity.TOP | Gravity.LEFT);
		else if (align.contentEquals("center"))
			this.setGravity(Gravity.TOP | Gravity.CENTER);
		else if (align.contentEquals("right"))
			this.setGravity(Gravity.TOP | Gravity.RIGHT);
	}
	
	public void setFontSize(int size) {
		this.setTextSize(size);
	}
	
	public void setColorText(int color) {
		this.setTextColor(color);
	}
	
	public void moveToXY(int left, int top) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
		lp.leftMargin = left;
		lp.topMargin = top;
		this.setLayoutParams(lp);
	}
	
	
	
}
