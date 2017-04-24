package zeus.minhquan.lifemanager.games;

/**
 * Created by QuanT on 4/22/2017.
 */

enum GameButtonBehavior {
    AUDIO,
    CAMERA
}

public interface IGameMediator {
    void start();
    void stop();
    boolean isGameRunning();

    void onGameSuccess(String successMessage);
    void onGameFailureWithRetry(String failureMessage);
    void onGameFailure(String failureMessage);
    void onGameInternalError();

    void registerStateBanner(GameStateBanner gameStateBanner);
    void registerCountDownTimer(CountDownTimerView countDownTimerView, int timeout);
    void registerProgressButton(ProgressButton progressButton, GameButtonBehavior buttonBehavior);
    void registerGame(IGameImplementation game);
}