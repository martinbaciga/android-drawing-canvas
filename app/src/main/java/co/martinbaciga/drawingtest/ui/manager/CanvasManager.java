package co.martinbaciga.drawingtest.ui.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.martinbaciga.drawingtest.domain.application.DrawingCanvasApplication;
import co.martinbaciga.drawingtest.domain.model.Board;
import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.infrastructure.FireBaseDBConstants;
import co.martinbaciga.drawingtest.infrastructure.manager.SharedPreferencesManager;
import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableImageView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.component.ManipulableView;
import co.martinbaciga.drawingtest.ui.interfaces.ManipulableViewEventListener;
import co.martinbaciga.drawingtest.ui.util.UiUtils;

public class CanvasManager
{	public static final String SHARED_PREFERENCES_KEY_MANIPULABLE_ENABLED = "SPManipulableEnabled";
	public static final int TARGET_PAINT = 1;
	public static final int TARGET_TEXT = 2;

	private static final float TEXT_SIZE = 5;
	private int mBackgroundColor = Color.WHITE;
	private ImageView mBackgroundImageView;

	private LayerManager mLayerManager;
	private CanvasMenuManager mCanvasMenuManager;
	private DrawingView mBaseDrawingView;
	private Context mContext;

	private Firebase mBoardRef;
	private Firebase mBackgroundRef;
	private Firebase mBackgroundImageRef;
	private Firebase mSegmentsRef;

	private Set<String> mOutstandingSegments = new HashSet<>();

	private boolean mManipulateEnabled = false;
	private String mManipulableViewEnabledId;

	public CanvasManager(Context context, FrameLayout root, ImageView backgroundImageView, DrawingView baseDrawingView, CanvasMenuManager canvasMenuManager)
	{
		mCanvasMenuManager = canvasMenuManager;
		mLayerManager = new LayerManager(context, root, baseDrawingView);
		mBackgroundImageView = backgroundImageView;
		mBaseDrawingView = baseDrawingView;
		mContext = context;

		initRefs();
		initCallbacks();
	}

