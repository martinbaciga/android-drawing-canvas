package co.martinbaciga.drawingtest.ui.manager;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class CanvasMenuManager
{
	private ImageView mManipulableOption;

	private ArrayList<ImageView> mBaseOptions = new ArrayList<>();
	private ArrayList<ImageView> mTextOptions = new ArrayList<>();
	private ArrayList<ImageView> mPaintOptions = new ArrayList<>();

	public CanvasMenuManager(ImageView textImageView, ImageView textFontImageView, ImageView textColorImageView, ImageView textAlignLeftImageView, ImageView textAlignCenterImageView, ImageView textAlignRightImageView,
							 ImageView paintImageView, ImageView imageImageView, ImageView fillBackgroundImageView, ImageView colorImageView, ImageView strokeImageView, ImageView opacityImageView, ImageView manipulateImageView, ImageView undoImageView)
	{
		mManipulableOption = manipulateImageView;

		mBaseOptions.add(textImageView);
		mBaseOptions.add(paintImageView);
		mBaseOptions.add(imageImageView);
		mBaseOptions.add(fillBackgroundImageView);
		mBaseOptions.add(manipulateImageView);
		mBaseOptions.add(undoImageView);

		mTextOptions.add(textFontImageView);
		mTextOptions.add(textColorImageView);
		mTextOptions.add(textAlignLeftImageView);
		mTextOptions.add(textAlignCenterImageView);
		mTextOptions.add(textAlignRightImageView);

		mPaintOptions.add(colorImageView);
		mPaintOptions.add(strokeImageView);
		mPaintOptions.add(opacityImageView);
	}

	public void showTextOptions()
	{
		for (ImageView to : mTextOptions)
		{
			to.setVisibility(View.VISIBLE);
		}

		for (ImageView bo : mBaseOptions)
		{
			bo.setVisibility(View.GONE);
		}

		for (ImageView po : mPaintOptions)
		{
			po.setVisibility(View.GONE);
		}

		mManipulableOption.setVisibility(View.VISIBLE);
	}

	public void showBaseOptions()
	{
		for (ImageView to : mTextOptions)
		{
			to.setVisibility(View.GONE);
		}

		for (ImageView bo : mBaseOptions)
		{
			bo.setVisibility(View.VISIBLE);
		}

		for (ImageView po : mPaintOptions)
		{
			po.setVisibility(View.GONE);
		}
	}

	public void showPaintOptions()
	{
		for (ImageView to : mTextOptions)
		{
			to.setVisibility(View.GONE);
		}

		for (ImageView bo : mBaseOptions)
		{
			bo.setVisibility(View.GONE);
		}

		for (ImageView po : mPaintOptions)
		{
			po.setVisibility(View.VISIBLE);
		}
	}
}
