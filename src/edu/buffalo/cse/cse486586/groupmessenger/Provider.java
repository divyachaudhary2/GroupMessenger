package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class Provider extends ContentProvider {
	private GroupMessengerDatabase md;
	SQLiteDatabase db;
	private static final String AUTHORITY = "content://edu.buffalo.cse.cse486586.groupmessenger.provider";
	private static final String BASE_PATH = GroupMessengerDatabase.MessegesTable;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);


	public boolean onCreate(){
		
		md=new GroupMessengerDatabase(getContext(),GroupMessengerDatabase.dbName,null,GroupMessengerDatabase.version);
		db=md.getWritableDatabase();
		db.delete(BASE_PATH, null, null);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String key,String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(GroupMessengerDatabase.MessegesTable);

		Cursor cursor = queryBuilder.query(md.getReadableDatabase(),
				projection, GroupMessengerDatabase.key+"=?", new String[]{key}, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		db=md.getWritableDatabase();
		db.replace(BASE_PATH,null,values);
		db.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return null;
	}


	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
