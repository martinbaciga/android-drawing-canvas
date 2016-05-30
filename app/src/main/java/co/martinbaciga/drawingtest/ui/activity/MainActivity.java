package co.martinbaciga.drawingtest.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.martinbaciga.drawingtest.R;
import co.martinbaciga.drawingtest.domain.manager.FileManager;
import co.martinbaciga.drawingtest.domain.manager.PermissionManager;
import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableImageView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.dialog.ColorDialog;
import co.martinbaciga.drawingtest.ui.dialog.StrokeSelectorDialog;
import co.martinbaciga.drawingtest.ui.dialog.TextDialog;
import co.martinbaciga.drawingtest.ui.manager.CanvasManager;
import co.martinbaciga.drawingtest.ui.manager.CanvasMenuManager;
import co.martinbaciga.drawingtest.ui.manager.LayerManager;
import co.martinbaciga.drawingtest.ui.util.UiUtils;

public class MainActivity extends AppCompatActivity
{
	@Bind(R.id.container) FrameLayout mContainer;
	@Bind(R.id.main_drawing_view) DrawingView mDrawingView;

	@Bind(R.id.main_text_iv) ImageView mTextImageView;
	@Bind(R.id.main_text_font_iv) ImageView mTextFontImageView;
	@Bind(R.id.main_text_color_iv) ImageView mTextColorImageView;
	@Bind(R.id.main_text_align_left_iv) ImageView mTextAlignLeftImageView;
	@Bind(R.id.main_text_align_center_iv) ImageView mTextAlignCenterImageView;
	@Bind(R.id.main_text_align_right_iv) ImageView mTextAlignRightImageView;

	@Bind(R.id.main_paint_iv) ImageView mPaintImageView;
	@Bind(R.id.main_image_iv) ImageView mImageImageView;
	@Bind(R.id.main_fill_iv) ImageView mFillBackgroundImageView;
	@Bind(R.id.main_color_iv) ImageView mColorImageView;
	@Bind(R.id.main_stroke_iv) ImageView mStrokeImageView;
	@Bind(R.id.main_manipulate_iv) ImageView mManipulateImageView;
	@Bind(R.id.main_undo_iv) ImageView mUndoImageView;

	private static final int MAX_STROKE_WIDTH = 10;

	private CanvasManager mCanvasManager;

	//private ValueEventListener mConnectedListener;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		CanvasMenuManager canvasMenuManager = new CanvasMenuManager(
				mTextImageView, mTextFontImageView, mTextColorImageView, mTextAlignLeftImageView, mTextAlignCenterImageView, mTextAlignRightImageView,
				mPaintImageView, mImageImageView, mFillBackgroundImageView, mColorImageView, mStrokeImageView, mManipulateImageView, mUndoImageView
		);

		mCanvasManager = new CanvasManager(this, mContainer, mDrawingView, canvasMenuManager);

		/*mDrawingView.setEnabled(false);

		ManipulableImageView iv_sticker = new ManipulableImageView(MainActivity.this);
		iv_sticker.setImageDrawable(getResources().getDrawable(R.drawable.messi));
		mContainer.addView(iv_sticker);

		DrawingView drawingView = new DrawingView(this, true);
		mContainer.addView(drawingView);
		drawingView.setBackgroundColor(Color.TRANSPARENT);
		drawingView.setEnabled(true);*/
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
				mCanvasManager.addTextComponent(text);
			}
		});

		dialog.show(getSupportFragmentManager(), "TextDialog");
	}

	private void startColorPickerDialog()
	{
		ColorDialog dialog = ColorDialog.newInstance(mDrawingView.getPaintColor());

		dialog.setOnColorSelectedListener(new ColorDialog.OnColorSelectedListener()
		{
			@Override
			public void onColorSelected(int color)
			{
				mDrawingView.setPaintColor(color);
			}
		});

		dialog.show(getSupportFragmentManager(), "ColorDialog");
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
			Uri uri = FileManager.saveBitmap(mCanvasManager.getCanvasBitmap());
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

	@OnClick(R.id.main_manipulate_iv)
	public void onManipulateOptionClick()
	{
		mCanvasManager.changeManipulateState();
	}

	@OnClick(R.id.main_text_iv)
	public void onTextOptionClick()
	{
		startTextDialog();
	}

	@OnClick(R.id.main_text_align_left_iv)
	public void onTextAlignLeftClick()
	{
		mCanvasManager.changeTextAlign(Gravity.LEFT, Segment.TEXT_ALIGN_LEFT);
	}

	@OnClick(R.id.main_text_align_center_iv)
	public void onTextAlignCenterClick()
	{
		mCanvasManager.changeTextAlign(Gravity.CENTER_HORIZONTAL, Segment.TEXT_ALIGN_CENTER);
	}

	@OnClick(R.id.main_text_align_right_iv)
	public void onTextAlignRightClick()
	{
		mCanvasManager.changeTextAlign(Gravity.RIGHT, Segment.TEXT_ALIGN_RIGHT);
	}

	@OnClick(R.id.main_image_iv)
	public void onImageOptionClick()
	{
		/*Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.messi);
		mCanvasManager.addImageComponent(image);*/
		mCanvasManager.addImageComponent("http://vignette2.wikia.nocookie.net/simpsons/images/d/df/Maggie-Simpson-icon.png/revision/latest?cb=20140817104831");
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
}
