package co.martinbaciga.drawingtest.ui.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import co.martinbaciga.drawingtest.ui.component.DrawingView;
import co.martinbaciga.drawingtest.ui.component.ManipulableImageView;
import co.martinbaciga.drawingtest.ui.component.ManipulableTextView;
import co.martinbaciga.drawingtest.ui.component.ManipulableView;

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

		mDrawingViews.add(mBaseDrawingView);
		mLayers.add(mBaseDrawingView);
	}

	public void addTextComponent(String text)
	{
		if (mLayers.size() > 1 && getTopLayer().getClass() == DrawingView.class && ((DrawingView)getTopLayer()).isEmpty())
		{
			removeTopLayer();
		}

		ManipulableTextView tv = new ManipulableTextView(mContext);
		tv.setText(text);
		tv.setControlItemsHidden(true);
		mRoot.addView(tv);

		mManipulableViews.add(tv);
		mLayers.add(tv);

		addDrawingLayer();
	}

	public void addImageComponent(Bitmap bitmap)
	{
		if (mLayers.size() > 1 && getTopLayer().getClass() == DrawingView.class && ((DrawingView)getTopLayer()).isEmpty())
		{
			removeTopLayer();
		}

		ManipulableImageView iv = new ManipulableImageView(mContext);
		iv.setImageBitmap(bitmap);
		iv.setControlItemsHidden(true);
		mRoot.addView(iv);

		mManipulableViews.add(iv);
		mLayers.add(iv);

		addDrawingLayer();
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
