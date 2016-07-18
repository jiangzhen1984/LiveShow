package com.v2tech.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDBHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Message.db";

	public MessageDBHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, null);
	}

	public MessageDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public MessageDBHelper(Context context) { 
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MessageDescriptor.SystemMessage.getCreateSql());
		db.execSQL(MessageDescriptor.P2PMessage.getCreateSql());
		db.execSQL(MessageDescriptor.P2PMessageItem.getCreateSql());
		db.execSQL(MessageDescriptor.MessageSession.getCreateSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	
	
	
	
}
