package co.martinbaciga.drawingtest.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.martinbaciga.drawingtest.R;

public class TextDialog extends DialogFragment
{
	private static final String PARAM_TEXT = "Text";

	private EditText mEditText;

	private String mText;

	private AlertDialog mDialog;

	private OnTextSettedListener mOnTextSettedListener;

	public static TextDialog newInstance(@Nullable String text) {
		TextDialog f = new TextDialog();
		Bundle args = new Bundle();
		args.putString(PARAM_TEXT, text);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mText = getArguments().getString(PARAM_TEXT);
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
		View view = i.inflate(R.layout.fragment_dialog_text, null);
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
						if (mOnTextSettedListener != null)
						{
							mOnTextSettedListener.onTextSetted(mEditText.getText().toString());
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
		mEditText = (EditText) v.findViewById(R.id.fragment_dialog_text_et);
		if (mText != null && !mText.isEmpty())
		{
			mEditText.setText(mText);
		}
	}

	public void setOnTextSettedListener(OnTextSettedListener listener)
	{
		mOnTextSettedListener = listener;
	}

	public interface OnTextSettedListener
	{
		public void onTextSetted(String text);
	}
}
