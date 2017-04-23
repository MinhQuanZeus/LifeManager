package zeus.minhquan.lifemanager.games;

import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by QuanT on 4/22/2017.
 */

public class GameStateManager implements IGameMediator {
    private static String TAG = "GameStateManager";

    GameStateBanner mGameStateBanner;
    CountDownTimerView mCountDownTimer;
    ProgressButton mProgressButton;
    GameButtonBehavior mButtonBehavior;
    WeakReference<IGameImplementation> mGameRef;
    boolean mGameRunning;

    // Should be called from Fragment::onStart, which is when it becomes visible to the user
    public void start(){
        Log.d(TAG, "Entered start!");
        mGameRunning = true;
        mCountDownTimer.start();
        IGameImplementation game = mGameRef.get();
        if (game != null) {
            game.initializeCapture();
        }
    }

    // Should be called from Fragment::onStop, when the fragment is invisible
    public void stop() {
        Log.d(TAG, "Entered stop!");
        mGameRunning = false;
        IGameImplementation game = mGameRef.get();
        if (game != null) {
            game.stopCapture();
        }
        mProgressButton.setReady();
    }

    public boolean isGameRunning() {
        return mGameRunning;
    }

    public void onGameSuccess(String successMessage) {
        Log.d(TAG, "Entered onGameSuccess!");
        if (isGameRunning()) {
            handleButtonState();
            mCountDownTimer.stop();
            mGameStateBanner.success(successMessage, new GameStateBanner.Command() {
                @Override
                public void execute() {
                    Log.d(TAG, "Entered onGameSuccess callback!");
                    if (isGameRunning()) {
                        IGameImplementation game = mGameRef.get();
                        if (game != null) {
                            game.onSucceeded();
                        }
                    }
                }
            });
        }
    }

    public void onGameFailureWithRetry(String failureMessage) {
        Log.d(TAG, "Entered onGameFailureWithRetry!");
        // If the countdown timer has just expired and has already registered a failure command,
        // then we should avoid changing state
        if (isGameRunning() && !mCountDownTimer.hasExpired()) {
            mCountDownTimer.pause();
            mGameStateBanner.failure(failureMessage, new GameStateBanner.Command() {
                @Override
                public void execute() {
                    Log.d(TAG, "Entered onGameFailureWithRetry callback!");
                    if (isGameRunning()) {
                        mCountDownTimer.resume();
                        mProgressButton.setReady();
                    }
                }
            });
        }
    }

    public void onGameFailure(String failureMessage) {
        Log.d(TAG, "Entered onGameFailure!");
        handleButtonState();
        mCountDownTimer.stop();
        mProgressButton.setClickable(false);
        mGameStateBanner.failure(failureMessage, new GameStateBanner.Command() {
            @Override
            public void execute() {
                Log.d(TAG, "Entered onGameFailure callback!");
                IGameImplementation game = mGameRef.get();
                if (game != null) {
                    game.onFailed();
                }
            }
        });
    }

    public void onGameInternalError() {
        Log.d(TAG, "Entered onGameInternalError!");
        handleButtonState();
        mCountDownTimer.stop();
        mProgressButton.setClickable(false);
        IGameImplementation game = mGameRef.get();
        if (game != null) {
            game.onInternalError();
        }
    }

    public void registerStateBanner(GameStateBanner gameStateBanner) {
        mGameStateBanner = gameStateBanner;
    }

    public void registerCountDownTimer(CountDownTimerView countDownTimer, int timeout) {
        mCountDownTimer = countDownTimer;
        mCountDownTimer.init(timeout, new CountDownTimerView.Command() {
            @Override
            public void execute() {
                Log.d(TAG, "Countdown timer expired!");
                if (isGameRunning()) {
                    IGameImplementation game = mGameRef.get();
                    if (game != null) {
                        game.stopCapture();
                        game.onCountDownTimerExpired();
                    }
                }
            }
        });
    }

    public void registerProgressButton(ProgressButton progressButton,
                                       GameButtonBehavior buttonBehavior) {
        mProgressButton = progressButton;
        mButtonBehavior = buttonBehavior;

        if (mButtonBehavior == GameButtonBehavior.AUDIO) {
            mProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mProgressButton.isReady()) {
                        IGameImplementation game = mGameRef.get();
                        if (game != null) {
                            game.startCapture();
                        }
                        mProgressButton.waiting();
                    } else {
                        IGameImplementation game = mGameRef.get();
                        if (game != null) {
                            game.stopCapture();
                        }
                    }
                }
            });
        } else if (mButtonBehavior == GameButtonBehavior.CAMERA) {
            mProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCountDownTimer.pause();
                    mProgressButton.loading();
                    IGameImplementation game = mGameRef.get();
                    if (game != null) {
                        game.startCapture();
                    }
                }
            });
        }
        mProgressButton.setReady();
    }

    public void registerGame(IGameImplementation game) {
        mGameRef = new WeakReference<>(game);
    }

    private void handleButtonState() {
        if (mButtonBehavior == GameButtonBehavior.CAMERA) {
            mProgressButton.stop();
        }
    }
}

