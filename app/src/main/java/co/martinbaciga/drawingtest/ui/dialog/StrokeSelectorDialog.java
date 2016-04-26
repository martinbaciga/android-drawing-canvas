package co.martinbaciga.drawingtest.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import co.martinbaciga.drawingtest.R;

public class StrokeSelectorDialog extends DialogFragment
{
	private static final String PARAM_CURRENT_STROKE = "CurrentStroke";
	private static final String PARAM_MAX_STROKE = "MaxStroke";

	private SeekBar mSeekBar;

	private int mCurrentStroke;
	private int mMaxStroke;
	private OnStrokeSelectedListener mOnStrokeSelectedListener;

	private AlertDialog mDialog;

	public static StrokeSelectorDialog newInstance(int currentStroke, int max) {
		StrokeSelectorDialog f = new StrokeSelectorDialog();
		Bundle args = new Bundle();
		args.putInt(PARAM_CURRENT_STROKE, currentStroke);
		args.putInt(PARAM_MAX_STROKE, max);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCurrentStroke = getArguments().getInt(PARAM_CURRENT_STROKE);
		mMaxStroke = getArguments().getInt(PARAM_MAX_STROKE);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new  AlertDialog.Builder(getActivity())
				.setTitle("Select stroke")
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
		View view = i.inflate(R.layout.fragment_dialog_stroke_selector, null);
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
						if (mOnStrokeSelectedListener != null)
						{
							mOnStrokeSelectedListener.onStrokeSelected(mSeekBar.getProgress());
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
		mSeekBar = (SeekBar) v.findViewById(R.id.fragment_dialog_stroke_selector_sb);
		mSeekBar.setMax(mMaxStroke);
		mSeekBar.setProgress(mCurrentStroke);
	}

	public void setOnStrokeSelectedListener(OnStrokeSelectedListener listener)
	{
		mOnStrokeSelectedListener = listener;
	}

	public interface OnStrokeSelectedListener
	{
		public void onStrokeSelected(int stroke);
	}
}
