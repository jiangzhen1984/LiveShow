package com.v2tech.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MessageProvider extends ContentProvider {

	private MessageDBHelper helper;

	public MessageProvider() {
		super();
		helper = new MessageDBHelper(getContext());
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int token = MessageDescriptor.URI_MATCHER.match(uri);
		SQLiteDatabase db  = helper.getReadableDatabase();
		Cursor cur = null;
		switch (token) {
		case MessageDescriptor.SystemMessage.TOKEN:
			cur =  db.query(MessageDescriptor.SystemMessage.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case MessageDescriptor.SystemMessage.TOKEN_WITH_ID:
			cur = db.query(MessageDescriptor.SystemMessage.TABLE_NAME, projection, " id ", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
			break;
		}
		
		db.close();
		
		return cur;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db  = helper.getWritableDatabase();
		int token = MessageDescriptor.URI_MATCHER.match(uri);
		long id = 0;
		switch (token) {
		case MessageDescriptor.SystemMessage.TOKEN:
			id = db.insert(MessageDescriptor.SystemMessage.TABLE_NAME, null, values);
			break;
		}
		db.close();
		return uri.buildUpon().appendEncodedPath(id+"").build();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
