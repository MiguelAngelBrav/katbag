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
	private static final int DATABASE_VERSION = 4;
	
	public static final String TABLE_APPLICATIONS = "applications";
	public static final String FIELD_APP_NAME = "app_name";
	public static final String FIELD_APP_TYPE = "app_type";
	
	public static final String TABLE_WORLDS = "worlds";
	public static final String FIELD_WORLD_APP_ID = "world_app_id";
	public static final String FIELD_WORLD_TYPE = "world_type";
	public static final String FIELD_WORLD_SRC = "world_src";

	public KatbagHandlerSqlite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_APPLICATIONS + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FIELD_APP_NAME + " TEXT NOT NULL, " 
				+ FIELD_APP_TYPE + " TEXT NOT NULL " 
				+ ")";
		db.execSQL(query);
		
		query = "CREATE TABLE " + TABLE_WORLDS + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FIELD_WORLD_APP_ID + " INTEGER NOT NULL, " 
				+ FIELD_WORLD_TYPE + " TEXT NOT NULL, " 
				+ FIELD_WORLD_SRC + " TEXT NOT NULL " 
				+ ")";
		db.execSQL(query);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORLDS);
		onCreate(db);
	}
	
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
		String filter = "_ID = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_APP_NAME, name);
		this.getWritableDatabase().update(TABLE_APPLICATIONS, values, filter, null);
		this.close();
		
		Log.d("updateNameApp", "App: " + name + ", id: " + id + ", update!");
	}
	
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
		
		//352272
	}
	
	// update world
	public long updateWorld(long id, String type, String src) {
		String filter = "_ID = " + id;
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
		
		String query = "select _ID from " + TABLE_WORLDS + " where " + FIELD_WORLD_APP_ID + " = " + id_app;
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
		String query = "_ID = " + id;
		
		Log.d("updateWorld", "World id: " + id + ", delete!");
		
		return this.getWritableDatabase().delete(TABLE_WORLDS, query, null) > 0;
	}
}