	public void addTextComponent(String text)
	{
		Firebase segmentRef = mSegmentsRef.push();
		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		ManipulableTextView tv = mLayerManager.addTextComponent(text, TEXT_SIZE*mBaseDrawingView.getScale(), 200, 200, mEventLister, segmentId);
		mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();

		Segment segment = new Segment(Segment.TYPE_TEXT,
				tv.getX()/mBaseDrawingView.getScale(), tv.getY()/mBaseDrawingView.getScale(),
				tv.getLayoutParams().width/mBaseDrawingView.getScale(), tv.getLayoutParams().height/mBaseDrawingView.getScale(),
				tv.getText(), tv.getTextSize()/mBaseDrawingView.getScale(),
				tv.getTextColor(),
				Segment.TEXT_ALIGN_LEFT);

		segmentRef.setValue(segment, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});
	}

	public void addImageComponent(Bitmap bitmap)
	{
		//mLayerManager.addImageComponent(bitmap, mEventLister);
	}

	public void addImageComponent(String url)
	{
		Firebase segmentRef = mSegmentsRef.push();
		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		ManipulableImageView iv = mLayerManager.addImageComponent(url, mEventLister, segmentId);
		mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();

		Segment segment = new Segment(Segment.TYPE_IMAGE,
				iv.getX()/mBaseDrawingView.getScale(), iv.getY()/mBaseDrawingView.getScale(),
				iv.getLayoutParams().width/mBaseDrawingView.getScale(), iv.getLayoutParams().height/mBaseDrawingView.getScale(),
				url);

		segmentRef.setValue(segment, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});
	}

	public void changeTextColor(int color)
	{
		ManipulableTextView mtv = (ManipulableTextView) mLayerManager.getManipulableView(mManipulableViewEnabledId);
		mtv.setTextColor(color);

		final String segmentId = mtv.getSegmentId();
		mOutstandingSegments.add(segmentId);

		Map<String, Object> segment = new HashMap<>();
		segment.put(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_COLOR, String.valueOf(color));

		mSegmentsRef.child(segmentId).updateChildren(segment, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebase)
			{
				if (error != null)
				{
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});
	}

	public void changeTextAlign(int gravity, String sGravity)
	{
		ManipulableTextView mtv = (ManipulableTextView) mLayerManager.getManipulableView(mManipulableViewEnabledId);
		mtv.setTextGravity(gravity);

		final String segmentId = mtv.getSegmentId();
		mOutstandingSegments.add(segmentId);

		Map<String, Object> segment = new HashMap<>();
		segment.put(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_ALIGNMENT, String.valueOf(sGravity));

		mSegmentsRef.child(segmentId).updateChildren(segment, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebase)
			{
				if (error != null)
				{
					throw error.toException();
				}
				mOutstandingSegments.remove(segmentId);
			}
		});
	}

	public void changeManipulateState()
	{
		mManipulateEnabled = !mManipulateEnabled;
		SharedPreferencesManager.save(mContext, SHARED_PREFERENCES_KEY_MANIPULABLE_ENABLED, mManipulateEnabled);

		if (mManipulableViewEnabledId != null && !mManipulableViewEnabledId.matches(""))
		{
			mLayerManager.getManipulableView(mManipulableViewEnabledId).setControlItemsHidden(!mManipulateEnabled);
		} else
		{
			mLayerManager.getTopManipulableView().setControlItemsHidden(!mManipulateEnabled);
			mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();
		}

		if (mManipulateEnabled)
		{
			mLayerManager.disableTopDrawingView();

			if (mLayerManager.getManipulableView(mManipulableViewEnabledId).getClass() == ManipulableTextView.class)
			{
				mCanvasMenuManager.showTextOptions();
			}
		} else {
			mLayerManager.enableTopDrawingView();

			mCanvasMenuManager.showBaseOptions();
		}
	}

	public Bitmap getCanvasBitmap()
	{
		return UiUtils.getBitmapFromView(mLayerManager.getRoot());
	}

	public int getStrokeWidth()
	{
		return mLayerManager.getTopDrawingView().getStrokeWidth();
	}

	public void setStrokeWidth(int strokeWidth)
	{
		mLayerManager.getTopDrawingView().setStrokeWidth(strokeWidth);
	}

	public int getPaintColor()
	{
		return mLayerManager.getTopDrawingView().getPaintColor();
	}

	public void setPaintColor(int color)
	{
		mLayerManager.getTopDrawingView().setPaintColor(color);
	}

	public void setPaintOpacity(int opacity)
	{
		mLayerManager.getTopDrawingView().setOpacity(opacity);
	}

	public int getPaintOpacity()
	{
		return mLayerManager.getTopDrawingView().getOpacity();
	}

	public int getTextColor()
	{
		return ((ManipulableTextView)mLayerManager.getManipulableView(mManipulableViewEnabledId)).getTextColor();
	}

	public int getBackgroundColor()
	{
		return mBackgroundColor;
	}

	public void setBackgroundColor(int color)
	{
		mBackgroundColor = color;
		mLayerManager.getRoot().setBackgroundColor(color);
		saveBackgroundColorChange();
	}

	public void setBackgroundImage(String url)
	{
		changeBackgroundImage(url);
		saveBackgroundImageChange(url);
	}

	// TODO Refactor this
	public void clearCanvas()
	{
		mLayerManager.getTopDrawingView().clearCanvas();
	}

	public void showPaintOptions()
	{
		mCanvasMenuManager.showPaintOptions();
	}

	private void initRefs()
	{
		mBoardRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_BOARD);

		mBackgroundRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_BOARD).child(FireBaseDBConstants.FIREBASE_DB_BOARD_BACKGROUND);

		mBackgroundImageRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_BOARD).child(FireBaseDBConstants.FIREBASE_DB_BOARD_BACKGROUND_IMAGE);

		mSegmentsRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS);
	}

	private void initCallbacks()
	{
		mBoardRef.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				Board board = dataSnapshot.getValue(Board.class);

				if (board != null)
				{
					setBackgroundColor(board.getBackgroundColor());

					if (board.getBackgroundImageUrl() != null && !board.getBackgroundImageUrl().matches(""))
					{
						changeBackgroundImage(board.getBackgroundImageUrl());
					}
				} else
				{
					saveBoard();
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError)
			{
				// No-op
			}
		});

		mSegmentsRef.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				String segmentId = dataSnapshot.getKey();
				Segment segment = dataSnapshot.getValue(Segment.class);

				if (!mOutstandingSegments.contains(segmentId))
				{
					if (segment.getType().matches(Segment.TYPE_TEXT))
					{
						mLayerManager.addTextComponent(segment.getText(),
								segment.getTextSize() * mBaseDrawingView.getScale(),
								segment.getColor(),
								segment.getX() * mBaseDrawingView.getScale(), segment.getY() * mBaseDrawingView.getScale(),
								(int)(segment.getWidth() * mBaseDrawingView.getScale()),
								(int)(segment.getHeight() * mBaseDrawingView.getScale()),
								segment.getAlignment(),
								mEventLister, segmentId);

						mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();
					} else if (segment.getType().matches(Segment.TYPE_IMAGE))
					{
						mLayerManager.addImageComponent(segment.getUrl(),
								segment.getX() * mBaseDrawingView.getScale(), segment.getY() * mBaseDrawingView.getScale(),
								(int)(segment.getWidth() * mBaseDrawingView.getScale()),
								(int)(segment.getHeight() * mBaseDrawingView.getScale()),
								mEventLister, segmentId);

						mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();
					}
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s)
			{
				String segmentId = dataSnapshot.getKey();
				Segment segment = dataSnapshot.getValue(Segment.class);

				if (!mOutstandingSegments.contains(segmentId))
				{
					if (segment.getType().matches(Segment.TYPE_TEXT))
					{
						mLayerManager.updateTextComponent(segmentId,
								segment.getText(),
								segment.getX() * mBaseDrawingView.getScale(), segment.getY() * mBaseDrawingView.getScale(),
								segment.getTextSize() * mBaseDrawingView.getScale(),
								segment.getColor(),
								(int)(segment.getWidth() * mBaseDrawingView.getScale()),
								(int)(segment.getHeight() * mBaseDrawingView.getScale()),
								segment.getAlignment());
					} else if (segment.getType().matches(Segment.TYPE_IMAGE))
					{
						mLayerManager.updateImageComponent(segmentId,
								segment.getX() * mBaseDrawingView.getScale(),
								segment.getY() * mBaseDrawingView.getScale(),
								(int)(segment.getWidth() * mBaseDrawingView.getScale()),
								(int)(segment.getHeight() * mBaseDrawingView.getScale()));
					}
				}
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot)
			{
				String segmentId = dataSnapshot.getKey();
				Segment segment = dataSnapshot.getValue(Segment.class);

				if (!mOutstandingSegments.contains(segmentId))
				{
					mLayerManager.removeManipulableView(segmentId);
					if (mLayerManager.getTopManipulableView() != null)
					{
						mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();
					} else {
						mManipulableViewEnabledId = null;
					}
				}
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s)
			{

			}

			@Override
			public void onCancelled(FirebaseError firebaseError)
			{

			}
		});
	}

	private void saveBoard()
	{
		Board board = new Board(mBackgroundColor);

		mBoardRef.setValue(board, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
			}
		});
	}

	private ManipulableViewEventListener mEventLister = new ManipulableViewEventListener()
	{
		@Override
		public void onDragFinished(ManipulableView v)
		{
			final String segmentId = v.getSegmentId();
			mOutstandingSegments.add(segmentId);

			Map<String, Object> segment = new HashMap<>();
			segment.put(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_X, String.valueOf(v.getX() / mBaseDrawingView.getScale()));
			segment.put(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_Y, String.valueOf(v.getY() / mBaseDrawingView.getScale()));

			mSegmentsRef.child(segmentId).updateChildren(segment, new Firebase.CompletionListener()
			{
				@Override
				public void onComplete(FirebaseError error, Firebase firebase)
				{
					if (error != null)
					{
						throw error.toException();
					}
					mOutstandingSegments.remove(segmentId);
				}
			});
		}

		@Override
		public void onScaleFinished(ManipulableView v)
		{
			final String segmentId = v.getSegmentId();
			mOutstandingSegments.add(segmentId);

			Map<String, Object> segment = new HashMap<>();
			segment.put(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_WIDTH, String.valueOf(v.getWidth() / mBaseDrawingView.getScale()));
			segment.put(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_HEIGHT, String.valueOf(v.getHeight() / mBaseDrawingView.getScale()));

			mSegmentsRef.child(segmentId).updateChildren(segment, new Firebase.CompletionListener()
			{
				@Override
				public void onComplete(FirebaseError error, Firebase firebase)
				{
					if (error != null)
					{
						throw error.toException();
					}
					mOutstandingSegments.remove(segmentId);
				}
			});
		}

		@Override
		public void onDeleteClick(ManipulableView v)
		{
			final String segmentId = v.getSegmentId();
			mOutstandingSegments.add(segmentId);

			mLayerManager.removeManipulableView(segmentId);
			mManipulableViewEnabledId = mLayerManager.getTopManipulableView().getSegmentId();

			mSegmentsRef.child(segmentId).removeValue(new Firebase.CompletionListener()
			{
				@Override
				public void onComplete(FirebaseError error, Firebase firebase)
				{
					if (error != null)
					{
						throw error.toException();
					}
					mOutstandingSegments.remove(segmentId);
				}
			});
		}

		@Override
		public void onTap(ManipulableView v)
		{
			if (mManipulateEnabled && !mManipulableViewEnabledId.matches(v.getSegmentId()))
			{
				mLayerManager.getManipulableView(mManipulableViewEnabledId).setControlItemsHidden(true);
				mLayerManager.getManipulableView(v.getSegmentId()).setControlItemsHidden(false);
				mManipulableViewEnabledId = v.getSegmentId();
			}
		}
	};

	private void changeBackgroundImage(String url)
	{
		Glide.with(mContext).load(url).into(mBackgroundImageView);
	}

	private void saveBackgroundColorChange()
	{
		mBackgroundRef.setValue(mBackgroundColor, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
			}
		});
	}

	private void saveBackgroundImageChange(String url)
	{
		mBackgroundImageRef.setValue(url, new Firebase.CompletionListener()
		{
			@Override
			public void onComplete(FirebaseError error, Firebase firebaseRef)
			{
				if (error != null)
				{
					Log.e("AndroidDrawing", error.toString());
					throw error.toException();
				}
			}
		});
	}
}
