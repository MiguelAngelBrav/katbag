package cl.ipp.katbag.core;

import static android.provider.BaseColumns._ID;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KatbagHandlerSqlite extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "katbag_db.sqlite3";
	private static final int DATABASE_VERSION = 2;
	
	public static final String TABLE_APPLICATIONS = "applications";
	public static final String FIELD_NAME_APP = "name_app";
	public static final String FIELD_TYPE_APP = "type_app";

	public KatbagHandlerSqlite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_APPLICATIONS + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FIELD_NAME_APP + " TEXT NOT NULL, " 
				+ FIELD_TYPE_APP + " TEXT NOT NULL " 
				+ ")";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String query = "DROP TABLE IF EXISTS " + TABLE_APPLICATIONS;
		db.execSQL(query);
		onCreate(db);
	}
	
	public long insertApp(String name_app, String type_app) {
		long id = 0;
		ContentValues values = new ContentValues();
		values.put(FIELD_NAME_APP, name_app);
		values.put(FIELD_TYPE_APP, type_app);
		id = this.getWritableDatabase().insert(TABLE_APPLICATIONS, null, values);	
		Log.d("setOnEditorActionListener", "App: " + name_app + ", id: " + id + ", save!");
		this.close();
		
		return id;
	}
	
	public void updateNameApp(long id, String name_app) {
		String filter = "_ID = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_NAME_APP, name_app);
		this.getWritableDatabase().update(TABLE_APPLICATIONS, values, filter, null);
		this.close();
	}
}
