package co.lateralview.drawingtest.ui.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View
{
	private Path mDrawPath;

	private Paint mCanvasPaint;

	private Paint mDrawPaint;

	private int mPaintColor = 0xFF660000;

	private int mStrokeWidth = 10;

	private Canvas mDrawCanvas;

	private Bitmap mCanvasBitmap;

	private ArrayList<Path> mPaths = new ArrayList<>();
	private ArrayList<Path> mUndonePaths = new ArrayList<>();

	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		mDrawPath = new Path();
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

		mCanvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	public void clearCanvas()
	{
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
	}

	public void setPaintColor(int color)
	{
		mPaintColor = color;
		mDrawPaint.setColor(mPaintColor);
	}

	public void setPaintStrokeWidth(int strokeWidth)
	{
		mStrokeWidth = strokeWidth;
		mDrawPaint.setStrokeWidth(mStrokeWidth);
	}

	public void setBackgroundColor(Activity activity, int color)
	{
		View view = activity.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	public Bitmap getBitmap()
	{
		return mCanvasBitmap;
	}

	public void undo()
	{
		if (mPaths.size() > 0)
		{
			mUndonePaths.add(mPaths.remove(mPaths.size()-1));
			invalidate();
		}
	}

	public void redo()
	{
		if (mUndonePaths.size() > 0)
		{
			mPaths.add(mUndonePaths.remove(mUndonePaths.size()-1));
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
		canvas.drawPath(mDrawPath, mDrawPaint);

		/*for (Path p : mPaths)
		{
			canvas.drawPath(p, mDrawPaint);
		}*/
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		mDrawCanvas = new Canvas(mCanvasBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float touchX = event.getX();
		float touchY = event.getY();

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mDrawPath.moveTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				mDrawPath.lineTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				mDrawPath.lineTo(touchX, touchY);
				mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
				mPaths.add(mDrawPath);
				mDrawPath.reset();
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}
}
