package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import co.martinbaciga.drawingtest.ui.interfaces.ManipulableViewEventListener;

public class ManipulableTextView extends ManipulableView
{
	private AutoResizeTextView mText;
	private float mTextSize;

	private static final int TEXT_COLOR = Color.BLACK;
	private static final int TEXT_GRAVITY = Gravity.LEFT;
	private static final float TEXT_SIZE = 50;

	public ManipulableTextView(Context context, ManipulableViewEventListener listener) {
		super(context, listener);
	}

	public ManipulableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ManipulableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public View getMainView()
	{
		if (mText != null)
		{
			return mText;
		}

		mTextSize = TEXT_SIZE;

		mText = new AutoResizeTextView(getContext());
		mText.setTextColor(TEXT_COLOR);
		mText.setGravity(TEXT_GRAVITY);
		mText.setTextSize(TEXT_SIZE);
		mText.setMinTextSize(TEXT_SIZE);
		mText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		mText.setSingleLine(false);

		/*ypeface font = Typeface.createFromAsset(mText.getContext().getAssets(), "fonts/Disney.ttf");
		mText.setTypeface(font);*/

		return mText;
	}

	public void setText(String text)
	{
		if (mText != null)
		{
			mText.setText(text);
		}
	}

	public String getText()
	{
		if (mText != null)
		{
			return mText.getText().toString();
		}

		return null;
	}

	public void setShadow(float radius, int color)
	{
		mText.setShadowLayer(radius, 0, 0, color);
	}

	public void setShadow(float radius, float dx, float dy, int color)
	{
		mText.setShadowLayer(radius, dx, dy, color);
	}

	public void setTextColor(int color)
	{
		mText.setTextColor(color);
	}

	public int getTextColor()
	{
		return mText.getCurrentTextColor();
	}

	public static float pixelsToSp(Context context, float px)
	{
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px/scaledDensity;
	}

	public float getTextSize()
	{
		return mTextSize;
	}

	public void setTextSize(float size)
	{
		mText.setTextSize(size);
		mText.setMinTextSize(size);
		mTextSize = size;
	}

	public void setTextGravity(int gravity)
	{
		mText.setGravity(gravity);
	}
}
