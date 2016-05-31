package co.martinbaciga.drawingtest.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;

import co.martinbaciga.drawingtest.R;

public class OpacityDialog extends DialogFragment
{
	private static final String PARAM_COLOR = "Color";
	private static final String PARAM_OPACITY = "Opacity";

	private int mColor;
	private int mOpacity;

	private AlertDialog mDialog;
	private ColorPicker mColorPicker;
	private OpacityBar mOpacityBar;

	private OnOpacitySelectedListener mOnOpacitySelectedListener;

	public static OpacityDialog newInstance(int color, int opacity) {
		OpacityDialog f = new OpacityDialog();
		Bundle args = new Bundle();
		args.putInt(PARAM_COLOR, color);
		args.putInt(PARAM_OPACITY, opacity);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mColor = getArguments().getInt(PARAM_COLOR);
		mOpacity = getArguments().getInt(PARAM_OPACITY);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new  AlertDialog.Builder(getActivity())
				.setTitle("Select paint opacity")
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
		View view = i.inflate(R.layout.fragment_dialog_opacity, null);
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
						if (mOnOpacitySelectedListener != null)
						{
							mOnOpacitySelectedListener.onOpacitySelected(mOpacityBar.getOpacity());
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
		mColorPicker = (ColorPicker) v.findViewById(R.id.fragment_dialog_opacity_color_picker);
		mOpacityBar = (OpacityBar) v.findViewById(R.id.fragment_dialog_opacity_opacity_bar);
		mColorPicker.setOldCenterColor(mColor);
		mColorPicker.setColor(mColor);
		mColorPicker.setNewCenterColor(mColor);
		mOpacityBar.setColor(mColor);
		mColorPicker.addOpacityBar(mOpacityBar);
	}

	public void setOnOpacitySelectedListener(OnOpacitySelectedListener listener)
	{
		mOnOpacitySelectedListener = listener;
	}

	public interface OnOpacitySelectedListener
	{
		public void onOpacitySelected(int opacity);
	}
}