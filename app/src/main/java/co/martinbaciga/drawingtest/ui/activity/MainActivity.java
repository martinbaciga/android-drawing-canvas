package co.martinbaciga.drawingtest.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.martinbaciga.drawingtest.R;
import co.martinbaciga.drawingtest.domain.application.DrawingCanvasApplication;
import co.martinbaciga.drawingtest.domain.manager.FileManager;
import co.martinbaciga.drawingtest.domain.manager.PermissionManager;
import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.infrastructure.FireBaseDBConstants;
import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableImageView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.dialog.StrokeSelectorDialog;
import co.martinbaciga.drawingtest.ui.dialog.TextDialog;
import co.martinbaciga.drawingtest.ui.manager.LayerManager;
import co.martinbaciga.drawingtest.ui.util.UiUtils;

public class MainActivity extends AppCompatActivity
{
	@Bind(R.id.main_manipulate_iv) ImageView mManipulateImageView;
	@Bind(R.id.container) FrameLayout mContainer;
	@Bind(R.id.main_drawing_view) DrawingView mDrawingView;
	@Bind(R.id.main_text_iv) ImageView mTextImageView;
	@Bind(R.id.main_image_iv) ImageView mImageImageView;
	@Bind(R.id.main_fill_iv) ImageView mFillBackgroundImageView;
	@Bind(R.id.main_color_iv) ImageView mColorImageView;
	@Bind(R.id.main_stroke_iv) ImageView mStrokeImageView;
	@Bind(R.id.main_undo_iv) ImageView mUndoImageView;
	@Bind(R.id.main_redo_iv) ImageView mRedoImageView;

	private static final int MAX_STROKE_WIDTH = 10;

	//private ValueEventListener mConnectedListener;

	private Set<String> mOutstandingSegments = new HashSet<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		mDrawingView.setEnabled(false);

		Firebase ref = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS);

		ref.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s)
			{
				if (!mOutstandingSegments.contains(dataSnapshot.getKey()))
				{
					Segment segment = dataSnapshot.getValue(Segment.class);

					ManipulableTextView tv = new ManipulableTextView(MainActivity.this);
					tv.setText(segment.getText());
					tv.setControlItemsHidden(true);
					mContainer.addView(tv);

					tv.setLeft((int) segment.getX());
					tv.setRight((int) segment.getY());
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

	@Override
	protected void onStart()
	{
		super.onStart();
		/*mConnectedListener = DrawingCanvasApplication.getInstance()
				.getFirebaseRef().getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				boolean connected = (Boolean) dataSnapshot.getValue();
				if (connected) {
					Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
					if (mDrawingView != null)
					{
						mDrawingView.syncBoard();
					}
				} else {
					Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				// No-op
			}
		});*/
	}

	@Override
	public void onStop() {
		super.onStop();
		/*DrawingCanvasApplication.getInstance()
				.getFirebaseRef().getRoot().child(".info/connected").removeEventListener(mConnectedListener);
		if (mDrawingView != null) {
			mDrawingView.clearListeners();
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_share:
				requestPermissionsAndSaveBitmap();
				break;
			case R.id.action_clear:
				mDrawingView.clearCanvas();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void startFillBackgroundDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mDrawingView.getBackgroundColor(),
				5,
				ColorPickerDialog.SIZE_SMALL);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mDrawingView.setBackgroundColor(color);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startTextDialog()
	{
		TextDialog dialog = TextDialog.newInstance(null);

		dialog.setOnTextSettedListener(new TextDialog.OnTextSettedListener()
		{
			@Override
			public void onTextSetted(String text)
			{

			}
		});

		dialog.show(getSupportFragmentManager(), "TextDialog");
	}

	private void startColorPickerDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mDrawingView.getPaintColor(),
				5,
				ColorPickerDialog.SIZE_SMALL);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mDrawingView.setPaintColor(color);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startStrokeSelectorDialog()
	{
		StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mDrawingView.getStrokeWidth(), MAX_STROKE_WIDTH);

		dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener()
		{
			@Override
			public void onStrokeSelected(int stroke)
			{
				mDrawingView.setStrokeWidth(stroke);
			}
		});

		dialog.show(getSupportFragmentManager(), "StrokeSelectorDialog");
	}

	private void startShareDialog(Uri uri)
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/*");

		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(intent, "Share Image"));
	}

	private void requestPermissionsAndSaveBitmap()
	{
		if (PermissionManager.checkWriteStoragePermissions(this))
		{
			//Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap());
			Uri uri = FileManager.saveBitmap(UiUtils.getBitmapFromView(mContainer));
			startShareDialog(uri);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode)
		{
			case PermissionManager.REQUEST_WRITE_STORAGE:
			{
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					Uri uri = FileManager.saveBitmap(mDrawingView.getBitmap());
					startShareDialog(uri);
				} else
				{
					Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void addText()
	{
		ManipulableTextView tv = new ManipulableTextView(this);
		tv.setText("holaaaaaa");
		tv.setControlItemsHidden(true);
		mContainer.addView(tv);

		Firebase ref = DrawingCanvasApplication.getInstance().getFirebaseRef()
				.child(FireBaseDBConstants.FIREBASE_DB_TEST_MARTIN)
				.child(FireBaseDBConstants.FIREBASE_DB_SEGMENTS);

		Firebase segmentRef = ref.push();

		tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int[] pos = new int[2];
		tv.getLocationOnScreen(pos);
		Segment segment = new Segment(Segment.TYPE_TEXT, pos[0], pos[1], tv.getMeasuredWidth(), tv.getMeasuredHeight(), tv.getText());

		final String segmentId = segmentRef.getKey();
		mOutstandingSegments.add(segmentId);

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

	@OnClick(R.id.main_manipulate_iv)
	public void onManipulateOptionClick()
	{

	}

	@OnClick(R.id.main_text_iv)
	public void onTextOptionClick()
	{
		addText();
	}

	@OnClick(R.id.main_image_iv)
	public void onImageOptionClick()
	{

	}

	@OnClick(R.id.main_fill_iv)
	public void onBackgroundFillOptionClick()
	{
		startFillBackgroundDialog();
	}

	@OnClick(R.id.main_color_iv)
	public void onColorOptionClick()
	{
		startColorPickerDialog();
	}

	@OnClick(R.id.main_stroke_iv)
	public void onStrokeOptionClick()
	{
		startStrokeSelectorDialog();
	}

	@OnClick(R.id.main_undo_iv)
	public void onUndoOptionClick()
	{
		mDrawingView.undo();
	}

	@OnClick(R.id.main_redo_iv)
	public void onRedoOptionClick()
	{
		mDrawingView.redo();
	}
}
