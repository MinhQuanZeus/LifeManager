package zeus.minhquan.lifemanager.games;

/**
 * Created by QuanT on 4/22/2017.
 */

public interface IGameImplementation {
    void initializeCapture();

    void startCapture();

    void stopCapture();

    void onCountDownTimerExpired();

    void onSucceeded();

    void onFailed();

    void onInternalError();
}
