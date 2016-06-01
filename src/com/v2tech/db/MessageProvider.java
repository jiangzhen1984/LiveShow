package com.v2tech.db;

import com.v2tech.db.MessageDescriptor.P2PMessage;
import com.v2tech.db.MessageDescriptor.P2PMessageItem;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MessageProvider extends ContentProvider {

	private MessageDBHelper helper;

	public MessageProvider() {
		super();
	}

	@Override
	public boolean onCreate() {
		if (helper == null) {
			if (getContext() != null) {
				helper = new MessageDBHelper(getContext());
			}
		}
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (helper == null) {

		}
		int token = MessageDescriptor.URI_MATCHER.match(uri);
		String tableName = null;
		switch (token) {
		case MessageDescriptor.SystemMessage.TOKEN:
			tableName = MessageDescriptor.SystemMessage.TABLE_NAME;
			break;
		case MessageDescriptor.SystemMessage.TOKEN_WITH_ID:
			tableName = MessageDescriptor.SystemMessage.TABLE_NAME;
			selection = P2PMessage.Cols.ID +"  = ?";
			selectionArgs = new String[] { uri.getLastPathSegment() };
			break;
		case MessageDescriptor.P2PMessage.TOKEN:
			throw new RuntimeException(
					" Does not support token, use:  TOKEN_WITH_ID");
		case MessageDescriptor.P2PMessage.TOKEN_WITH_ID:
			tableName = MessageDescriptor.P2PMessage.TABLE_NAME;
			selection = P2PMessage.Cols.ID +" = ?";
			selectionArgs = new String[] { uri.getLastPathSegment() };
			break;
		case MessageDescriptor.P2PMessage.TOKEN_WITH_USER_ID:
			tableName = MessageDescriptor.P2PMessage.TABLE_NAME;
			selection = MessageDescriptor.P2PMessage.Cols.FROM_USER + "=? or  "
					+ MessageDescriptor.P2PMessage.Cols.TO_USER + "=?";
			selectionArgs = new String[] { uri.getLastPathSegment(),
					uri.getLastPathSegment() };
			break;

		case MessageDescriptor.P2PMessageItem.TOKEN_WITH_MASTER_ID:
			tableName = MessageDescriptor.P2PMessageItem.TABLE_NAME;
			selection = P2PMessageItem.Cols.MASTER_ID + "  = ?";
			selectionArgs = new String[] { uri.getLastPathSegment() };
			break;
		default:
			throw new RuntimeException(" Does not support token" + uri);

		}
		Cursor cur = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		cur = db.query(tableName, projection, selection, selectionArgs, null,
				null, sortOrder);
		return cur;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (helper == null) {
			helper = new MessageDBHelper(getContext());
		}

		int token = MessageDescriptor.URI_MATCHER.match(uri);
		long id = 0;
		String name = null;
		switch (token) {
		case MessageDescriptor.SystemMessage.TOKEN:
			name = MessageDescriptor.SystemMessage.TABLE_NAME;
			break;
		case MessageDescriptor.P2PMessageItem.TOKEN_WITH_MASTER_ID:
			name = MessageDescriptor.P2PMessageItem.TABLE_NAME;
			values.put(MessageDescriptor.P2PMessageItem.Cols.MASTER_ID,
					uri.getLastPathSegment());
			break;
		case MessageDescriptor.P2PMessage.TOKEN_WITH_USER_ID:
			name = MessageDescriptor.P2PMessage.TABLE_NAME;
			break;
		default:
			throw new RuntimeException(" Does not support token: " + uri);
		}

		SQLiteDatabase db = helper.getWritableDatabase();
		id = db.insert(name, null, values);
		return uri.buildUpon().appendEncodedPath(id + "").build();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (helper == null) {
			helper = new MessageDBHelper(getContext());
		}

		int token = MessageDescriptor.URI_MATCHER.match(uri);
		String tableName = null;
		switch (token) {
		case MessageDescriptor.P2PMessage.TOKEN_WITH_ID:
			tableName = MessageDescriptor.P2PMessage.TABLE_NAME;
			selection =  P2PMessage.Cols.ID + "  = ?";
			selectionArgs = new String[]{uri.getLastPathSegment()};
			break;
		default:
			throw new RuntimeException(" Does not support token: " + uri);
		}

		SQLiteDatabase db = helper.getWritableDatabase();
		return db.update(tableName, values, selection, selectionArgs);
	}
}
