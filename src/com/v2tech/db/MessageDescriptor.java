package com.v2tech.db;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class MessageDescriptor {

	public static final String AUTHORITY = "com.v2tech.liveshow";

	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String augura = AUTHORITY;

		matcher.addURI(augura, P2PMessage.PATH, P2PMessage.TOKEN);
		matcher.addURI(augura, P2PMessage.PATH + "/#", P2PMessage.TOKEN_WITH_ID);
		matcher.addURI(augura, P2PMessage.PATH_USER +"/#",
				P2PMessage.TOKEN_WITH_USER_ID);
		
		
		matcher.addURI(augura, P2PMessageItem.PATH ,
				P2PMessageItem.TOKEN);
		matcher.addURI(augura, P2PMessageItem.PATH_MASTER ,
				P2PMessageItem.TOKEN_WITH_MASTER);
		
		matcher.addURI(augura, P2PMessageItem.PATH_MASTER +"/#",
				P2PMessageItem.TOKEN_WITH_MASTER_ID);
		

		matcher.addURI(augura, SystemMessage.PATH, SystemMessage.TOKEN);
		matcher.addURI(augura, SystemMessage.PATH + "/#",
				SystemMessage.TOKEN_WITH_ID);

		return matcher;
	}

	public static class P2PMessage {

		public static String PATH = "p2p";

		public static String PATH_USER = PATH+ "/user";
		
		public static Uri URI = BASE_URI.buildUpon().appendPath(PATH).build();
		
		public static Uri QUERY_URI = BASE_URI.buildUpon().appendPath(PATH).appendPath("user").build();
		
		public static Uri INSERT_URI = BASE_URI.buildUpon().appendPath(PATH).appendPath("user").build();
		
		public static String TABLE_NAME ="p2p_message";

		public static final int TOKEN = 1;

		public static final int TOKEN_WITH_ID = 2;

		public static final int TOKEN_WITH_USER_ID = 3;
		
		public static class Cols {
			public static final String ID = BaseColumns._ID;
			
			public static final String FROM_USER = "FROM_USER_ID";
			public static final String TO_USER = "TO_USER_ID";
			public static final String DATE_TIME = "datetime";
			public static final String READ_FLAG = "flag";
		}
		
		public static final String[] COL_ARR = { Cols.ID, Cols.FROM_USER,
				Cols.TO_USER, Cols.DATE_TIME, Cols.READ_FLAG };
		
		
		public static String getCreateSql() {
			return " create table  " + TABLE_NAME 
					+ " ( "
					+ Cols.ID
					+ " integer primary key AUTOINCREMENT,"
					+ Cols.FROM_USER
					+ " NUMERIC(20),"
					+ Cols.TO_USER
					+ " NUMERIC(20),"
					+ Cols.DATE_TIME
					+" NUMERIC(20),"
					+ Cols.READ_FLAG
					+ " NUMERIC(2))";
		}
		
	}
	
	
	
	public static class P2PMessageItem {

		public static String PATH = "p2pitem";
		
		public static String PATH_MASTER = PATH+"/master" ;
		
		public static Uri INSERT = BASE_URI.buildUpon().appendPath(PATH).appendPath("master").build();
		
		public static Uri QUERY_URI = BASE_URI.buildUpon().appendPath(PATH).appendPath("master").build();

		public static String TABLE_NAME ="p2p_message_item";
		
		public static final int TOKEN = 4;
		public static final int TOKEN_WITH_MASTER = 5;
		public static final int TOKEN_WITH_MASTER_ID = 6;
		
		
		public static class Cols {
			public static final String ID = BaseColumns._ID;
			
			public static final String MASTER_ID = "master_id";
			public static final String TYPE = "item_type";
			public static final String CONTENT = "content";
		}
		
		
		public static final String[] COL_ARR = { Cols.ID, Cols.MASTER_ID,
			Cols.TYPE, Cols.CONTENT};
		
		
		public static String getCreateSql() {
			return " create table  " + TABLE_NAME 
					+ " ( "
					+ Cols.ID
					+ " integer primary key AUTOINCREMENT,"
					+ Cols.MASTER_ID
					+ " NUMERIC(20),"
					+ Cols.TYPE
					+ " NUMERIC(1),"
					+ Cols.CONTENT
					+ " TEXT)";
		}
		
	}
	
	

	public static class SystemMessage {

		public static String PATH = "system";

		public static String NAME = PATH;

		public static final int TOKEN = 7;

		public static final int TOKEN_WITH_ID = 8;
		
		public static String TABLE_NAME ="system_message";

		public static Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH)
				.build();

		public static class Cols {
			public static final String ID = BaseColumns._ID;

			public static final String SYSTEM_MESSAGE_TYPE = "msg_type";
			public static final String SYSTEM_MESSAGE_CONTENT = "msg_content";
			public static final String SYSTEM_MESSAGE_STATE = "msg_state";
			public static final String SYSTEM_MESSAGE_TIMESTAMP = "msg_stamp";

			public static final String[] ALL_CLOS = { ID, SYSTEM_MESSAGE_TYPE,
					SYSTEM_MESSAGE_CONTENT, SYSTEM_MESSAGE_STATE,
					SYSTEM_MESSAGE_TIMESTAMP };
		}
		
		
		public static String getCreateSql() {
			return " create table  " + TABLE_NAME 
					+ " ( "
					+ Cols.ID
					+ " integer primary key AUTOINCREMENT,"
					+ Cols.SYSTEM_MESSAGE_TYPE
					+ " NUMERIC(1),"
					+ Cols.SYSTEM_MESSAGE_CONTENT
					+ " text,"
					+ Cols.SYSTEM_MESSAGE_STATE
					+ " NUMERIC(1),"
					+ Cols.SYSTEM_MESSAGE_TIMESTAMP
					+ " NUMERIC(20))";
		}
	}

}
