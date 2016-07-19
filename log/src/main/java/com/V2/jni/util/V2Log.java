package com.V2.jni.util;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO add file log output
 *
 * @author 28851274
 */
public final class V2Log {



    private static final int BUF_MAX = 8172;
    private static final String FILE_NAME = "v2tech";
    private static final int LEVEL_DEBUG = 1;
    private static final int LEVEL_INFO = 2;
    private static final int LEVEL_WARN = 3;
    private static final int LEVEL_ERROR = 4;

    public static final String TAG = "V2TECH";


    private static char[] buf = new char[BUF_MAX];
    private static int idx = 0;
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static int fileIdx = 1;
    private static int fileSize = 8172000;
    private static String logDir;;


    private V2Log() {

    }


    /**
     *
     * @param ld  log folder
     * @param fs  file maximum size
     */
    public static void initLogConfig(String ld, int fs) {
        logDir = ld;
        fileSize = fs;
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        wrtiteBuf(LEVEL_INFO, TAG, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        wrtiteBuf(LEVEL_ERROR, TAG, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
        wrtiteBuf(LEVEL_WARN, TAG, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        wrtiteBuf(LEVEL_DEBUG, TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
        wrtiteBuf(LEVEL_INFO, TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
        wrtiteBuf(LEVEL_ERROR, TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
        wrtiteBuf(LEVEL_WARN, TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
        wrtiteBuf(LEVEL_DEBUG, TAG, msg);
    }


    public static void i(Throwable e) {
        String str = getStackTraceString(e);
        Log.i(TAG, str);
        wrtiteBuf(LEVEL_INFO, TAG, str);
    }

    public static void e(Throwable e) {
        String str = getStackTraceString(e);
        Log.e(TAG, str);
        wrtiteBuf(LEVEL_ERROR, TAG, str);
    }

    public static void w(Throwable e) {
        String str = getStackTraceString(e);
        Log.w(TAG, str);
        wrtiteBuf(LEVEL_WARN, TAG, str);
    }

    public static void d(Throwable e) {
        String str = getStackTraceString(e);
        Log.d(TAG, str);
        wrtiteBuf(LEVEL_DEBUG, TAG, str);
    }


    public static void flush() {

    }


    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


    private static void wrtiteBuf(int level, String tag, String str) {
        StringBuilder sb = new StringBuilder(str.length() + tag.length()+ 30);
        sb.append(df.format(Calendar.getInstance().getTime())).append(" ");
        sb.append(tag);
        switch (level) {
            case LEVEL_DEBUG:
                sb.append("[DEBUG] ");
                break;
            case LEVEL_INFO:
                sb.append("[INFO] ");
                break;
            case LEVEL_WARN:
                sb.append("[WARN] ");
                break;
            case LEVEL_ERROR:
                sb.append("[ERROR] ");
                break;
        }
        sb.append(str);
        sb.append("\n");
        int len = sb.length();
        boolean flush = false;
        char[] flushBuf = null;
        synchronized (buf) {
            int curIdx = idx;
            int capcity = BUF_MAX - curIdx;
            if (capcity >= len) {
                sb.getChars(0, len, buf, curIdx);
            } else {
                sb.getChars(0, capcity, buf, curIdx);
                flush = true;
                flushBuf = new char[BUF_MAX];
                System.arraycopy(buf, 0, flushBuf, 0, BUF_MAX);
                sb.getChars(len - capcity, len, buf, 0);
            }
            idx = (curIdx + len) % BUF_MAX;
        }

        if (flush) {
            if (flushBuf == null) {
                throw new RuntimeException("Illegal state flush buf is null but flush flag:" + flush);
            }
            writeFile(flushBuf);
            flushBuf = null;
        }


    }


    private static synchronized  void writeFile(char[] flushBuf) {
        File dir = new File(logDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.canWrite()) {
            dir.setWritable(true);
        }

        File f = null;
        while(true) {
            f = new File(logDir + "/" +FILE_NAME+"_"+fileIdx+".log");
            Log.i(TAG, " try new log file:" + f.getAbsolutePath());
            if(f.length() > fileSize) {
                fileIdx ++;
                continue;
            } else {
                break;
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
            fw.write(flushBuf, 0, flushBuf.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Can not save file");
                }
            }
        }
    }
}
