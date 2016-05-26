package co.martinbaciga.drawingtest.ui.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.martinbaciga.drawingtest.domain.application.DrawingCanvasApplication;
import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.infrastructure.FireBaseDBConstants;
import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.component.ManipulableView;
import co.martinbaciga.drawingtest.ui.interfaces.ManipulableViewEventListener;
import co.martinbaciga.drawingtest.ui.util.UiUtils;

public class CanvasManager
{
	private static final float TEXT_SIZE = 5;

	private LayerManager mLayerManager;
	private DrawingView mBaseDrawingView;

	private Firebase mSegmentsRef;

	private Set<String> mOutstandingSegments = new HashSet<>();

	public CanvasManager(Context context, FrameLayout root, DrawingView baseDrawingView)
	{
		mLayerManager = new LayerManager(context, root, baseDrawingView);
		mBaseDrawingView = baseDrawingView;

		initRefs();
		initCallbacks();
	}

	public void addTextComponent(String text)
	{
		Firebase segmentRef = mSegmentsRef.push();
		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		ManipulableTextView tv = mLayerManager.addTextComponent(text, TEXT_SIZE*mBaseDrawingView.getScale(), 200, 200, mEventLister, segmentId);

		Segment segment = new Segment(Segment.TYPE_TEXT,
				tv.getX()/mBaseDrawingView.getScale(), tv.getY()/mBaseDrawingView.getScale(),
				tv.getWidth(), tv.getHeight(),
				tv.getText(), tv.getTextSize()/mBaseDrawingView.getScale());

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
		mLayerManager.addImageComponent(bitmap, mEventLister);
	}

	public void changeManipulateState()
	{
		mLayerManager.changeManipulateState();
	}

	public Bitmap getCanvasBitmap()
	{
		return UiUtils.getBitmapFromView(mLayerManager.getRoot());
	}

	private void initRefs()
	{
		mSegmentsRef = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS);
	}

	private void initCallbacks()
	{
		mSegmentsRef.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				String segmentId = dataSnapshot.getKey();
				Segment segment = dataSnapshot.getValue(Segment.class);

				if (!mOutstandingSegments.contains(segmentId) && segment.getType().matches(Segment.TYPE_TEXT))
				{
					if (segment.getType().matches(Segment.TYPE_TEXT))
					{
						mLayerManager.addTextComponent(segment.getText(),
								segment.getTextSize() * mBaseDrawingView.getScale(),
								segment.getX() * mBaseDrawingView.getScale(), segment.getY() * mBaseDrawingView.getScale(),
								mEventLister, segmentId);
					} else if (segment.getType().matches(Segment.TYPE_IMAGE))
					{

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
								(int)(segment.getWidth() * mBaseDrawingView.getScale()),
								(int)(segment.getHeight() * mBaseDrawingView.getScale()));
					} else if (segment.getType().matches(Segment.TYPE_IMAGE))
					{

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
					if (segment.getType().matches(Segment.TYPE_TEXT))
					{
						mLayerManager.removeTextComponent(segmentId);
					} else if (segment.getType().matches(Segment.TYPE_IMAGE))
					{

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

	private ManipulableViewEventListener mEventLister = new ManipulableViewEventListener()
	{
		@Override
		public void onDragFinished(ManipulableView v)
		{
			if (v.getClass() == ManipulableTextView.class)
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
		}

		@Override
		public void onScaleFinished(ManipulableView v)
		{
			if (v.getClass() == ManipulableTextView.class)
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
		}

		@Override
		public void onDeleteClick(ManipulableView v)
		{
			if (v.getClass() == ManipulableTextView.class)
			{
				final String segmentId = v.getSegmentId();
				mOutstandingSegments.add(segmentId);

				mLayerManager.removeTextComponent(segmentId);

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
		}
	};
}
