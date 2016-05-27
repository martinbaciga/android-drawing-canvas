package co.martinbaciga.drawingtest.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.larswerkman.holocolorpicker.ColorPicker;

import co.martinbaciga.drawingtest.R;

public class ColorDialog extends DialogFragment
{
	private static final String PARAM_COLOR = "Color";

	private int mColor;

	private AlertDialog mDialog;
	private ColorPicker mColorPicker;

	private OnColorSelectedListener mOnColorSelectedListener;

	public static ColorDialog newInstance(int color) {
		ColorDialog f = new ColorDialog();
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
				.setTitle("Add text")
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
		View view = i.inflate(R.layout.fragment_dialog_color, null);
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
						if (mOnColorSelectedListener != null)
						{
							mOnColorSelectedListener.onColorSelected(mColorPicker.getColor());
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
	}

	public void setOnColorSelectedListener(OnColorSelectedListener listener)
	{
		mOnColorSelectedListener = listener;
	}

	public interface OnColorSelectedListener
	{
		public void onColorSelected(int color);
	}
}
