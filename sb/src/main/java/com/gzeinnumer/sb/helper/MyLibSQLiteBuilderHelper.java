package com.gzeinnumer.sb.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gzeinnumer.sb.struct.CreateTableQuery;
import com.gzeinnumer.sb.struct.SQLiteDatabaseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyLibSQLiteBuilderHelper {
    private final String TAG = "Helper_";
    private final Context context;
    Class<?> aClass;
    private String DB_NAME;
    private int DATABASE_VERSION;
    private String DB_PATH_EXTERNAL = "";
    private String DB_PATH_BACKUP = "";
    private SQLiteDatabase myDataBase;

    public MyLibSQLiteBuilderHelper(Class<?> clss, Context context) {
        this.aClass = clss;
        this.context = context;
    }

    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/'));
    }

    public MyLibSQLiteBuilderHelper setDatabaseName(String dbName) {
        if (!dbName.contains(".db")) {
            dbName = dbName + ".db";
        }
        DB_NAME = dbName;
        return this;
    }

    public MyLibSQLiteBuilderHelper setDatabaseVersion(int version) {
        DATABASE_VERSION = version;
        return this;
    }

    public MyLibSQLiteBuilderHelper putDatabaseToExternal(String dbPath) {
        DB_PATH_BACKUP = dbPath;
        return this;
    }

    public MyLibSQLiteBuilderHelper loadDatabaseFromExternal(String dbPath) {
        DB_PATH_EXTERNAL = dbPath;
        return this;
    }

    public SQLiteDatabase build() throws SQLException {

        MyLibSQLiteDBHelper myDB;
        if (DB_PATH_EXTERNAL.length() > 0) {
            List<String> list = getTableEntities();
            myDB = new MyLibSQLiteDBHelper(context, DB_NAME, null, DATABASE_VERSION, new ArrayList<>());
            File dbFile = new File(DB_PATH_EXTERNAL);

            if (dbFile.exists()) {
                Log.d(TAG, "DatabaseHelper: Database exist");
                this.myDataBase = SQLiteDatabase.openDatabase(DB_PATH_EXTERNAL, null, 0);
            } else {
                if (!checkDatabase()) {
                    try {
                        myDB.getReadableDatabase();
                        copyDatabase();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (DB_PATH_BACKUP.length() > 0) {
            List<String> list = getTableQueries();
            myDB = new MyLibSQLiteDBHelper(context, DB_NAME, null, DATABASE_VERSION, list);
            this.myDataBase = myDB.getWritableDatabase();
            backup(context);
            this.myDataBase = SQLiteDatabase.openDatabase(DB_PATH_BACKUP, null, 0);
        } else {
            List<String> list = getTableQueries();
            myDB = new MyLibSQLiteDBHelper(context, DB_NAME, null, DATABASE_VERSION, list);
            this.myDataBase = myDB.getWritableDatabase();
        }
        return this.myDataBase;
    }

    public List<String> getTableQueries() {
        List<String> list = new ArrayList<>();

        if (aClass.isAnnotationPresent(SQLiteDatabaseEntity.class)) {
            SQLiteDatabaseEntity sqLiteDatabaseEntity = aClass.getAnnotation(SQLiteDatabaseEntity.class);
            if (sqLiteDatabaseEntity != null) {
                Class[] classes = sqLiteDatabaseEntity.entities();
                for (Class aClass : classes) {
                    CreateTableQuery createTableQuery = (CreateTableQuery) aClass.getAnnotation(CreateTableQuery.class);
                    if (createTableQuery != null) {
                        if (createTableQuery.query().length() > 0) {
                            list.add(createTableQuery.query());
                        } else {
                            throw new RuntimeException("Annotation CreateTableQuery Should Not Empty " + classes.getClass().getName());
                        }
                    } else {
                        throw new RuntimeException("Annotation CreateTableQuery Not Found On " + classes.getClass().getName());
                    }
                }
            } else {
                logD("getTableQueries: Annotation SQLiteDatabaseEntity Not Found On " + aClass.getName());
                return list;
            }
        } else {
            logD("getTableQueries: Annotation SQLiteDatabaseEntity Not Found On " + aClass.getName());
            return list;
        }
        return list;
    }

    public List<String> getTableEntities() {
        List<String> list = new ArrayList<>();

        if (aClass.isAnnotationPresent(SQLiteDatabaseEntity.class)) {
            SQLiteDatabaseEntity sqLiteDatabaseEntity = aClass.getAnnotation(SQLiteDatabaseEntity.class);
            if (sqLiteDatabaseEntity != null) {
                Class[] classes = sqLiteDatabaseEntity.entities();
                for (Class aClass : classes) {
                    CreateTableQuery createTableQuery = (CreateTableQuery) aClass.getAnnotation(CreateTableQuery.class);
                    if (createTableQuery == null) {
                        throw new RuntimeException("Annotation CreateTableQuery Not Found On " + classes.getClass().getName());
                    }
                }
            } else {
                logD("getTableQueries: Annotation SQLiteDatabaseEntity Not Found On " + aClass.getName());
                return list;
            }
        } else {
            logD("getTableQueries: Annotation SQLiteDatabaseEntity Not Found On " + aClass.getName());
            return list;
        }
        return list;
    }

    private boolean checkDatabase() {
        boolean statusDB = false;
        SQLiteDatabase checkDB = null;
        try {
            if (new File(DB_PATH_EXTERNAL).exists()) {
                try {
                    checkDB = SQLiteDatabase.openDatabase(DB_PATH_EXTERNAL, null, 0);
                    statusDB = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return statusDB;
    }

    private void copyDatabase() throws IOException {
        if (new File(DB_PATH_EXTERNAL).exists()) {
            InputStream myInput = this.context.getAssets().open(DB_NAME);
            File outFile = this.context.getDatabasePath(DB_PATH_EXTERNAL);
            boolean isDeleted = outFile.delete();
            OutputStream myOutput = new FileOutputStream(outFile);
            byte[] buffer = new byte[1204];
            while (true) {
                int length = myInput.read(buffer);
                if (length <= 0) {
                    myOutput.flush();
                    myOutput.close();
                    myInput.close();

                    return;
                }
                myOutput.write(buffer, 0, length);
            }
        } else {
            Log.e(TAG, "copyDatabase: Database file doesn't exist " + DB_PATH_EXTERNAL);
        }
    }

    @SuppressLint("SdCardPath")
    private void backup(Context applicationContext) {
        try {
            String fileName = getNameFromUrl(DB_PATH_BACKUP);
            String dir = DB_PATH_BACKUP.replace(fileName, "");
            boolean makeDir = new File(dir).mkdirs();
            final String inFileName = "/data/data/" + applicationContext.getPackageName() + "/databases/" + DB_NAME;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            OutputStream output = new FileOutputStream(DB_PATH_BACKUP);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, "backup: failed backup Database");
            e.printStackTrace();
        }
    }

    private void logD(String msg) {
        Log.e(TAG, "logD: " + msg, null);
    }
}