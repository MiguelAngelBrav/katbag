/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.holoeverywhere.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagUtilities;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

public class OneWorld extends SherlockFragment {
	
	private Tracker tracker;

	static LinearLayout v = null;
	public MainActivity mainActivity;
	public long id_world = -1;
	public String name_world;
	public MenuItem menuItemColor, menuItemPhotoAlbum, menuItemTakePhoto;
	private ColorPicker pickerColor;
	private SVBar svBar;
	public List<String> world = new ArrayList<String>();
	public int colorBackground = -16750951;
	public LinearLayout select_color;
	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int RESULT_OK = -1;
	static final int SELECT_PICTURE = 2;
	private String mCurrentPhotoPath = "";
	private String mCurrentType = "";
	private String mOldPhotoPath = "";
	private String mOldType = "";
	public ImageView picture;
	public static final String APP_DIRECTORY_NAME = "katbag";
	public final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_DIRECTORY_NAME);

	public static final int WORLD_ARRAY_TYPE = 0;
	public static final int WORLD_ARRAY_SRC = 1;
	public static final int WORLD_ARRAY_SCALE_FACTOR = 2;

	public static final String WORLD_TYPE_COLOR = "color";
	public static final String WORLD_TYPE_CAMERA = "camera";
	public static final String WORLD_TYPE_LIBRARY = "library";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = (LinearLayout) inflater.inflate(R.layout.fragment_one_world, container, false);
		mainActivity = (MainActivity) super.getActivity();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		select_color = (LinearLayout) v.findViewById(R.id.one_world_select_color);
		picture = (ImageView) v.findViewById(R.id.one_world_picture);

		// rescues parameters
		Bundle bundle = getArguments();
		if (bundle != null) {
			id_world = bundle.getLong("id_world");
			name_world = bundle.getString("name_world");

			world.clear();
			world = mainActivity.katbagHandler.selectWorldTypeSrcAndScaleFactorWorldForId(id_world);

			mCurrentType = world.get(WORLD_ARRAY_TYPE);
			mOldType = mCurrentType;

			if (world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_COLOR)) {
				setColorBackground(Integer.parseInt(world.get(WORLD_ARRAY_SRC)));
			} else if (world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_CAMERA) || world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_LIBRARY)) {
				mCurrentPhotoPath = world.get(WORLD_ARRAY_SRC);
				setPic(world.get(WORLD_ARRAY_TYPE), Integer.parseInt(world.get(WORLD_ARRAY_SCALE_FACTOR)));
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.one_world, menu);
		menuItemColor = menu.findItem(R.id.one_world_dropdown_menu_color);
		menuItemPhotoAlbum = menu.findItem(R.id.one_world_dropdown_menu_photo_album);
		menuItemTakePhoto = menu.findItem(R.id.one_world_dropdown_menu_take_photo);

		menuItemColor.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				selectColor();
				return true;
			}
		});

		menuItemPhotoAlbum.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				selectSinglePintureIntent();
				return true;
			}
		});

		menuItemTakePhoto.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dispatchTakePictureIntent();
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	public void selectColor() {

		LayoutInflater inflater = LayoutInflater.from(mainActivity.context);
		final View dialog_layout = inflater.inflate(R.layout.dialog_picker_color, null);

		svBar = (SVBar) dialog_layout.findViewById(R.id.custom_valuebar);
		pickerColor = (ColorPicker) dialog_layout.findViewById(R.id.custom_picker_color);
		pickerColor.addSVBar(svBar);
		pickerColor.setOldCenterColor(colorBackground);
		pickerColor.setColor(colorBackground);

		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
		builder.setView(dialog_layout);
		builder.setTitle(getString(R.string.dialog_title_select_picker_color));

		builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// :)
			}
		});

		builder.setPositiveButton(getString(R.string.dialog_button_select), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mainActivity.katbagHandler.updateWorld(id_world, WORLD_TYPE_COLOR, String.valueOf(pickerColor.getColor()), -1);
				setColorBackground(pickerColor.getColor());
				picture.setImageResource(android.R.color.transparent);
				mOldType = mCurrentType;
				mCurrentType = WORLD_TYPE_COLOR;
				deleteImageFile(mCurrentPhotoPath);
			}
		});

		builder.show();
	}

	private void selectSinglePintureIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(Intent.createChooser(intent, getString(R.string.one_world_select_picture)), SELECT_PICTURE);
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(mainActivity.getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				mOldPhotoPath = mCurrentPhotoPath; // if take a new photo,
													// delete old
				photoFile = createImageFile();
			} catch (IOException ex) {
				Log.w("dispatchTakePictureIntent", "IOException: " + ex.getMessage());
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "katbagImage_" + timeStamp + "_";
		File storageDir = Environment.getExternalStorageDirectory();
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void deleteImageFile(String path) {
		if (!path.contentEquals("") && mOldType.contentEquals(WORLD_TYPE_CAMERA)) {
			File photoFile = new File(path);
			if (photoFile.exists()) {
				photoFile.delete();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_IMAGE_CAPTURE: // result of take a photo
			if (resultCode == RESULT_OK) {
				mOldType = mCurrentType;
				mCurrentType = WORLD_TYPE_CAMERA;

				deleteImageFile(mOldPhotoPath);

				File imgFile = new File(mCurrentPhotoPath);
				if (imgFile.exists()) {
					setPic(WORLD_TYPE_CAMERA, -1); // on setPic() save path to
													// database
				} else {
					Log.w("onActivityResult", "Exception: image does not exist");
				}
			}

			break;

		case SELECT_PICTURE: // result of select one picture
			if (resultCode == RESULT_OK) {
				Uri selectedImageUri = data.getData();
				mOldPhotoPath = mCurrentPhotoPath;
				mCurrentPhotoPath = getPath(selectedImageUri);
				mOldType = mCurrentType;
				mCurrentType = WORLD_TYPE_LIBRARY;
				deleteImageFile(mOldPhotoPath);

				if (!mCurrentPhotoPath.contentEquals("")) {
					if (!mCurrentPhotoPath.contains(APP_DIRECTORY_NAME))
						mCurrentPhotoPath = copyFile();

					setPic(WORLD_TYPE_LIBRARY, -1);
				} else {
					KatbagUtilities.message(mainActivity.context, getString(R.string.one_drawing_message_not_image_selected));
				}
			}

			break;

		default:
			break;
		}
	}

	public String getPath(Uri contentUri) {
		String filePath = "";
		String[] filePathColumn = { MediaColumns.DATA };
		Cursor cursor = mainActivity.context.getContentResolver().query(contentUri, filePathColumn, null, null, null);

		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			filePath = cursor.getString(columnIndex);
			cursor.close();
		}

		return filePath;
	}

	public String copyFile() {
		if (!imageRoot.exists())
			imageRoot.mkdir();

		int random = (int) Math.ceil(Math.random() * 100000000);
		String fname = Integer.toString(random);
		String mPathNewFile = imageRoot + "/" + fname + ".jpg";

		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(mCurrentPhotoPath);
			out = new FileOutputStream(mPathNewFile);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return mPathNewFile;
	}

	private void setPic(String type_world, int scaleFactor) {
		// Get the dimensions of the View
		int targetW = picture.getWidth();
		int targetH = picture.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		if (scaleFactor == -1) {
			if (photoW != 0 && targetW != 0 && photoH != 0 && targetH != 0)
				scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			else
				scaleFactor = 1;

			mainActivity.katbagHandler.updateWorld(id_world, type_world, mCurrentPhotoPath, scaleFactor);
		}

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();

			try {
				bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
				// handle gracefully.
			}
		}

		picture.setImageBitmap(bitmap);
	}

	public void setColorBackground(int color) {
		select_color.setBackgroundColor(color);
		colorBackground = color;
	}

	@Override
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(Add.name_app_text + " - " + getString(R.string.worlds_row_name) + " " + name_world);

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		System.gc();
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		System.gc();
		super.onStop();
	}
}
