package zeus.minhquan.lifemanager.utils;

/**
 * Created by anh82 on 5/9/2017.
 */

public class CountDownAlarm {
    public  static String getTimeCountDown(int second){
        int minute = second/60;
        int hours = minute/60;
        int day = hours/24;

        int secondCur = second - (minute*60);
        int minuteCur = minute - (hours*60);
        int hoursCur = hours - (day*24);

        if(day != 0 ){
            return day + " date " + hoursCur +" hours " + minuteCur + " minutes " + secondCur +" seconds ";
        } else if(hoursCur != 0){
            return  hoursCur +" hours " + minuteCur + " hours " + secondCur +" seconds ";
        } else if(minuteCur != 0 ){
            return minuteCur + " minutes " + secondCur +" seconds ";
        } else {
            return secondCur +" seconds ";
        }

    }
}
