package cl.ipp.katbag.core;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KatbagHandlerSqlite extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "katbag_db.sqlite3";
	private static final int DATABASE_VERSION = 6;
	
	public static final String TABLE_APPLICATIONS = "applications";
	public static final String FIELD_APP_ID = "_ID";
	public static final String FIELD_APP_NAME = "app_name";
	public static final String FIELD_APP_TYPE = "app_type";
	
	public static final String TABLE_WORLDS = "worlds";
	public static final String FIELD_WORLD_ID = "_ID";
	public static final String FIELD_WORLD_APP_ID = "world_app_id";
	public static final String FIELD_WORLD_TYPE = "world_type";
	public static final String FIELD_WORLD_SRC = "world_src";
	
	public static final String TABLE_DRAWINGS = "drawings";
	public static final String FIELD_DRAWING_ID = "_ID";
	public static final String FIELD_DRAWING_APP_ID = "drawing_app_id";
	public static final String FIELD_DRAWING_SRC = "drawing_src";
	
	public static final int SCORE_FOR_HAVE_WORLDS = 20;
	public static final int SCORE_FOR_HAVE_DRAWINGS = 20;
	public static final int SCORE_FOR_HAVE_DEVELOPMENTS = 40;

	// ==================================================================================
	//                                      GLOBAL
	// ==================================================================================
	
	public KatbagHandlerSqlite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_APPLICATIONS + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FIELD_APP_NAME + " TEXT NOT NULL, " 
				+ FIELD_APP_TYPE + " TEXT NOT NULL " 
				+ ");";
		db.execSQL(query);
		
		query = "CREATE TABLE " + TABLE_WORLDS + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FIELD_WORLD_APP_ID + " INTEGER NOT NULL, " 
				+ FIELD_WORLD_TYPE + " TEXT NOT NULL, " 
				+ FIELD_WORLD_SRC + " TEXT NOT NULL, " 
				+ "FOREIGN KEY(" + FIELD_WORLD_APP_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + FIELD_APP_ID + ") ON DELETE CASCADE"
				+ ");";
		db.execSQL(query);
		
		query = "CREATE TABLE " + TABLE_DRAWINGS + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FIELD_DRAWING_APP_ID + " INTEGER NOT NULL, " 
				+ FIELD_DRAWING_SRC + " TEXT NOT NULL, " 
				+ "FOREIGN KEY(" + FIELD_DRAWING_APP_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + FIELD_APP_ID + ") ON DELETE CASCADE"
				+ ");";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORLDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWINGS);
		onCreate(db);
	}
	
	// ==================================================================================
	//                                      GENERIC
	// ==================================================================================
	
	public int estimatedProgress(String id_app) {
		Cursor cursor;
		String query;
		int result = 0;
		
		// have a world?
		query = "select " + FIELD_WORLD_ID + " from " + TABLE_WORLDS + " where " + FIELD_WORLD_APP_ID + " = " + id_app;
		cursor = this.getReadableDatabase().rawQuery(query, null);
		
		if (cursor.getCount() > 0) result = result + SCORE_FOR_HAVE_WORLDS;
		cursor.close();
		
		// have a drawing?
		query = "select " + FIELD_DRAWING_ID + " from " + TABLE_DRAWINGS + " where " + FIELD_DRAWING_APP_ID + " = " + id_app;
		cursor = this.getReadableDatabase().rawQuery(query, null);
		
		if (cursor.getCount() > 0) result = result + SCORE_FOR_HAVE_DRAWINGS;
		cursor.close();
		
		// have a development?
//		query = "select " + FIELD_WORLD_ID + " from " + TABLE_WORLDS + " where " + FIELD_WORLD_APP_ID + " = " + id_app;
//		cursor = this.getReadableDatabase().rawQuery(query, null);
//		
//		if (cursor.getCount() > 0) result = result + SCORE_FOR_HAVE_DEVELOPMENTS;
//		cursor.close();
		
		return result;
	}
	
	// ==================================================================================
	//                                      APP
	// ==================================================================================
	
	// insert new app
	public long insertApp(String name, String type) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_APP_NAME, name);
		values.put(FIELD_APP_TYPE, type);
		id = this.getWritableDatabase().insert(TABLE_APPLICATIONS, null, values);	
		this.close();
		
		Log.d("insertApp", "App: " + name + ", id: " + id + ", save!");
		
		return id;
	}
	
	// update name app
	public void updateNameApp(long id, String name) {
		String filter = FIELD_APP_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_APP_NAME, name);
		this.getWritableDatabase().update(TABLE_APPLICATIONS, values, filter, null);
		this.close();
		
		Log.d("updateNameApp", "App: " + name + ", id: " + id + ", update!");
	}
	
	// select all apps
	public List<String> selectAllApps() {
		List<String> results = new ArrayList<String>();
		results.clear();
		
		String query = "select " + FIELD_APP_ID + ", " + FIELD_APP_NAME + ", " + FIELD_APP_TYPE + " from " + TABLE_APPLICATIONS + " order by " + FIELD_APP_ID + " desc ";
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0) + "&&" + cursor.getString(1) + "&&" + cursor.getString(2));
			} while (cursor.moveToNext());
			cursor.close();
		}		
		
		return results;
	}
	
	// delete app for id
	public boolean deleteAppForId(String id) {
		String query = FIELD_APP_ID + " = " + id;
		
		Log.d("delete", "App id: " + id + ", delete!");
		
		return this.getWritableDatabase().delete(TABLE_APPLICATIONS, query, null) > 0;
	}
	
	// ==================================================================================
	//                                      WORLD
	// ==================================================================================
	
	// insert new world
	public long insertWorld(long id_app, String type, String src) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_WORLD_APP_ID, id_app);
		values.put(FIELD_WORLD_TYPE, type);
		values.put(FIELD_WORLD_SRC, src);
		id = this.getWritableDatabase().insert(TABLE_WORLDS, null, values);	
		this.close();
		
		Log.d("insertWorld", "Type World: " + type + ", id: " + id + ", save!");
		
		return id;
	}
	
	// update world
	public long updateWorld(long id, String type, String src) {
		String filter = FIELD_WORLD_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_WORLD_TYPE, type);
		values.put(FIELD_WORLD_SRC, src);
		this.getWritableDatabase().update(TABLE_WORLDS, values, filter, null);	
		this.close();
		
		Log.d("updateWorld", "Type World: " + type + ", src: " + src + ", id: " + id + ", update!");
		
		return id;
	}
	
	// select worlds
	public List<String> selectWorldsForIdApp(long id_app) {
		List<String> results = new ArrayList<String>();
		results.clear();
		
		String query = "select " + FIELD_WORLD_ID + " from " + TABLE_WORLDS + " where " + FIELD_WORLD_APP_ID + " = " + id_app;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0));
			} while (cursor.moveToNext());
			cursor.close();
		}		
		
		return results;
	}
	
	// delete world for id
	public boolean deleteWorldForId(String id) {
		String query = FIELD_WORLD_ID + " = " + id;
		
		Log.d("delete", "World id: " + id + ", delete!");
		
		return this.getWritableDatabase().delete(TABLE_WORLDS, query, null) > 0;
	}
	
	// ==================================================================================
	//                                      DRAWING
	// ==================================================================================
	
	// insert new drawing
	public long insertDrawing(long id_app, String src) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_APP_ID, id_app);
		values.put(FIELD_DRAWING_SRC, src);
		id = this.getWritableDatabase().insert(TABLE_DRAWINGS, null, values);	
		this.close();
		
		Log.d("insertDrawing", "Drawing id: " + id + ", save!");
		
		return id;
	}
	
	// update drawing
	public long updateWorld(long id, String src) {
		String filter = FIELD_DRAWING_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_SRC, src);
		this.getWritableDatabase().update(TABLE_DRAWINGS, values, filter, null);	
		this.close();
		
		Log.d("updateDrawing", "Drawing src: " + src + ", id: " + id + ", update!");
		
		return id;
	}
	
	// select drawig
	public List<String> selectDrawingsForIdApp(long id_app) {
		List<String> results = new ArrayList<String>();
		results.clear();
		
		String query = "select " + FIELD_DRAWING_ID + " from " + TABLE_DRAWINGS + " where " + FIELD_DRAWING_APP_ID + " = " + id_app;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0));
			} while (cursor.moveToNext());
			cursor.close();
		}		
		
		return results;
	}
	
	// delete drawing for id
	public boolean deleteDrawingForId(String id) {
		String query = FIELD_DRAWING_ID + " = " + id;
		
		Log.d("delete", "Drawing id: " + id + ", delete!");
		
		return this.getWritableDatabase().delete(TABLE_DRAWINGS, query, null) > 0;
	}
}
