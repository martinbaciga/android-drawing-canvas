package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class StickerTextView extends StickerView
{
	private AutoResizeTextView mText;

	private static final int TEXT_COLOR = Color.BLACK;
	private static final int TEXT_GRAVITY = Gravity.LEFT;
	private static final float TEXT_SIZE = 50;

	public StickerTextView(Context context) {
		super(context);
	}

	public StickerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StickerTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public View getMainView()
	{
		if (mText != null)
		{
			return mText;
		}

		mText = new AutoResizeTextView(getContext());
		mText.setTextColor(TEXT_COLOR);
		mText.setGravity(TEXT_GRAVITY);
		mText.setTextSize(TEXT_SIZE);
		mText.setMinTextSize(TEXT_SIZE);
		mText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		mText.setSingleLine(false);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
		);
		params.gravity = Gravity.CENTER;
		mText.setLayoutParams(params);

		if(getImageViewFlip() != null)
		{
			getImageViewFlip().setVisibility(View.GONE);
		}

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

	public void setTextSize(float size)
	{
		mText.setTextSize(size);
		mText.setMinTextSize(size);
	}

	public void setTextColor(int color)
	{
		mText.setTextColor(color);
	}

	public static float pixelsToSp(Context context, float px)
	{
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px/scaledDensity;
	}

	@Override
	protected void onScaling(boolean scaleUp) {
		super.onScaling(scaleUp);
	}
}
