package com.gzeinnumer.sb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.gzeinnumer.sb.helper.MyLibSQLiteBuilderHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class SQLiteBuilder {

    private static final String TAG = "SQLiteBuilder";

    public static MyLibSQLiteBuilderHelper builder(Class<?> clss, Context context) {
        return new MyLibSQLiteBuilderHelper(clss, context);
    }

    protected boolean deleteDatabase(String DB_PATH) {
        try {
            return new File(DB_PATH).delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean deleteDatabaseOnRoot(Context context, String DB_NAME) {
        try {
            final String inFileName = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME;
            return new File(inFileName).delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("SdCardPath")
    protected boolean backUpDatabase(Context applicationContext, String DB_PATH, String DB_NAME) {
        try {
            if (!DB_NAME.contains("/")) {
                DB_NAME = "/" + DB_NAME;
            }
            if (!new File(DB_PATH).exists()) {
                boolean makeDir = new File(DB_PATH).mkdirs();
            }
            final String inFileName = "/data/data/" + applicationContext.getPackageName() + "/databases/" + DB_NAME;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            OutputStream output = new FileOutputStream(DB_PATH + DB_NAME);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "backup: failed backup Database");
            e.printStackTrace();
            return false;
        }
    }

    protected boolean isDatabaseExists(String DB_PATH) {
        try {
            return new File(DB_PATH).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("SdCardPath")
    protected boolean isDatabaseExistOnRoot(Context context, String DB_NAME) {
        try {
            final String inFileName = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME;
            return new File(inFileName).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
