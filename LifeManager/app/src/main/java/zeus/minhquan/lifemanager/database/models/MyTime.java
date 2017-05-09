package zeus.minhquan.lifemanager.database.models;

/**
 * Created by anh82 on 5/6/2017.
 */

public class MyTime {
    private static final int HOUR_OF_TIME = 12;
    private int hour;
    private int min;
    private HalfTime halfTime;

    public MyTime(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public int getHour() {
        halfTime = getHalfTime(hour);
        if (halfTime == HalfTime.AM && hour <= 12){
            return hour;
        } else if(halfTime == HalfTime.PM && hour > 12){
            return hour -= HOUR_OF_TIME;
        }
        else return hour + 12;
    }

    public int getMin() {
        return min;
    }

    public String getTime(){
        String hourFormat = "";
        String minFormat = "";
        halfTime = getHalfTime(hour);
        if(hour < 10){
            hourFormat = "0" + hour;
        } else {
            hourFormat = "" + hour;
        }
        if(min < 10){
            minFormat = "0" + min;
        } else {
            minFormat = "" + min;
        }
        return hourFormat + " : " + minFormat + " " + halfTime;}
    public HalfTime getHalfTime(int hour){
        if(hour > HOUR_OF_TIME){
            this.hour -= HOUR_OF_TIME;
            return HalfTime.PM;
        } else return HalfTime.AM;
    }

}
