package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import co.martinbaciga.drawingtest.R;

public class ManipulableTextView extends FrameLayout implements View.OnTouchListener
{
	private int mPosX;
	private int mPosY;
	private int mWidth;
	private int mHeight;

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
		this.setOnTouchListener(this);

		ImageView imageView = (ImageView) findViewById(R.id.text_resize_iv);
		imageView.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		final int x = (int) event.getRawX();
		final int y = (int) event.getRawY();

		if (v.getClass() == ManipulableTextView.class)
		{
			/*switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
					RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
							this.getLayoutParams();

					mPosX = x - lParams.leftMargin;
					mPosY = y - lParams.topMargin;
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_MOVE:
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
					layoutParams.leftMargin = x - mPosX;
					layoutParams.topMargin = y - mPosY;
					layoutParams.rightMargin = 0;
					layoutParams.bottomMargin = 0;
					this.setLayoutParams(layoutParams);
					break;
			}*/
		} else
		{
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
					mWidth = x - this.getWidth();
					mHeight = y - this.getHeight();
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_MOVE:
					FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
					layoutParams.width = x - mWidth;
					layoutParams.height = y - mHeight;
					this.setLayoutParams(layoutParams);
					break;
			}
		}

		invalidate();
		return true;
	}

}
