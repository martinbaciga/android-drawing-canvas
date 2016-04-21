package co.lateralview.drawingtest.ui.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import co.lateralview.drawingtest.R;
import co.lateralview.drawingtest.ui.component.DrawingView;
import co.lateralview.drawingtest.ui.dialog.StrokeSelectorDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private DrawingView mDrawingView;
	private TextView mBackTextView;
	private TextView mForwardTextView;

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
			case R.id.action_color:
				startColorPickerDialog();
				break;
			case R.id.action_stroke:
				startStrokeSelectorDialog();
				break;
			case R.id.action_clear:
				mDrawingView.clearCanvas();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void initControls()
	{
		mBackTextView = (TextView) findViewById(R.id.main_back);
		mBackTextView.setOnClickListener(this);
		mForwardTextView = (TextView) findViewById(R.id.main_forward);
		mForwardTextView.setOnClickListener(this);
	}

	private void initDrawingView()
	{
		mDrawingView = (DrawingView) findViewById(R.id.main_drawing_view);

		mCurrentColor = ContextCompat.getColor(this, R.color.flamingo);
		mCurrentStroke = 10;
		mDrawingView.setPaintColor(mCurrentColor);
		mDrawingView.setPaintStrokeWidth(mCurrentStroke);
	}

	private void startColorPickerDialog()
	{
		int[] colors = getResources().getIntArray(R.array.default_rainbow);

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

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.main_back:
				mDrawingView.goBack();
				break;
			case R.id.main_forward:
				mDrawingView.goForward();
				break;
		}
	}
}
