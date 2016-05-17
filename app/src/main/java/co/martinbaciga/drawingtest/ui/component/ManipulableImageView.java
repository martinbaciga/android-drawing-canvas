package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ManipulableImageView extends ManipulableView
{
	private String mOwnerId;
	private ImageView mImageView;

	public ManipulableImageView(Context context)
	{
		super(context);
	}

	public ManipulableImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ManipulableImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void setOwnerId(String owner_id)
	{
		this.mOwnerId = owner_id;
	}

	public String getOwnerId()
	{
		return this.mOwnerId;
	}

	@Override
	public View getMainView()
	{
		if (this.mImageView == null)
		{
			this.mImageView = new ImageView(getContext());
			this.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		}
		return mImageView;
	}

	public void setImageBitmap(Bitmap bmp)
	{
		this.mImageView.setImageBitmap(bmp);
	}

	public void setImageResource(int res_id)
	{
		this.mImageView.setImageResource(res_id);
	}

	public void setImageDrawable(Drawable drawable)
	{
		this.mImageView.setImageDrawable(drawable);
	}

	public Bitmap getImageBitmap()
	{
		return ((BitmapDrawable) this.mImageView.getDrawable()).getBitmap();
	}

}