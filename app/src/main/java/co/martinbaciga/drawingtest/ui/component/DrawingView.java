package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.martinbaciga.drawingtest.domain.application.DrawingCanvasApplication;
import co.martinbaciga.drawingtest.domain.model.Board;
import co.martinbaciga.drawingtest.domain.model.ExtendedPath;
import co.martinbaciga.drawingtest.domain.model.Point;
import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.infrastructure.FireBaseDBConstants;

public class DrawingView extends View
{
	public static final int PIXEL_SIZE = 1;
	private int mCanvasWidth = 74;
	private int mCanvasHeight = 105;
	private float mScale = 1.0f;

	private int mLastX;
	private int mLastY;

	private Path mDrawPath;
	private Paint mDrawPaint;
	private Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;

	private ArrayList<ExtendedPath> mExtendedPaths = new ArrayList<>();
	private ArrayList<ExtendedPath> mUndoneExtendedPaths = new ArrayList<>();

	// Set default values
	private int mPaintColor = Color.BLACK;
	private int mStrokeWidth = 2;
	private int mOpacity = 255;
	private boolean mEnabled = true;

	// Firebase
	private Firebase mSegmentsRef;
	private ChildEventListener mSegmentsListener;
	private Segment mCurrentSegment;
	private Set<String> mOutstandingSegments;
	private boolean mCleaningBoard = false;

	public DrawingView(Context context)
	{
		super(context);
		init();
	}

	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		mDrawPath = new Path();
		mOutstandingSegments = new HashSet<>();
		initPaint();
		initFirebaseRefs();

