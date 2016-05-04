package co.martinbaciga.drawingtest.domain.model;

import android.graphics.Paint;

public class TextDrawingObject
{
	private String mText;
	private float mX;
	private float mY;
	private Paint mPaint;

	public TextDrawingObject(String mText, int mX, int mY, Paint mPaint)
	{
		this.mText = mText;
		this.mX = mX;
		this.mY = mY;
		this.mPaint = mPaint;
	}

	public String getText()
	{
		return mText;
	}

	public void setText(String mText)
	{
		this.mText = mText;
	}

	public float getX()
	{
		return mX;
	}

	public void setX(int mX)
	{
		this.mX = mX;
	}

	public float getY()
	{
		return mY;
	}

	public void setY(int mY)
	{
		this.mY = mY;
	}

	public Paint getPaint()
	{
		return mPaint;
	}

	public void setPaint(Paint mPaint)
	{
		this.mPaint = mPaint;
	}
}
