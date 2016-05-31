package co.martinbaciga.drawingtest.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;

import co.martinbaciga.drawingtest.R;

public class BackgroundDialog extends DialogFragment
{
	private static final String PARAM_COLOR = "Color";

	private int mColor;

	private AlertDialog mDialog;
	private ColorPicker mColorPicker;
	private Button mImageButton;

	private OnBackgroundSelectedListener mOnBackgroundSelectedListener;

	public static BackgroundDialog newInstance(int color) {
		BackgroundDialog f = new BackgroundDialog();
		Bundle args = new Bundle();
		args.putInt(PARAM_COLOR, color);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mColor = getArguments().getInt(PARAM_COLOR);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new  AlertDialog.Builder(getActivity())
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						}
				)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								dialog.dismiss();
							}
						}
				);

		LayoutInflater i = getActivity().getLayoutInflater();
		View view = i.inflate(R.layout.fragment_background_dialog, null);
		initControls(view);

		b.setView(view);

		mDialog = b.create();

		mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mOnBackgroundSelectedListener != null)
						{
							mOnBackgroundSelectedListener.onColorSelected(mColorPicker.getColor());
							mDialog.dismiss();
						}
					}
				});
			}
		});

		return mDialog;
	}

	private void initControls(View v)
	{
		mColorPicker = (ColorPicker) v.findViewById(R.id.fragment_dialog_color_color_picker);
		mColorPicker.setOldCenterColor(mColor);
		mColorPicker.setColor(mColor);
		mImageButton = (Button) v.findViewById(R.id.fragment_background_dialog_image_bt);
		mImageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DrawingCanvas/freedom-beach.jpg";
				mOnBackgroundSelectedListener.onImageSelected(filePath);
			}
		});
	}

	public void setOnColorSelectedListener(OnBackgroundSelectedListener listener)
	{
		mOnBackgroundSelectedListener = listener;
	}

	public interface OnBackgroundSelectedListener
	{
		public void onColorSelected(int color);
		public void onImageSelected(String uri);
	}
}
