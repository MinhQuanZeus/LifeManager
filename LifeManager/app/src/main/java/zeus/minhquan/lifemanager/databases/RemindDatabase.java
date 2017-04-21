package zeus.minhquan.lifemanager.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


import java.util.ArrayList;
import java.util.List;

import zeus.minhquan.lifemanager.databases.models.Remind;

/**
 * Created by anh82 on 4/18/2017.
 */

public class RemindDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "Life_Manager.db";
    private static final int DATABASE_VERSION = 1;

    private  static final String TITLE= "title";
    private  static final String DESCRIPTION = "description";
    private  static final String Time= "time";


    private static final String[] REMIND_ALL_COLUMNS = new String[]{
            TITLE,
           DESCRIPTION,
            Time,

    };

    public RemindDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    public List<Remind> loadAllReminds(){
        // get readable database , lay quyen dc doc
        List<Remind> reminds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        //query ==> cursor
        Cursor cursor = db.query("remind",REMIND_ALL_COLUMNS, null, null, null,null, null);

        // Go through rows

        while(cursor.moveToNext()){

            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            String time = cursor.getString(cursor.getColumnIndex(Time));
            reminds.add(new Remind(title,description,time));

        }
        cursor.close();
        db.close();
        return reminds;
    }
}
