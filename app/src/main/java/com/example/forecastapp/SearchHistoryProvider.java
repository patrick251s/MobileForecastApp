package com.example.forecastapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchHistoryProvider extends ContentProvider {
    private DatabaseHelper databaseHelper;
    private static final String ID_CLASS = "com.example.forecastapp.SearchHistoryProvider";
    public static final Uri URI_CONTENT = Uri.parse("content://" + ID_CLASS +"/"+  DatabaseHelper.TABLE_NAME);
    private static final int ALL_TABLE = 1;
    private static final int CHOSEN_ROW = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(ID_CLASS, DatabaseHelper.TABLE_NAME, ALL_TABLE);
        uriMatcher.addURI(ID_CLASS, DatabaseHelper.TABLE_NAME + "/#", CHOSEN_ROW);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cityCursor = null;
        switch (uriType) {
            case ALL_TABLE:
                cityCursor = db.query(true, DatabaseHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, "10", null);
                break;
            case CHOSEN_ROW:
                cityCursor = db.query(false, DatabaseHelper.TABLE_NAME, projection, addToSelection(selection, uri), selectionArgs, null, null, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        cityCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cityCursor;
    }

    private String addToSelection(String selection, Uri uri)
    {
        if (selection != null && !selection.equals("")) selection = selection + " and " + DatabaseHelper.ID + "=" + uri.getLastPathSegment();
        else selection = DatabaseHelper.ID + "=" + uri.getLastPathSegment();
        return selection;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long addedID = 0;
        switch (uriType) {
            case ALL_TABLE:
                addedID = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(DatabaseHelper.TABLE_NAME + "/" + addedID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int deletedNumber = 0;
        switch (uriType) {
            case ALL_TABLE:
                deletedNumber = db.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case CHOSEN_ROW:
                deletedNumber = db.delete(DatabaseHelper.TABLE_NAME, addToSelection(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedNumber;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int updatedNumber = 0;
        switch (uriType) {
            case ALL_TABLE:
                updatedNumber = db.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CHOSEN_ROW:
                updatedNumber = db.update(DatabaseHelper.TABLE_NAME, values, addToSelection(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedNumber;
    }
}
