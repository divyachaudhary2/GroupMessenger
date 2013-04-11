package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupMessengerDatabase extends SQLiteOpenHelper{

	static final String dbName="MessegesDB";
	public static final String MessegesTable="Messeges";
	public static final String key="key";
	public static final String value="value";
	static int version=1;
	public GroupMessengerDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, dbName, null,33); 
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+MessegesTable+" ("+key+ " TEXT PRIMARY KEY , "+
				value+ " TEXT)");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+MessegesTable);
		onCreate(db);
	}


	public void readMessege(){
	}
}