		syncBoard();
	}

	private void initFirebaseRefs()
	{
		mSegmentsRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS);
	}

	public void syncBoard()
	{
		mSegmentsListener = mSegmentsRef.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName)
			{
				String name = dataSnapshot.getKey();
				Segment segment = dataSnapshot.getValue(Segment.class);

				if (!mOutstandingSegments.contains(name) && segment.getType().matches(Segment.TYPE_LINE) && mEnabled)
				{
					drawSegment(segment, createPaint(segment.getColor(), segment.getStrokeWidth(), segment.getOpacity()), dataSnapshot.getKey());
					invalidate();
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s)
			{
				// No-op
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot)
			{
				String name = dataSnapshot.getKey();

				if (!mOutstandingSegments.contains(name) && mExtendedPaths.size() > 0 && !mCleaningBoard && mEnabled)
				{
					eraseSegment(name);
					invalidate();
				}
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s)
			{
				// No-op
			}

			@Override
			public void onCancelled(FirebaseError firebaseError)
			{
				// No-op
			}
		});
	}

	private void initPaint()
	{
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setDither(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth * mScale);
		mDrawPaint.setAlpha(mOpacity);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	private void drawPaths(Canvas canvas)
	{
		for (ExtendedPath ep : mExtendedPaths)
		{
			canvas.drawPath(ep.getPath(), ep.getPaint());
		}
	}

	private Paint createPaint(int color, int strokeWidth, int opacity)
	{
		Paint p = new Paint();
		p.setColor(color);
		p.setAntiAlias(true);
		p.setDither(true);
		p.setStrokeWidth(strokeWidth * mScale);
		p.setAlpha(opacity);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);
		return p;
	}

	private void drawSegment(Segment segment, Paint paint, String segmentId)
	{
		/*if (mBuffer != null) {
			mBuffer.drawPath(getPathForPoints(segment.getPoints(), mScale), paint);
		}*/
		mExtendedPaths.add(new ExtendedPath(getPathFromPoints(segment.getPoints(), mScale), segment.getPoints(), paint, segmentId));
	}

	private Path getPathFromPoints(List<Point> points, float scale)
	{
		Path path = new Path();
		scale = scale * PIXEL_SIZE;
		Point current = points.get(0);
		path.moveTo(scale * current.getX(), scale * current.getY());
		Point next = null;
		for (int i = 1; i < points.size(); ++i)
		{
			next = points.get(i);
			path.quadTo(
					scale * current.getX(), scale * current.getY(),
					scale * ((next.getX() + current.getX()) / 2), scale * ((next.getY() + current.getY()) / 2));
			current = next;
		}
		if (next != null)
		{
			path.lineTo(scale * next.getX(), scale * next.getY());
		} else if (current != null && next == null)
		{
			path.lineTo(scale * current.getX(), scale * current.getY());
		}
		return path;
	}

	private void eraseSegment(String segmentId)
	{
		// Used regular for iteration to prevent java.util.ConcurrentModificationException
		for (int i = 0; i < mExtendedPaths.size(); i++)
		{
			if (mExtendedPaths.get(i).getSegmentId().matches(segmentId))
			{
				mExtendedPaths.remove(mExtendedPaths.get(i));
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		drawPaths(canvas);

		canvas.drawPath(mDrawPath, mDrawPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mScale = Math.min(1.0f * w / mCanvasWidth, 1.0f * h / mCanvasHeight);

		if (mDrawPaint != null)
		{
			mDrawPaint.setStrokeWidth(mStrokeWidth * mScale);
		}

		mCanvasBitmap = Bitmap.createBitmap(Math.round(mCanvasWidth * mScale), Math.round(mCanvasHeight * mScale), Bitmap.Config.ARGB_8888);

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
		params.width = Math.round(mCanvasWidth * mScale);
		params.height = Math.round(mCanvasHeight * mScale);
		this.setLayoutParams(params);

		mDrawCanvas = new Canvas(mCanvasBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (mEnabled)
		{
			float touchX = event.getX();
			float touchY = event.getY();

			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					onTouchDown(touchX, touchY);
					break;
				case MotionEvent.ACTION_MOVE:
					onTouchMove(touchX, touchY);
					break;
				case MotionEvent.ACTION_UP:
					onTouchUp(touchX, touchY);
					break;
				default:
					return false;
			}

			invalidate();
			return true;
		} else
		{
			return false;
		}
	}

	private void onTouchDown(float touchX, float touchY)
	{
		mDrawPath.moveTo(touchX, touchY);
		mLastX = (int) touchX / PIXEL_SIZE;
		mLastY = (int) touchY / PIXEL_SIZE;

		mCurrentSegment = new Segment(Segment.TYPE_LINE, mPaintColor, mStrokeWidth, mOpacity);
		mCurrentSegment.addPoint(mLastX, mLastY);
	}

	private void onTouchMove(float touchX, float touchY)
	{
		int x = (int) touchX / PIXEL_SIZE;
		int y = (int) touchY / PIXEL_SIZE;

		float dx = Math.abs(x - mLastX);
		float dy = Math.abs(y - mLastY);
		if (dx >= 1 || dy >= 1)
		{
			mDrawPath.quadTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE, ((x + mLastX) * PIXEL_SIZE) / 2, ((y + mLastY) * PIXEL_SIZE) / 2);
			mLastX = x;
			mLastY = y;

			mCurrentSegment.addPoint(mLastX, mLastY);
		}
	}

	private void onTouchUp(float touchX, float touchY)
	{
		String segmentId = "";
		if (mEnabled)
		{
			segmentId = saveSegment();
		}
		mDrawPath.lineTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE);
		mExtendedPaths.add(new ExtendedPath(mDrawPath, mCurrentSegment.getPoints(), mDrawPaint, segmentId));
		mDrawPath = new Path();
		initPaint();
	}

	private String saveSegment()
	{
		// scaled version of the segment, so that it matches the size of the board
		Segment segment = new Segment(Segment.TYPE_LINE, mCurrentSegment.getColor(), mCurrentSegment.getStrokeWidth(), mCurrentSegment.getOpacity());
		for (Point point : mCurrentSegment.getPoints())
		{
			segment.addPoint(point.getX() / mScale, point.getY() / mScale);
		}

		Firebase segmentRef = mSegmentsRef.push();

		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		segmentRef.setValue(segment, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});

		return segmentId;
	}

	private String saveSegment(ExtendedPath extendedPath)
	{
		Segment segment = new Segment(Segment.TYPE_LINE, extendedPath.getPaint().getColor(), (int) extendedPath.getPaint().getStrokeWidth(), extendedPath.getPaint().getAlpha());
		for (Point point : extendedPath.getPoints())
		{
			segment.addPoint(point.getX() / mScale, point.getY() / mScale);
		}

		Firebase segmentRef = mSegmentsRef.push();

		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		segmentRef.setValue(segment, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});

		return segmentId;
	}

	private void removeSegments()
	{
		mCleaningBoard = true;

		mSegmentsRef.removeValue(new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebase)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
				mCleaningBoard = false;
			}
		});
	}

	private void removeSegment(final String segmentId)
	{
		mOutstandingSegments.add(segmentId);

		mSegmentsRef.child(segmentId).removeValue(new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebase)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});
	}

	public void clearListeners()
	{
		mSegmentsRef.removeEventListener(mSegmentsListener);
	}

	public void clearCanvas()
	{
		mExtendedPaths.clear();
		mUndoneExtendedPaths.clear();
		mOutstandingSegments.clear();
		mCurrentSegment = null;
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();

		removeSegments();
	}

	public void setPaintColor(int color)
	{
		mPaintColor = color;
		mDrawPaint.setColor(mPaintColor);
	}

	public int getPaintColor()
	{
		return mPaintColor;
	}

	public void setStrokeWidth(int strokeWidth)
	{
		mStrokeWidth = strokeWidth;
		mDrawPaint.setStrokeWidth(mStrokeWidth * mScale);
	}

	public int getStrokeWidth()
	{
		return mStrokeWidth;
	}

	public void setOpacity(int opacity)
	{
		mOpacity = opacity;
		mDrawPaint.setAlpha(mOpacity);
	}

	public int getOpacity()
	{
		return mOpacity;
	}

	public Bitmap getBitmap()
	{
		drawPaths(mDrawCanvas);
		return mCanvasBitmap;
	}

	public void undo()
	{
		if (mExtendedPaths.size() > 0)
		{
			removeSegment(mExtendedPaths.get(mExtendedPaths.size() - 1).getSegmentId());

			mUndoneExtendedPaths.add(mExtendedPaths.remove(mExtendedPaths.size() - 1));
			invalidate();
		}
	}

	public void redo()
	{
		if (mUndoneExtendedPaths.size() > 0)
		{
			String newSegmentId = saveSegment(mUndoneExtendedPaths.get(mUndoneExtendedPaths.size() - 1));
			mUndoneExtendedPaths.get(mUndoneExtendedPaths.size() - 1).setSegmentId(newSegmentId);

			mExtendedPaths.add(mUndoneExtendedPaths.remove(mUndoneExtendedPaths.size() - 1));
			invalidate();
		}
	}

	public void setEnabled(boolean enabled)
	{
		mEnabled = enabled;
	}

	public boolean isEmpty()
	{
		return mExtendedPaths.size() == 0;
	}

	public float getScale()
	{
		return mScale;
	}
}
