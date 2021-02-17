<p align="center">
  <img src="https://i.ytimg.com/vi/F9wbogYwTVM/maxresdefault.jpg"/>
</p>

<h1 align="center">
    SQLiteBuilder
</h1>

<p align="center">
    <a><img src="https://img.shields.io/badge/Version-2.0.0-brightgreen.svg?style=flat"></a>
    <a><img src="https://img.shields.io/badge/ID-gzeinnumer-blue.svg?style=flat"></a>
    <a><img src="https://img.shields.io/badge/Java-Suport-green?logo=java&style=flat"></a>
    <a><img src="https://img.shields.io/badge/Kotlin-Suport-green?logo=kotlin&style=flat"></a>
    <a href="https://github.com/gzeinnumer"><img src="https://img.shields.io/github/followers/gzeinnumer?label=follow&style=social"></a>
    <br>
    <p>Simple builder for <b>SQLite</b> .</p>
</p>

---
# Content List
* [Download](#download)
* [Feature List](#feature-list)
* [Tech stack and 3rd library](#tech-stack-and-3rd-library)
* [Usage](#usage)
* [Example Code/App](#example-codeapp)
* [Version](#version)
* [Contribution](#contribution)

---
# Download
Add maven `jitpack.io` and `dependencies` in `build.gradle (Project)` :
```gradle
// build.gradle project
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

// build.gradle app/module
dependencies {
  ...
  implementation 'com.github.gzeinnumer:SQLiteBuilder:version'
}
```

---
# Feature List
- [x] [Make Class Table](#make-class-table)
- [x] [Usage](#usage)
- [x] [File Database On External](#file-database-on-external)
- [x] [Load Database From External](#load-database-from-external)
- [x] [Delete Database On External](#delete-database-on-external)
- [x] [Delete Database On Root](#delete-database-on-root)
- [x] [Backup Database From Root To External](#backup-database-from-root-to-external)
- [x] [Check File Database Exists On External](#check-file-database-exists-on-external)
- [x] [Check File Database Exists On Root](#check-file-database-exists-on-root)

---
# Tech stack and 3rd library
- [SQLite](https://developer.android.com/training/data-storage/sqlite?hl=id)

---
# Usage

* Make Class Table.

Example : Make class `Table1` and put your query **`CREATE TABLE table1(...);`** to annotation `@CreateTableQuery(query = { ... })`.
```java
@CreateTableQuery(
    query = "CREATE TABLE table1 (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, rating REAL, descr TEXT, flag_active INTEGER, created_at TEXT)"
)
public class Table1{

    //your code

}
```

#
* Make Instance Class `DBInstance` extends `SQLiteBuilder`
```java
public class DBInstance extends SQLiteBuilder {
}
```

#
* Define your Table Class
```java
@SQLiteDatabaseEntity(entities = {
        Table1.class
})
public class DBInstance extends SQLiteBuilder {

   ...

}
```

#
* Make function instance in `DBInstance`.
```java
@SQLiteDatabaseEntity(entities = {
        Table1.class
})
public class DBInstance extends SQLiteBuilder {

    private static SQLiteDatabase sqLiteDatabase;
    public static String DB_NAME = "MyLibSQLiteSimple.db";

    public static SQLiteDatabase getDataBase(Context context) {
        sqLiteDatabase = SQLiteBuilder.builder(DBInstance.class, context)
                .setDatabaseName(DB_NAME)
                .setDatabaseVersion(1)
                .build();
        return sqLiteDatabase;
    }

}
```

#
* After you have defined the entity and make function `getDataBase(Context context)`, you can use the following code to create an instance of the database:
```java
SQLiteDatabase sqLiteDatabase = DBInstance.getDataBase(getApplicationContext());
```

Database and table created on **Root Directory**
<p align="center">
  <img src="https://github.com/gzeinnumer/SQLiteBuilder/blob/master/preview/example1.jpg"/>
</p>
<p align="center">
  <img src="https://github.com/gzeinnumer/SQLiteBuilder/blob/master/preview/example4.jpg" width="400"/>
</p>

#
### File Database On External

If you want to **PUT** your database `file` on **External** you can add and use function `putDatabaseToExternal(DB_PATH_BC)` in `SQLiteBuilder.builder(DBInstance.class, context)`.
```java
private static SQLiteDatabase sqLiteDatabase;

public static SQLiteDatabase getDataBase(Context context) {
    String DB_PATH_BC = Environment.getExternalStorageDirectory().toString()
        + "/MyLibSQLiteBC/MyLibSQLiteSimple.db";

    sqLiteDatabase = SQLiteBuilder.builder(DBInstance.class, context)
            ...
            .putDatabaseToExternal(DB_PATH_BC)
            ...
            .build();
    return sqLiteDatabase;
}
```
File Database will be create on your folder Path. (Not in Root anymore like [this](#usage))
<p align="center">
  <img src="https://github.com/gzeinnumer/SQLiteBuilder/blob/master/preview/example2.jpg" width="400"/>
</p>

#
### Load Database From External

If you want to **Load** your database `file` **From External** you can add and use function `loadDatabaseFromExternal(DB_PATH_EXTERNAL)` in `SQLiteBuilder.builder(DBInstance.class, context)`.
```java
private static SQLiteDatabase sqLiteDatabase;

public static SQLiteDatabase getDataBase(Context context) {
    String DB_PATH_EXTERNAL = Environment.getExternalStorageDirectory().toString()
        + "/MyLibSQLiteExternal/MyLibSQLiteSimple.db";

    sqLiteDatabase = SQLiteBuilder.builder(DBInstance.class, context)
            ...
            .loadDatabaseFromExternal(DB_PATH_EXTERNAL)
            ...
            .build();
    return sqLiteDatabase;
}
```
File database will be **Load** from your `Database Path`.
<p align="center">
  <img src="https://github.com/gzeinnumer/SQLiteBuilder/blob/master/preview/example3.jpg" width="400"/>
</p>

**Warning this method will ignore `@CreateTableQuery(query = ""`)**

You can write `Class Table` like this
```java
@CreateTableQuery
public class Table1{
    //your code
}
```

#
### Delete Database on External
```java
...
public class DBInstance extends SQLiteBuilder {

    ...

    public boolean delete() {
        String DB_PATH_EXTERNAL = Environment.getExternalStorageDirectory().toString()
            + "/MyLibSQLiteExternal/MyLibSQLiteSimple.db";
        return deleteDatabase(DB_PATH_EXTERNAL); // return true/false
    }
}
```

#
### Delete Database on Root
```java
...
public class DBInstance extends SQLiteBuilder {

    ...

    public boolean deleteRootDb(Context context) {
        String DB_NAME = "MyLibSQLiteSimple.db";
        return deleteDatabaseOnRoot(context, DB_NAME);
    }
}
```

#
### Backup Database From Root To External
```java
...
public class DBInstance extends SQLiteBuilder {

    ...

    public boolean backUp(Context context) {
        String BACK_UP_TO = Environment.getExternalStorageDirectory().toString()
            + "/MyLibSQLiteExternalBackUp";
        String DB_NAME = "MyLibSQLiteSimple.db";
        return backUpDatabase(context, BACK_UP_TO, DB_NAME);
    }
}
```
<p align="center">
  <img src="https://github.com/gzeinnumer/SQLiteBuilder/blob/master/preview/example5.jpg" width="400"/>
</p>

#
### Check File Database Exists On External
```java
...
public class DBInstance extends SQLiteBuilder {

    ...

    public boolean isDBExist() {
        String DB_PATH_EXTERNAL = Environment.getExternalStorageDirectory().toString()
            + "/MyLibSQLiteExternal/MyLibSQLiteSimple.db";
        return isDatabaseExists(DB_PATH_EXTERNAL);
    }
}
```

#
### Check File Database Exists On Root
```java
...
public class DBInstance extends SQLiteBuilder {

    ...

    public boolean isDBExistOnRoot(Context context){
        String DB_NAME = "MyLibSQLiteSimple.db";
        return isDatabaseExistOnRoot(context, DB_NAME);
    }
}
```

---

You can combine this library with [EasySQLiteCRUD](https://github.com/gzeinnumer/EasySQLiteCRUD)

---
# Example Code/App

Sample APP, just clone it [Java](https://github.com/gzeinnumer/MyLibSQLiteBuilderExample) & [Kotlin](https://github.com/gzeinnumer/MyLibSimpleSQLitekt)

[Sample Code And App](https://github.com/gzeinnumer/MyLibSQLiteBuilderExample)

---
# Version
- **1.0.1**
  - First Release
- **1.0.2**
  - Bug Fixing
- **1.0.3**
  - Ready Release
- **2.0.0**
  - Suppoert SDK 16

---
# Contribution
You can sent your constibution to `branch` `open-pull`.

---

```
Copyright 2021 M. Fadli Zein
```
