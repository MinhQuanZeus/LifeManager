package zeus.minhquan.lifemanager.games;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import zeus.minhquan.lifemanager.R;

/**
 * Created by QuanT on 4/22/2017.
 */

public class GameStateBanner extends TextView {
    private AnimatorSet mEnterLeftAnimation;
    private int mWidth;
    private int mSuccessColor, mFailureColor;

    public GameStateBanner(Context context) {
        this(context, null);
    }

    public GameStateBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public GameStateBanner(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSuccessColor = ContextCompat.getColor(context, R.color.green3);
        mFailureColor = ContextCompat.getColor(context, R.color.dark3);

        mEnterLeftAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.game_success_animator);
        mEnterLeftAnimation.setTarget(this);
    }

    public void success(String message, final Command onAnimationEnd){
        setBackgroundColor(mSuccessColor);
        animate(message, onAnimationEnd);

    }

    public void failure(String message, final Command onAnimationEnd){
        setBackgroundColor(mFailureColor);
        animate(message, onAnimationEnd);
    }

    private void animate(String message, final Command onAnimationEnd){
        setText(message);
        mEnterLeftAnimation.removeAllListeners();
        mEnterLeftAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
                bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onAnimationEnd != null) {
                    onAnimationEnd.execute();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mEnterLeftAnimation.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
    }

    // This is required for animation
    public void setXPercentage(float value){
        value /= 100f;
        setX((mWidth > 0) ? (value * mWidth) : 0);
    }

    public interface Command {
        void execute();
    }
}

