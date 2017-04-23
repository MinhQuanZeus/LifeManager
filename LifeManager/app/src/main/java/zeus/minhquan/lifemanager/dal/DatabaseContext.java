package zeus.minhquan.lifemanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import zeus.minhquan.lifemanager.model.RemindInfo;

/**
 * Created by EDGY on 4/21/2017.
 */

public class DatabaseContext extends SQLiteOpenHelper{
    private static final String TAG = "DatabaseContext";
    private static final String REMIND_TABLE_NAME = "remind_tbl";
    private static final String DATABASE_NAME = "remind.db";
    private static final int DATABASE_VERSION = 1;
    private static final String REMIND_ID = "id";
    private static final String REMIND_TITLE = "title";
    private static final String REMIND_DESCRIPTION = "description";
    private static final String REMIND_DATE = "date";
    private static final String REMIND_TIME = "time";
    private static final String[] REMIND_ALL_COLUMNS = new String[]{
            REMIND_TITLE,
            REMIND_DESCRIPTION,
            REMIND_DATE,
            REMIND_TIME
    };


    private SQLiteDatabase sqLiteDatabase;

    public DatabaseContext(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public boolean add(RemindInfo remindInfo){
        try {
            if (remindInfo != null) {
                sqLiteDatabase = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(REMIND_TITLE, remindInfo.getTitle());
                values.put(REMIND_DESCRIPTION, remindInfo.getDescription());
                values.put(REMIND_DATE, remindInfo.getDate());
                values.put(REMIND_TIME, remindInfo.getTime());
                sqLiteDatabase.insert(REMIND_TABLE_NAME, null, values);
                sqLiteDatabase = this.getReadableDatabase();
                sqLiteDatabase.close();
                return true;
            }
        } catch(Exception ex){
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    public boolean delete(int remindId){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(REMIND_TABLE_NAME, REMIND_ID + " = ?",
                    new String[]{String.valueOf(remindId)});
            db.close();
            return true;
        } catch (Exception ex){
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + REMIND_TABLE_NAME + "(" + REMIND_TITLE + " TEXT,"
                + REMIND_DESCRIPTION + " TEXT," + REMIND_DATE + " TEXT," + REMIND_TIME + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+REMIND_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
