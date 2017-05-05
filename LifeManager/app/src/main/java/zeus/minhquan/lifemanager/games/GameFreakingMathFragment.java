package zeus.minhquan.lifemanager.games;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.utils.Loggable;
import zeus.minhquan.lifemanager.utils.Logger;


public class GameFreakingMathFragment extends Fragment {
    public static final String NO_NETWORK_FRAGMENT_TAG = "game_no_network";

    private final static int TIMEOUT_MILLISECONDS = 30000;
    GameFactory.GameResultListener mCallback;
    private CountDownTimerView mTimer;
    private TextView mInstructionText;
    private GameStateBanner mStateBanner;
    private ImageView iv_true, iv_false;
    private TextView tv_caculation;
    private int a, b, r_true;
    private boolean isTrue = false;
    private boolean isCorrect = false;
    private int mTapsRemaining = 15;
    private int mFailedsRemaining = 3;
    private String cal = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_freaking_math, container, false);

        mStateBanner = (GameStateBanner) view.findViewById(R.id.game_state);
        iv_false = (ImageView) view.findViewById(R.id.iv_false);
        iv_true = (ImageView) view.findViewById(R.id.iv_true);
        tv_caculation = (TextView) view.findViewById(R.id.calculation);
        mTimer = (CountDownTimerView) view.findViewById(R.id.countdown_timer);
        mTimer.init(TIMEOUT_MILLISECONDS, new CountDownTimerView.Command() {
            @Override
            public void execute() {
                gameFailure();
            }
        });

        mInstructionText = (TextView) view.findViewById(R.id.instruction_text);
        mInstructionText.setText(R.string.game_freaking_math_prompt);

        Logger.init(getActivity());
        Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_GAME_NONETWORK);
        Logger.track(userAction);
        initCaculation();
        iv_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrue == true) {
                    mTapsRemaining--;
                    isCorrect = true;
                } else {
                    mFailedsRemaining--;
                    isCorrect = false;
                }
                checkWin();
                initCaculation();
            }
        });
        iv_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrue == false) {
                    mTapsRemaining--;
                    isCorrect = true;
                } else {
                    mFailedsRemaining--;
                    isCorrect = false;
                }
                checkWin();
                initCaculation();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (GameFactory.GameResultListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mTimer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimer.stop();
    }

    protected void gameFailure() {
        String failureMessage = "";
        if (mFailedsRemaining == 0) {
            mTimer.stop();
            failureMessage = getString(R.string.game_lose_message);
        } else {
            failureMessage = getString(R.string.game_time_up_message);
        }
        Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_GAME_NONETWORK_TIMEOUT);
        Logger.track(userAction);
        mStateBanner.failure(failureMessage, new GameStateBanner.Command() {
            @Override
            public void execute() {
                mCallback.onGameFailure();
            }
        });
    }

    protected void gameSuccess() {
        mTimer.stop();
        String successMessage = getString(R.string.game_success_message);
        Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_GAME_NONETWORK_SUCCESS);
        Logger.track(userAction);
        mStateBanner.success(successMessage, new GameStateBanner.Command() {
            @Override
            public void execute() {
                mCallback.onGameSuccess(null);
            }
        });
    }

    private void initCaculation() {
        a = getRandomNumberInRange(1, 10);
        b = getRandomNumberInRange(1, 10);
        String cacul = "";
        switch (getRandomNumberInRange(1, 3)) {
            case 1:
                r_true = b + a;
                cacul = b + " + " + a;
                cal = b + " + " + a + " = " + r_true;
                break;
            case 2:
                r_true = b - a;
                cacul = b + " - " + a;
                cal = b + " - " + a + " = " + r_true;
                break;
            case 3:
                r_true = b * a;
                cacul = b + " x " + a;
                cal = b + " x " + a + " = " + r_true;
                break;
        }
        switch (getRandomNumberInRange(1, 2)) {
            case 1:
                cacul += "\n" + "= " + r_true;
                isTrue = true;
                break;
            case 2:
                cacul += "\n" + "= " + (r_true + getRandomNumberInRange(1, 5));
                isTrue = false;
                break;
        }
        tv_caculation.setText(cacul);


    }

    private void checkWin() {
        if (mTapsRemaining == 10) {
            mInstructionText.setBackgroundColor(getResources().getColor(R.color.green));
            mInstructionText.setText(cal + "\n" + getString(R.string.game_nonetwork_prompt2));
        } else if (mTapsRemaining == 5) {
            mInstructionText.setBackgroundColor(getResources().getColor(R.color.green));
            mInstructionText.setText(cal + "\n" + getString(R.string.game_nonetwork_prompt3));
        } else if (mTapsRemaining <= 0) {
            gameSuccess();
        } else if (mFailedsRemaining == 2 && !isCorrect) {
            mInstructionText.setBackgroundColor(getResources().getColor(R.color.red));
            mInstructionText.setText(cal + "\n" + getString(R.string.game_freaking_math_failed2));
        } else if (mFailedsRemaining == 1 && !isCorrect) {
            mInstructionText.setBackgroundColor(getResources().getColor(R.color.red));
            mInstructionText.setText(cal + "\n" + getString(R.string.game_freaking_math_failed1));
        } else if (mFailedsRemaining == 0) {
            gameFailure();
        } else {
            mInstructionText.setBackgroundColor(getResources().getColor(R.color.green));
            mInstructionText.setText(cal);
        }


    }

    private int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}