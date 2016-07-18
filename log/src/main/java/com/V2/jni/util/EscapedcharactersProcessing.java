package com.V2.jni.util;


import android.text.TextUtils;

public class EscapedcharactersProcessing {

	public static String convert(String str) {
		if (str == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case 0xD:
				buf.append("&#x0D;");
				break;
			case 0xA:
				buf.append("&#x0A;");
				break;
			case 0x9:
				buf.append("&#x09;");
				break;
			case '&':
				buf.append("&amp;");
				break;
			case '\'':
				buf.append("&apos;");
				break;
			case '"':
				buf.append("&quot;");
				break;
			default:
				buf.append(c);
				break;
			}
		}

		// str = str.replace("<", "&lt;");
		// str = str.replace(">", "&gt;");
		// str = str.replace("0xD", "&#x0D;");
		// str = str.replace("0xA", "&#x0A;");
		// str = str.replace("0x9", "&#x09;");
		// str = str.replace("&", "&amp;");
		// str = str.replace("'", "&apos;");
		// str = str.replace("\"", "&quot;");

		return buf.toString();
	}
	
	public static String convertAmp(String str) {
		if (str == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '&':
				buf.append("&amp;");
				break;
			default:
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static String reverse(String str) {
		
		if (!TextUtils.isEmpty(str)){
			str = str.replace("&lt;", "<");
			str = str.replace("&gt;", ">");
			str = str.replace("&#x0D;", "0xD");
			str = str.replace("&#x0A;", "0xA");
			str = str.replace("&#x09;", "0x9");
			str = str.replace("&amp;", "&");
			str = str.replace("&apos;", "'");
			str = str.replace("&quot;", "\"");
			return str;
		}
		else{
			V2Log.e("HeartCharacterProcessing : reverse failed...the given String is --" + str);
			return "";
		}
	}

}
