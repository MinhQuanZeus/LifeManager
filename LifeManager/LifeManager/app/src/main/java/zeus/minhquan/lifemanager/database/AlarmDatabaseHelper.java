package zeus.minhquan.lifemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import zeus.minhquan.lifemanager.database.AlarmDbSchema.AlarmTable;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "alarmDatabase.db";

    public AlarmDatabaseHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "create table " + AlarmDbSchema.AlarmTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                AlarmTable.Columns.UUID + ", " +
                AlarmTable.Columns.TITLE + ", " +
                AlarmTable.Columns.ENABLED + ", " +
                AlarmTable.Columns.HOUR + ", " +
                AlarmTable.Columns.MINUTE + ", " +
                AlarmTable.Columns.DAYS + ", " +
                AlarmTable.Columns.TONE + ", " +
                AlarmTable.Columns.VIBRATE + ", " +
                AlarmTable.Columns.TONGUE_TWISTER + ", " +
                AlarmTable.Columns.COLOR_CAPTURE + ", " +
                AlarmTable.Columns.EXPRESS_YOURSELF + ", " +
                AlarmTable.Columns.NEW + ", " +
                AlarmTable.Columns.SNOOZED + ", " +
                AlarmTable.Columns.SNOOZED_HOUR + ", " +
                AlarmTable.Columns.SNOOZED_MINUTE + ", " +
                AlarmTable.Columns.SNOOZED_SECONDS +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AlarmDatabaseHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmTable.NAME);
        onCreate(db);
    }
}