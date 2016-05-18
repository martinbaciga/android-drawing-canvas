package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
	public static final int PIXEL_SIZE = 3;
	private int mCanvasWidth = 2160;
	private int mCanvasHeight = 3552;
	private float mScale = 1.0f;

	private int mLastX;
	private int mLastY;

	private Path mDrawPath;
	private Paint mBackgroundPaint;
	private Paint mDrawPaint;
	private Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;

	private ArrayList<ExtendedPath> mExtendedPaths = new ArrayList<>();
	private ArrayList<ExtendedPath> mUndoneExtendedPaths = new ArrayList<>();

	// Set default values
	private int mBackgroundColor = Color.WHITE;
	private int mPaintColor = Color.BLACK;
	private int mStrokeWidth = 8;
	private boolean mEnabled = true;

	// Firebase
	private Firebase mBoardRef;
	private Firebase mBackgroundRef;
	private Firebase mSegmentsRef;
	private ValueEventListener mBoardListener;
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
		mBackgroundPaint = new Paint();
		mOutstandingSegments = new HashSet<>();
		initPaint();
		initFirebaseRefs();
		syncBoard();
	}

	private void initFirebaseRefs()
	{
		mBoardRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_BOARD);

		mSegmentsRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
			.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
			.child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS);

		mBackgroundRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
			.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
			.child(FireBaseDBConstants.FIREBASE_DB_BOARD).child(FireBaseDBConstants.FIREBASE_DB_BOARD_BACKGROUND);
	}

	public void syncBoard()
	{
		mBoardListener = mBoardRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Board board = dataSnapshot.getValue(Board.class);

				if (board != null)
				{
					setBackgroundColor(board.getBackgroundColor());
				} else
				{
					saveBoard();
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				// No-op
			}
		});

		mSegmentsListener = mSegmentsRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
				String name = dataSnapshot.getKey();

				if (!mOutstandingSegments.contains(name))
				{
					Segment segment = dataSnapshot.getValue(Segment.class);
					drawSegment(segment, createPaint(segment.getColor(), segment.getStrokeWidth()), dataSnapshot.getKey());
					invalidate();
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				// No-op
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				String name = dataSnapshot.getKey();

				if (!mOutstandingSegments.contains(name) && mExtendedPaths.size() > 0 && !mCleaningBoard)
				{
					eraseSegment(name);
					invalidate();
				}
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {
				// No-op
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				// No-op
			}
		});
	}

	private void saveBoard()
	{
		Board board = new Board(mBackgroundColor);

		mBoardRef.setValue(board, new Firebase.CompletionListener() {
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef) {
				if (error != null) {
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
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
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	private void drawBackground(Canvas canvas, float width, float height)
	{
		mBackgroundPaint.setColor(mBackgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		canvas.drawRect(0, 0, width, height, mBackgroundPaint);
	}

	private void drawPaths(Canvas canvas)
	{
		for (ExtendedPath ep : mExtendedPaths)
		{
			canvas.drawPath(ep.getPath(), ep.getPaint());
		}
	}

	private Paint createPaint(int color, int strokeWidth)
	{
		Paint p = new Paint();
		p.setColor(color);
		p.setAntiAlias(true);
		p.setDither(true);
		p.setStrokeWidth(strokeWidth * mScale);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);
		return p;
	}

	private void drawSegment(Segment segment, Paint paint, String segmentId) {
		/*if (mBuffer != null) {
			mBuffer.drawPath(getPathForPoints(segment.getPoints(), mScale), paint);
		}*/
		mExtendedPaths.add(new ExtendedPath(getPathFromPoints(segment.getPoints(), mScale), segment.getPoints(), paint, segmentId));
	}

	private Path getPathFromPoints(List<Point> points, double scale) {
		Path path = new Path();
		scale = scale * PIXEL_SIZE;
		Point current = points.get(0);
		path.moveTo(Math.round(scale * current.getX()), Math.round(scale * current.getY()));
		Point next = null;
		for (int i = 1; i < points.size(); ++i) {
			next = points.get(i);
			path.quadTo(
					Math.round(scale * current.getX()), Math.round(scale * current.getY()),
					Math.round(scale * (next.getX() + current.getX()) / 2), Math.round(scale * (next.getY() + current.getY()) / 2)
			);
			current = next;
		}
		if (next != null)
		{
			path.lineTo(Math.round(scale * next.getX()), Math.round(scale * next.getY()));
		} else if (current != null && next == null)
		{
			path.lineTo(Math.round(scale * current.getX()), Math.round(scale * current.getY()));
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
		drawBackground(canvas, mCanvasBitmap.getWidth(), mCanvasBitmap.getHeight());
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
		} else {
			return false;
		}
	}

	private void onTouchDown(float touchX, float touchY)
	{
		mDrawPath.moveTo(touchX, touchY);
		mLastX = (int) touchX / PIXEL_SIZE;
		mLastY = (int) touchY / PIXEL_SIZE;

		mCurrentSegment = new Segment(mPaintColor, mStrokeWidth);
		mCurrentSegment.addPoint(mLastX, mLastY);
	}

	private void onTouchMove(float touchX, float touchY)
	{
		int x = (int) touchX / PIXEL_SIZE;
		int y = (int) touchY / PIXEL_SIZE;

		float dx = Math.abs(x - mLastX);
		float dy = Math.abs(y - mLastY);
		if (dx >= 1 || dy >= 1) {
			mDrawPath.quadTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE, ((x + mLastX) * PIXEL_SIZE) / 2, ((y + mLastY) * PIXEL_SIZE) / 2);
			mLastX = x;
			mLastY = y;

			mCurrentSegment.addPoint(mLastX, mLastY);
		}
	}

	private void onTouchUp(float touchX, float touchY)
	{
		String segmentId = saveSegment();
		mDrawPath.lineTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE);
		mExtendedPaths.add(new ExtendedPath(mDrawPath, mCurrentSegment.getPoints(), mDrawPaint, segmentId));
		mDrawPath = new Path();
		initPaint();
	}

	private String saveSegment()
	{
		// scaled version of the segment, so that it matches the size of the board
		Segment segment = new Segment(mCurrentSegment.getColor(), mCurrentSegment.getStrokeWidth());
		for (Point point : mCurrentSegment.getPoints()) {
			segment.addPoint(Math.round(point.getX() / mScale), Math.round(point.getY() / mScale));
		}

		Firebase segmentRef = mSegmentsRef.push();

		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		segmentRef.setValue(segment, new Firebase.CompletionListener() {
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef) {
				if (error != null) {
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
		Segment segment = new Segment(extendedPath.getPaint().getColor(), (int)extendedPath.getPaint().getStrokeWidth());
		for (Point point : extendedPath.getPoints()) {
			segment.addPoint(Math.round(point.getX() / mScale), Math.round(point.getY() / mScale));
		}

		Firebase segmentRef = mSegmentsRef.push();

		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		segmentRef.setValue(segment, new Firebase.CompletionListener() {
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef) {
				if (error != null) {
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});

		return segmentId;
	}

	private void saveBackgroundColorChange()
	{
		mBackgroundRef.setValue(mBackgroundColor, new Firebase.CompletionListener() {
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef) {
				if (error != null) {
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
			}
		});
	}

	private void removeSegments()
	{
		mCleaningBoard = true;

		mSegmentsRef.removeValue(new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebase)
			{
				if (error != null) {
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
				if (error != null) {
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});
	}

	public void clearListeners()
	{
		mBoardRef.removeEventListener(mBoardListener);
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

	public void setBackgroundColor(int color)
	{
		mBackgroundColor = color;
		mBackgroundPaint.setColor(mBackgroundColor);
		invalidate();

		saveBackgroundColorChange();
	}

	public int getBackgroundColor()
	{
		return mBackgroundColor;
	}

	public Bitmap getBitmap()
	{
		drawBackground(mDrawCanvas, mCanvasBitmap.getWidth(), mCanvasBitmap.getHeight());
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
}
