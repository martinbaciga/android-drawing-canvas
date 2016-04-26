package co.lateralview.drawingtest.ui.activity;

import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import co.lateralview.drawingtest.R;
import co.lateralview.drawingtest.domain.manager.FileManager;
import co.lateralview.drawingtest.domain.manager.PermissionManager;
import co.lateralview.drawingtest.ui.component.DrawingView;
import co.lateralview.drawingtest.ui.dialog.StrokeSelectorDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private DrawingView mDrawingView;
	private ImageView mFillBackgroundImageView;
	private ImageView mColorImageView;
	private ImageView mStrokeImageView;
	private ImageView mUndoImageView;
	private ImageView mRedoImageView;

	private int mCurrentBackgroundColor;
	private int mCurrentColor;
	private int mCurrentStroke;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initControls();
		initDrawingView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_share:
				startShareDialog();
				break;
			case R.id.action_clear:
				mDrawingView.clearCanvas();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void initControls()
	{
		mFillBackgroundImageView = (ImageView) findViewById(R.id.main_fill_iv);
		mFillBackgroundImageView.setOnClickListener(this);
		mColorImageView = (ImageView) findViewById(R.id.main_color_iv);
		mColorImageView.setOnClickListener(this);
		mStrokeImageView = (ImageView) findViewById(R.id.main_stroke_iv);
		mStrokeImageView.setOnClickListener(this);
		mUndoImageView = (ImageView) findViewById(R.id.main_undo_iv);
		mUndoImageView.setOnClickListener(this);
		mRedoImageView = (ImageView) findViewById(R.id.main_redo_iv);
		mRedoImageView.setOnClickListener(this);
	}

	private void initDrawingView()
	{
		mDrawingView = (DrawingView) findViewById(R.id.main_drawing_view);

		mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
		mCurrentColor = ContextCompat.getColor(this, R.color.flamingo);
		mCurrentStroke = 10;

		mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
		mDrawingView.setPaintColor(mCurrentColor);
		mDrawingView.setPaintStrokeWidth(mCurrentStroke);
	}

	private void startFillBackgroundDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentBackgroundColor,
				5,
				ColorPickerDialog.SIZE_SMALL);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener()
		{

			@Override
			public void onColorSelected(int color)
			{
				mCurrentBackgroundColor = color;
				mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startColorPickerDialog()
	{
		int[] colors = getResources().getIntArray(R.array.palette);

		ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				colors,
				mCurrentColor,
				5,
				ColorPickerDialog.SIZE_SMALL);

		dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

			@Override
			public void onColorSelected(int color) {
				mCurrentColor = color;
				mDrawingView.setPaintColor(mCurrentColor);
			}

		});

		dialog.show(getFragmentManager(), "ColorPickerDialog");
	}

	private void startStrokeSelectorDialog()
	{
		StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, 30);

		dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener()
		{
			@Override
			public void onStrokeSelected(int stroke)
			{
				mCurrentStroke = stroke;
				mDrawingView.setPaintStrokeWidth(mCurrentStroke);
			}
		});

		dialog.show(getSupportFragmentManager(), "StrokeSelectorDialog");
	}

	private void startShareDialog()
	{
		if (PermissionManager.checkWriteStoragePermissions(this)) {
			FileManager.saveBitmap(mDrawingView.getBitmap());
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode)
		{
			case PermissionManager.REQUEST_WRITE_STORAGE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					FileManager.saveBitmap(mDrawingView.getBitmap());
				} else
				{
					Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.main_fill_iv:
				startFillBackgroundDialog();
				break;
			case R.id.main_color_iv:
				startColorPickerDialog();
				break;
			case R.id.main_stroke_iv:
				startStrokeSelectorDialog();
				break;
			case R.id.main_undo_iv:
				mDrawingView.undo();
				break;
			case R.id.main_redo_iv:
				mDrawingView.redo();
				break;
		}
	}
}
