package cl.ipp.katbag.core;

import java.util.Locale;

import org.holoeverywhere.widget.Toast;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

public class KatbagUtilities {

	private static Toast toast = null;

	public static void message(final Context context, final String msg) {
		if (toast != null) {
			toast.cancel();
		}

		toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.show();
	}

	public static InputFilter katbagAlphaNumericFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence arg0, int arg1, int arg2, Spanned arg3, int arg4, int arg5) {
			for (int k = arg1; k < arg2; k++) {
				if (!Character.isLetterOrDigit(arg0.charAt(k)) && !Character.isSpaceChar(arg0.charAt(k))) {
					return "";
				}
			}
			return null;
		}
	};

	public static String capitalizeString(String s) {
		s = s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1, s.length()).toLowerCase(Locale.getDefault());
		return s;
	}

}
