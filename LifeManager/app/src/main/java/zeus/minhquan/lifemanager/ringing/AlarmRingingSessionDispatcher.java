package zeus.minhquan.lifemanager.ringing;

import android.content.Intent;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by QuanT on 4/22/2017.
 */

public abstract class AlarmRingingSessionDispatcher {
    Queue<Intent> mAlarmIntentQueue;

    public AlarmRingingSessionDispatcher() {
        mAlarmIntentQueue = new LinkedList<>();
    }

    public abstract void beforeDispatchFirstAlarmRingingSession();

    public abstract void dispatchAlarmRingingSession(Intent intent);

    public abstract void allAlarmRingingSessionsComplete();

    protected void registerAlarm(Intent intent) {
        if (mAlarmIntentQueue.offer(intent) &&
                mAlarmIntentQueue.size() == 1) {
            beforeDispatchFirstAlarmRingingSession();
            dispatchAlarmRingingSession(mAlarmIntentQueue.peek());
        }
    }

    protected void alarmRingingSessionCompleted() {
        if (mAlarmIntentQueue.poll() != null) {
            dispatchAlarmRingingSession(mAlarmIntentQueue.peek());
        }
        if (mAlarmIntentQueue.isEmpty()) {
            allAlarmRingingSessionsComplete();
        }
    }
}

