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

import java.util.HashSet;
import java.util.Set;

import co.martinbaciga.drawingtest.domain.application.DrawingCanvasApplication;
import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.infrastructure.FireBaseDBConstants;
import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.component.ManipulableView;
import co.martinbaciga.drawingtest.ui.interfaces.ManipulableViewEventListener;

public class CanvasManager
{
	private LayerManager mLayerManager;

	private Firebase mSegmentsRef;

	private Set<String> mOutstandingSegments = new HashSet<>();

	public CanvasManager(Context context, FrameLayout root, DrawingView baseDrawingView)
	{
		mLayerManager = new LayerManager(context, root, baseDrawingView);

		initRefs();
		initCallbacks();
	}

	public void addTextComponent(String text)
	{
		Firebase segmentRef = mSegmentsRef.push();
		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

		ManipulableTextView tv = mLayerManager.addTextComponent(text, 200, 200, mEventLister, segmentId);

		Segment segment = new Segment(Segment.TYPE_TEXT, tv.getX(), tv.getY(), tv.getMeasuredWidth(), tv.getMeasuredHeight(), tv.getText());

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
					mLayerManager.addTextComponent(segment.getText(), (int) segment.getX(), (int) segment.getY(), mEventLister, segmentId);
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s)
			{

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot)
			{

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
			mSegmentsRef.child(v.getSegmentId()).child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_X).setValue(v.getX());
			mSegmentsRef.child(v.getSegmentId()).child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS_Y).setValue(v.getY());
		}
	};
}
