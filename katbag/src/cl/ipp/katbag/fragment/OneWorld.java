package cl.ipp.katbag.fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.holoeverywhere.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

public class OneWorld extends SherlockFragment {
	
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
		if(bundle != null){
			id_world = bundle.getLong("id_world");
			name_world = bundle.getString("name_world");
			
			world.clear();
			world = mainActivity.katbagHandler.selectTypeSrcAndScaleFactorWorldForId(id_world);
			
			Log.i("getArguments", "type: " + world.get(WORLD_ARRAY_TYPE) + " - src: " + world.get(WORLD_ARRAY_SRC) + " - scale_factor: " + world.get(WORLD_ARRAY_SCALE_FACTOR));
			
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
		builder.setTitle(getString(R.string.one_world_picker_color_title));
		
		builder.setNegativeButton(getString(R.string.one_world_picker_color_button_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	// :)
            }
        });
		
		builder.setPositiveButton(getString(R.string.one_world_picker_color_button_select), new DialogInterface.OnClickListener() {
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
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
	}
	
	private void dispatchTakePictureIntent() { 
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(mainActivity.getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	        	mOldPhotoPath = mCurrentPhotoPath; // if take a new photo, delete old
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
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	private void deleteImageFile(String path) {
		if (!path.contentEquals("") && mOldType.contentEquals(WORLD_TYPE_CAMERA)) {
    		File photoFile = new  File(path);
	    	if(photoFile.exists()){
	    		photoFile.delete();
	    		Log.i("deleteImageFile", "image old delete: " + path);
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
		    	
		    	File imgFile = new  File(mCurrentPhotoPath);
		    	if(imgFile.exists()){
		    	    setPic(WORLD_TYPE_CAMERA, -1); // on setPic() save path to database
		    	} else {
		    		Log.w("onActivityResult", "Exception: image does not exist");
		    	}
			}
			
			break;
			
		case SELECT_PICTURE: // result of select one picture
			if (resultCode == RESULT_OK) {
				Uri selectedImageUri = data.getData();
				
				mOldPhotoPath = mCurrentPhotoPath;
				mCurrentPhotoPath = getPath(mainActivity.context,selectedImageUri);
                
				mOldType = mCurrentType;
                mCurrentType = WORLD_TYPE_LIBRARY;
                
                deleteImageFile(mOldPhotoPath);
                
                setPic(WORLD_TYPE_LIBRARY, -1);
			}
			break;

		default:
			break;
		}
	}
	
	public String getPath(Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
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
	    	scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	    	mainActivity.katbagHandler.updateWorld(id_world, type_world, mCurrentPhotoPath, scaleFactor);
		}
	    
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    
	    picture.setImageBitmap(bitmap);
	}
	
	public void setColorBackground(int color) {
		select_color.setBackgroundColor(color);
		colorBackground = color;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.worlds_row_name) + " " +  name_world);
	}
}
