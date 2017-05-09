package zeus.minhquan.lifemanager.database.models;

/**
 * Created by anh82 on 5/6/2017.
 */

public class MyDate {
    private int year;
    private int month;
    private int myDate;
    private static final int COUNT_MONTH_START = 1;

    public MyDate(int year, int month, int date) {
        this.year = year;
        this.month = month + COUNT_MONTH_START;
        this.myDate = date;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getMyDate() {
        return myDate;
    }

    public String getDate(){
        return myDate + "/" + month + "/" + year;
    }
}
