package zeus.minhquan.lifemanager.appcore;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import zeus.minhquan.lifemanager.R;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmListItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean mCanDismiss;

    public AlarmListItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mCanDismiss) {
            // Remove the item from the view
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        } else {
            // Reset the view back to its default visual state
            mAdapter.onItemDismissCancel(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            Resources resources = LifeManagerApplication.getAppContext().getResources();
            Bitmap icon = BitmapFactory.decodeResource(resources, R.drawable.delete_trash_can);
            int iconPadding = resources.getDimensionPixelOffset(R.dimen.alarm_list_delete_icon_padding);
            int maxDrawWidth = (iconPadding * 2) + icon.getWidth();

            Paint paint = new Paint();
            paint.setColor(ContextCompat.getColor(LifeManagerApplication.getAppContext(), R.color.red));

            int x = Math.round(Math.abs(dX));

            // Reset the dismiss flag if the view resets to its default position
            if (x == 0) {
                mCanDismiss = false;
            }

            // If we have travelled beyond the icon area via direct user interaction
            // we will dismiss when we get a swipe callback.  We do this to try to avoid
            // unwanted swipe dismissal
            if ((x > maxDrawWidth) && isCurrentlyActive) {
                mCanDismiss = true;
            }

            int drawWidth = Math.min(x, maxDrawWidth);
            // Cap the height of the drawable area to the selectable area - this improves the visual
            // for the first taller item in the alarm menu_todo_list
            int itemTop = itemView.getBottom() - resources.getDimensionPixelSize(R.dimen.alarm_list_item_height);

            if (dX > 0) {
                // Handle swiping to the right
                // Draw red background in area that we vacate up to maxDrawWidth
                canvas.drawRect((float) itemView.getLeft(),
                        (float) itemTop,
                        drawWidth,
                        (float) itemView.getBottom(),
                        paint);

                // Only draw icon when we've past the padding threshold
                if (x > iconPadding) {

                    Rect destRect = new Rect();
                    destRect.left = itemView.getLeft() + iconPadding;
                    destRect.top = itemTop + (itemView.getBottom() - itemTop - icon.getHeight()) / 2;
                    int maxRight = destRect.left + icon.getWidth();
                    destRect.right = Math.min(x, maxRight);
                    destRect.bottom = destRect.top + icon.getHeight();

                    // Only draw the appropriate parts of the bitmap as it is revealed
                    Rect srcRect = null;
                    if (x < maxRight) {
                        srcRect = new Rect();
                        srcRect.top = 0;
                        srcRect.left = 0;
                        srcRect.bottom = icon.getHeight();
                        srcRect.right = x - iconPadding;
                    }

                    canvas.drawBitmap(icon,
                            srcRect,
                            destRect,
                            paint);
                }

            } else {
                // Handle swiping to the left
                // Draw red background in area that we vacate  up to maxDrawWidth
                canvas.drawRect((float) itemView.getRight() - drawWidth,
                        (float) itemTop,
                        (float) itemView.getRight(),
                        (float) itemView.getBottom(), paint);

                // Only draw icon when we've past the padding threshold
                if (x > iconPadding) {
                    int fromLeftX = itemView.getRight() - x;
                    Rect destRect = new Rect();
                    destRect.right = itemView.getRight() - iconPadding;
                    destRect.top = itemTop + (itemView.getBottom() - itemTop - icon.getHeight()) / 2;
                    int maxFromLeft = destRect.right - icon.getWidth();
                    destRect.left = Math.max(fromLeftX, maxFromLeft);
                    destRect.bottom = destRect.top + icon.getHeight();

                    // Only draw the appropriate parts of the bitmap as it is revealed
                    Rect srcRect = null;
                    if (fromLeftX > maxFromLeft) {
                        srcRect = new Rect();
                        srcRect.top = 0;
                        srcRect.right = icon.getWidth();
                        srcRect.bottom = icon.getHeight();
                        srcRect.left = srcRect.right - (x - iconPadding);
                    }

                    canvas.drawBitmap(icon,
                            srcRect,
                            destRect,
                            paint);
                }
            }

            // Fade out the item as we swipe it
            float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
            itemView.setAlpha(alpha);
            itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    public interface ItemTouchHelperAdapter {
        void onItemDismiss(int position);

        void onItemDismissCancel(int position);
    }
}