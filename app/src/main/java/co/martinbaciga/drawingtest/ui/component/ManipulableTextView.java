package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import co.martinbaciga.drawingtest.R;

public class ManipulableTextView extends RelativeLayout
{
	private ScaleGestureDetector mScaleDetector;
	private int mActivePointerId = 1;
	private float mLastTouchX;
	private float mLastTouchY;
	private float mPosX;
	private float mPosY;

	public ManipulableTextView(Context context) {
		super(context);
		init(context);
	}

	public ManipulableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ManipulableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		LayoutInflater.from(context).inflate(R.layout.manipulable_text_view, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		//mScaleDetector.onTouchEvent(ev);

		final int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final float x = MotionEventCompat.getX(ev, pointerIndex);
				final float y = MotionEventCompat.getY(ev, pointerIndex);

				// Remember where we started (for dragging)
				mLastTouchX = x;
				mLastTouchY = y;
				// Save the ID of this pointer (for dragging)
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				// Find the index of the active pointer and fetch its position
				final int pointerIndex =
						MotionEventCompat.findPointerIndex(ev, mActivePointerId);

				final float x = MotionEventCompat.getX(ev, pointerIndex);
				final float y = MotionEventCompat.getY(ev, pointerIndex);

				// Calculate the distance moved
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				this.setX(mPosX);
				this.setY(mPosY);
				//invalidate();

				// Remember this touch position for the next move event
				mLastTouchX = x;
				mLastTouchY = y;

				break;
			}

			case MotionEvent.ACTION_UP: {
				mActivePointerId = 0;
				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				mActivePointerId = 0;
				break;
			}

			case MotionEvent.ACTION_POINTER_UP: {

				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

				if (pointerId == mActivePointerId) {
					// This was our active pointer going up. Choose a new
					// active pointer and adjust accordingly.
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
					mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
					mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
				}
				break;
			}
		}
		return true;
	}
}
