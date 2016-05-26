package co.martinbaciga.drawingtest.ui.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.martinbaciga.drawingtest.domain.model.Segment;
import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableImageView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.component.ManipulableView;
import co.martinbaciga.drawingtest.ui.interfaces.ManipulableViewEventListener;

public class LayerManager
{
	private Context mContext;
	private FrameLayout mRoot;
	private DrawingView mBaseDrawingView;

	private boolean mManipulateEnabled = false;

	private ArrayList<ManipulableView> mManipulableViews = new ArrayList<>();
	private ArrayList<DrawingView> mDrawingViews = new ArrayList<>();
	private ArrayList<View> mLayers = new ArrayList<>();

	public LayerManager(Context context, FrameLayout root, DrawingView baseDrawingView)
	{
		mContext = context;
		mRoot = root;
		mBaseDrawingView = baseDrawingView;
		mBaseDrawingView.setEnabled(true);

		mDrawingViews.add(mBaseDrawingView);
		mLayers.add(mBaseDrawingView);
	}

	public ManipulableTextView addTextComponent(String text, float textSize, float x, float y, ManipulableViewEventListener listener, String segmentId)
	{
		if (mLayers.size() > 1 && getTopLayer().getClass() == DrawingView.class && ((DrawingView)getTopLayer()).isEmpty())
		{
			removeTopLayer();
		}

		ManipulableTextView tv = new ManipulableTextView(mContext, listener);
		tv.setText(text);
		tv.setTextSize(textSize);
		tv.setControlItemsHidden(true);
		tv.setX(x);
		tv.setY(y);
		tv.setSegmentId(segmentId);
		mRoot.addView(tv);

		mManipulableViews.add(tv);
		mLayers.add(tv);

		//addDrawingLayer();

		return tv;
	}

	public void updateTextComponent(String segmentId, String text, float x, float y, float textSize, int width, int height)
	{
		for (ManipulableView mv : mManipulableViews)
		{
			if (mv.getSegmentId().matches(segmentId))
			{
				ManipulableTextView mtv = (ManipulableTextView) mv;
				mtv.setText(text);
				mtv.setX(x);
				mtv.setY(y);
				mtv.setTextSize(textSize);
				mtv.setSize(width, height);
			}
		}
	}

	public void removeTextComponent(String segmentId)
	{
		for (int i = 0; i < mManipulableViews.size(); i++)
		{
			if (mManipulableViews.get(i).getSegmentId().matches(segmentId))
			{
				mRoot.removeView(mManipulableViews.get(i));
				mLayers.remove(mManipulableViews.get(i));
				mManipulableViews.remove(i);
			}
		}
	}

	public void addImageComponent(Bitmap bitmap, ManipulableViewEventListener listener)
	{
		if (mLayers.size() > 1 && getTopLayer().getClass() == DrawingView.class && ((DrawingView)getTopLayer()).isEmpty())
		{
			removeTopLayer();
		}

		ManipulableImageView iv = new ManipulableImageView(mContext, listener);
		iv.setImageBitmap(bitmap);
		iv.setControlItemsHidden(true);
		mRoot.addView(iv);

		mManipulableViews.add(iv);
		mLayers.add(iv);

		//addDrawingLayer();
	}

	public void addDrawingLayer()
	{
		DrawingView drawingView = new DrawingView(mContext, true);
		mRoot.addView(drawingView);
		drawingView.setBackgroundColor(Color.TRANSPARENT);

		disableDrawingViews();

		mDrawingViews.add(drawingView);
		mLayers.add(drawingView);
	}

	public void changeManipulateState()
	{
		mManipulateEnabled = !mManipulateEnabled;

		for (ManipulableView mv : mManipulableViews)
		{
			mv.setControlItemsHidden(!mManipulateEnabled);
		}

		if (mManipulateEnabled)
		{
			disableTopDrawingView();
		} else {
			enableTopDrawingView();
		}
	}

	public void disableDrawingViews()
	{
		for (DrawingView dv : mDrawingViews)
		{
			dv.setEnabled(false);
		}
	}

	public void disableTopDrawingView()
	{
		mDrawingViews.get(mDrawingViews.size()-1).setEnabled(false);
	}

	public View getRoot()
	{
		return mRoot;
	}

	public void enableTopDrawingView()
	{
		mDrawingViews.get(mDrawingViews.size()-1).setEnabled(true);
	}

	private View getTopLayer()
	{
		return mLayers.get(mLayers.size()-1);
	}

	private void removeTopLayer()
	{
		mRoot.removeView(getTopLayer());
		mLayers.remove(mLayers.size()-1);
		mDrawingViews.remove(mDrawingViews.size()-1);
	}

}
