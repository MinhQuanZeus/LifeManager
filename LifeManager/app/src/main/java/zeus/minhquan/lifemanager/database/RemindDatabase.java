package zeus.minhquan.lifemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import zeus.minhquan.lifemanager.database.models.Remind;


/**
 * Created by anh82 on 4/18/2017.
 */

public class RemindDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "LifeManager.db";
    private static final String TABLE_NAME = "remind_tbl";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private static final String ID="id";
    private  static final String TITLE= "title";
    private  static final String DESCRIPTION = "description";
    private  static final String DATE = "date";
    private  static final String TIME = "time";
    private static final String RECORD_NAME = "record_name";


    private static final String[] REMIND_ALL_COLUMNS = new String[]{
            ID,
            TITLE,
            DESCRIPTION,
            DATE,
            TIME,
            RECORD_NAME
    };

    public RemindDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public boolean add(Remind remind){
        long result = 0;
        if(remind != null){
            db = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(TITLE, remind.getTitle());
            values.put(DESCRIPTION, remind.getDescription());
            values.put(TIME, remind.getTime());
            values.put(DATE, remind.getDate());
            if(remind.getRecord_name() != null && remind.getRecord_name() != ""){
                values.put(RECORD_NAME, remind.getRecord_name());
            }

            result = db.insert(TABLE_NAME, null, values);
            Log.d("Ok ! fine",result + "");
            db.close();
        }
        return result != 0;
    }
    public boolean delete(int remindId){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, ID + " = ?",
                    new String[]{String.valueOf(remindId)});
            db.close();
            return true;
        } catch (Exception ex){

        }
        return false;
    }
    public int updateContact(Remind remind) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TITLE, remind.getTitle());
        values.put(DESCRIPTION, remind.getDescription());
        values.put(TIME, remind.getTime());
        values.put(DATE, remind.getDate());
        if(remind.getRecord_name() != null && remind.getRecord_name() != ""){
            values.put(RECORD_NAME, remind.getRecord_name());
        }

        return db.update(TABLE_NAME, values, ID + " = ?",
                new String[] { String.valueOf(remind.getId()) });
    }




    public List<Remind> loadAllReminds(){
        // get readable database , lay quyen dc doc
        List<Remind> reminds = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,REMIND_ALL_COLUMNS, null, null, null,null, null);

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            String time = cursor.getString(cursor.getColumnIndex(TIME));
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            if(cursor.getString(cursor.getColumnIndex(RECORD_NAME)) != null && cursor.getString(cursor.getColumnIndex(RECORD_NAME)) != "" ){
                reminds.add(new Remind(id,title,description,date,time,cursor.getString(cursor.getColumnIndex(RECORD_NAME)) ));
            } else {
                reminds.add(new Remind(id,title,description,date,time));
            }
        }
        cursor.close();
        db.close();
        return reminds;
    }


    public int getIDMax(){
       int id = 0;
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,REMIND_ALL_COLUMNS, null, null, null,null, null);
        while(cursor.moveToNext()){
             if( cursor.getInt(cursor.getColumnIndex(ID)) > id){
                 id = cursor.getInt(cursor.getColumnIndex(ID));
             }

        }
        cursor.close();
        db.close();

        return id;
    }
}
