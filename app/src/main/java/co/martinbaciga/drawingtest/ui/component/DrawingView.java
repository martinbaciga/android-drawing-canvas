package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import co.martinbaciga.drawingtest.domain.model.TextDrawingObject;

public class DrawingView extends View
{
	private Path mDrawPath;
	private Paint mBackgroundPaint;
	private Paint mDrawPaint;
	private Paint mTextPaint;
	private Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;

	private ArrayList<Path> mPaths = new ArrayList<>();
	private ArrayList<Paint> mPaints = new ArrayList<>();
	private ArrayList<Path> mUndonePaths = new ArrayList<>();
	private ArrayList<Paint> mUndonePaints = new ArrayList<>();
	private ArrayList<TextDrawingObject> mTextDrawingObjects = new ArrayList<>();

	// Set default values
	private int mBackgroundColor = Color.WHITE;
	private int mPaintColor = Color.BLACK;
	private int mStrokeWidth = 10;

	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		mDrawPath = new Path();
		mBackgroundPaint = new Paint();
		initPaint();
	}

	private void initPaint()
	{
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(30);
	}

	private void drawBackground(Canvas canvas)
	{
		mBackgroundPaint.setColor(mBackgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mBackgroundPaint);
	}

	private void drawPaths(Canvas canvas)
	{
		int i = 0;
		for (Path p : mPaths)
		{
			canvas.drawPath(p, mPaints.get(i));
			i++;
		}
	}

	private void drawTexts(Canvas canvas)
	{
		for (TextDrawingObject tdo : mTextDrawingObjects)
		{
			canvas.drawText(tdo.getText(), tdo.getX(), tdo.getY(), tdo.getPaint());
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		drawBackground(canvas);
		drawPaths(canvas);
		drawTexts(canvas);

		canvas.drawPath(mDrawPath, mDrawPaint);
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
				//mDrawPath.addCircle(touchX, touchY, mStrokeWidth/10, Path.Direction.CW);
				break;
			case MotionEvent.ACTION_MOVE:
				mDrawPath.lineTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				mDrawPath.lineTo(touchX, touchY);
				mPaths.add(mDrawPath);
				mPaints.add(mDrawPaint);
				mDrawPath = new Path();
				initPaint();
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}

	public void clearCanvas()
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mUndonePaints.clear();
		mTextDrawingObjects.clear();
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

	public void setBackgroundColor(int color)
	{
		mBackgroundColor = color;
		mBackgroundPaint.setColor(mBackgroundColor);
		invalidate();
	}

	public Bitmap getBitmap()
	{
		drawBackground(mDrawCanvas);
		drawPaths(mDrawCanvas);
		return mCanvasBitmap;
	}

	public void undo()
	{
		if (mPaths.size() > 0)
		{
			mUndonePaths.add(mPaths.remove(mPaths.size() - 1));
			mUndonePaints.add(mPaints.remove(mPaints.size() - 1));
			invalidate();
		}
	}

	public void redo()
	{
		if (mUndonePaths.size() > 0)
		{
			mPaths.add(mUndonePaths.remove(mUndonePaths.size() - 1));
			mPaints.add(mUndonePaints.remove(mUndonePaints.size() - 1));
			invalidate();
		}
	}
}
