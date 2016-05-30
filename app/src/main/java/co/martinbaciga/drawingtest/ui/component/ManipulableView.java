package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import co.martinbaciga.drawingtest.R;
import co.martinbaciga.drawingtest.infrastructure.manager.SharedPreferencesManager;
import co.martinbaciga.drawingtest.ui.interfaces.ManipulableViewEventListener;
import co.martinbaciga.drawingtest.ui.manager.CanvasManager;
import co.martinbaciga.drawingtest.ui.util.SystemUtils;

public abstract class ManipulableView extends FrameLayout
{
	private static final String TAG = "DraggableViewGroup";
	private BorderView mBorderImageView; private static final String BORDER_IV_TAG = "BorderImageView";
	private ImageView mScaleImageView; private static final String SCALE_IV_TAG = "ScaleImageView";
	private ImageView mDeleteImageView; private static final String DELETE_IV_TAG = "DeleteImageView";
	private ImageView mFlipImageView; private static final String FLIP_IV_TAG = "FlipImageView";

	private String mSegmentId;

	private int mMargin;
	private int mSize;

	// For scalling
	private float mOrgX = -1, mOrgY = -1;
	private float mScaleOrgX = -1, mScaleOrgY = -1;
	private double mScaleOrgWidth = -1, mScaleOrgHeight = -1;

	// For rotating
	private float mRotateOrgX = -1, mRotateOrgY = -1, mRotateNewX = -1, mRotateNewY = -1;

	// For moving
	private float mMoveOrgX = -1, mMoveOrgY = -1;

	private double mCenterX, mCenterY;

	private final static int BUTTON_SIZE_DP = 30;
	private final static int SELF_SIZE_DP = 100;

	private boolean mRotationEnabled = false;
	private boolean mControlsHidden = false;

	private Context mContext;

	private GestureDetector mGestureDetector;

	private ManipulableViewEventListener mEventListener;

	public ManipulableView(Context context, ManipulableViewEventListener listener)
	{
		super(context);
		mContext = context;
		mEventListener = listener;
		init(context);
	}

	public ManipulableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public ManipulableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		mGestureDetector = new GestureDetector(context, new GestureListener());

		mMargin = SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext()) / 2;
		mSize = SystemUtils.convertDpToPixel(SELF_SIZE_DP, getContext());

		initMain(context);
		initBorder(context);
		initScaleButton(context);
		initDeleteButton(context);
		initFlipButton(context);
	}

	private void initMain(Context context)
	{
		this.setTag(TAG);

		LayoutParams this_params =
				new LayoutParams(
						mSize,
						mSize
				);

		LayoutParams iv_main_params =
				new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT
				);
		iv_main_params.setMargins(mMargin, mMargin, mMargin, mMargin);

		this.setLayoutParams(this_params);
		this.addView(getMainView(), iv_main_params);

		this.setOnTouchListener(mTouchListener);
	}

	private void initBorder(Context context)
	{
		this.mBorderImageView = new BorderView(context);
		this.mBorderImageView.setTag(BORDER_IV_TAG);

		LayoutParams iv_border_params =
				new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT
				);
		iv_border_params.setMargins(mMargin, mMargin, mMargin, mMargin);

		this.addView(mBorderImageView, iv_border_params);
	}

	private void initScaleButton(Context context)
	{
		this.mScaleImageView = new ImageView(context);
		this.mScaleImageView.setImageResource(R.drawable.zoominout);
		this.mScaleImageView.setTag(SCALE_IV_TAG);

		LayoutParams iv_scale_params =
				new LayoutParams(
						SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext()),
						SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext())
				);
		iv_scale_params.gravity = Gravity.BOTTOM | Gravity.RIGHT;

		this.addView(mScaleImageView, iv_scale_params);
		this.mScaleImageView.setOnTouchListener(mTouchListener);
	}

	private void initDeleteButton(Context context)
	{
		this.mDeleteImageView = new ImageView(context);
		this.mDeleteImageView.setImageResource(R.drawable.remove);
		this.mDeleteImageView.setTag(DELETE_IV_TAG);

		LayoutParams iv_delete_params =
				new LayoutParams(
						SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext()),
						SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext())
				);
		iv_delete_params.gravity = Gravity.TOP | Gravity.RIGHT;

		this.addView(mDeleteImageView, iv_delete_params);

		this.mDeleteImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				delete();
			}
		});
	}

	private void initFlipButton(Context context)
	{
		this.mFlipImageView = new ImageView(context);
		this.mFlipImageView.setImageResource(R.drawable.flip);
		this.mFlipImageView.setTag(FLIP_IV_TAG);

		LayoutParams iv_flip_params =
				new LayoutParams(
						SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext()),
						SystemUtils.convertDpToPixel(BUTTON_SIZE_DP, getContext())
				);
		iv_flip_params.gravity = Gravity.TOP | Gravity.LEFT;

		this.addView(mFlipImageView, iv_flip_params);

		this.mFlipImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				flip();
			}
		});
	}

	private OnTouchListener mTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			if (!mControlsHidden)
			{
				if (view.getTag().equals(TAG))
				{
					manageViewMotionEvents(event);
				} else if (view.getTag().equals(SCALE_IV_TAG))
				{
					manageScaleMotionEvents(event);
				}

				return true;
			} else if (SharedPreferencesManager.getBoolean(mContext, CanvasManager.SHARED_PREFERENCES_KEY_MANIPULABLE_ENABLED))
			{
				mGestureDetector.onTouchEvent(event);

				return true;
			}

			return false;
		}
	};

	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			mEventListener.onTap(ManipulableView.this);
			return true;
		}
	}

	private void manageViewMotionEvents(MotionEvent event)
	{
		if (!mControlsHidden)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					mMoveOrgX = event.getRawX();
					mMoveOrgY = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					float offsetX = event.getRawX() - mMoveOrgX;
					float offsetY = event.getRawY() - mMoveOrgY;
					ManipulableView.this.setX(ManipulableView.this.getX() + offsetX);
					ManipulableView.this.setY(ManipulableView.this.getY() + offsetY);
					mMoveOrgX = event.getRawX();
					mMoveOrgY = event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					mEventListener.onDragFinished(this);
					break;
			}
		}
	}

	private void manageScaleMotionEvents(MotionEvent event)
	{
		if (!mControlsHidden)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					manageMovement(event);
					break;
				case MotionEvent.ACTION_MOVE:
					manageScale(event);
					manageRotation(event);

					postInvalidate();
					requestLayout();
					break;
				case MotionEvent.ACTION_UP:
					mEventListener.onScaleFinished(this);
					break;
			}
		}
	}

	private void delete()
	{
		/*if (ManipulableView.this.getParent() != null)
		{
			ViewGroup myCanvas = ((ViewGroup) ManipulableView.this.getParent());
			myCanvas.removeView(ManipulableView.this);
		}*/
		mEventListener.onDeleteClick(this);
	}

	private void flip()
	{
		View mainView = getMainView();
		mainView.setRotationY(mainView.getRotationY() == -180f ? 0f : -180f);
		mainView.invalidate();
		requestLayout();
	}

	private void manageMovement(MotionEvent event)
	{
		mOrgX = ManipulableView.this.getX();
		mOrgY = ManipulableView.this.getY();

		mScaleOrgX = event.getRawX();
		mScaleOrgY = event.getRawY();
		mScaleOrgWidth = ManipulableView.this.getLayoutParams().width;
		mScaleOrgHeight = ManipulableView.this.getLayoutParams().height;

		mRotateOrgX = event.getRawX();
		mRotateOrgY = event.getRawY();

		mCenterX = ManipulableView.this.getX() +
				((View) ManipulableView.this.getParent()).getX() +
				(float) ManipulableView.this.getWidth() / 2;


		//double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0)
		{
			result = getResources().getDimensionPixelSize(resourceId);
		}
		double statusBarHeight = result;
		mCenterY = ManipulableView.this.getY() +
				((View) ManipulableView.this.getParent()).getY() +
				statusBarHeight +
				(float) ManipulableView.this.getHeight() / 2;
	}

	private void manageScale(MotionEvent event)
	{
		double angle_diff = Math.abs(
				Math.atan2(event.getRawY() - mScaleOrgY, event.getRawX() - mScaleOrgX)
						- Math.atan2(mScaleOrgY - mCenterY, mScaleOrgX - mCenterX)) * 180 / Math.PI;

		double length1 = getLength(mCenterX, mCenterY, mScaleOrgX, mScaleOrgY);
		double length2 = getLength(mCenterX, mCenterY, event.getRawX(), event.getRawY());

		int size = SystemUtils.convertDpToPixel(SELF_SIZE_DP, getContext());
		if (length2 > length1
				&& (angle_diff < 25 || Math.abs(angle_diff - 180) < 25))
		{
			// Scale up
			double offsetX = Math.abs(event.getRawX() - mScaleOrgX);
			double offsetY = Math.abs(event.getRawY() - mScaleOrgY);
			double offset = Math.max(offsetX, offsetY);
			offset = Math.round(offset);
			ManipulableView.this.getLayoutParams().width += offset;
			ManipulableView.this.getLayoutParams().height += offset;
			//DraggableViewGroup.this.setX((float) (getX() - offset / 2));
			//DraggableViewGroup.this.setY((float) (getY() - offset / 2));
		} else if (length2 < length1
				&& (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
				&& ManipulableView.this.getLayoutParams().width > size / 2
				&& ManipulableView.this.getLayoutParams().height > size / 2)
		{
			// Scale down
			double offsetX = Math.abs(event.getRawX() - mScaleOrgX);
			double offsetY = Math.abs(event.getRawY() - mScaleOrgY);
			double offset = Math.max(offsetX, offsetY);
			offset = Math.round(offset);
			ManipulableView.this.getLayoutParams().width -= offset;
			ManipulableView.this.getLayoutParams().height -= offset;
		}

		mScaleOrgX = event.getRawX();
		mScaleOrgY = event.getRawY();
	}

	private void manageRotation(MotionEvent event)
	{
		if (mRotationEnabled)
		{
			mRotateNewX = event.getRawX();
			mRotateNewY = event.getRawY();

			double angle = Math.atan2(event.getRawY() - mCenterY, event.getRawX() - mCenterX) * 180 / Math.PI;
			setRotation((float) angle - 45);

			mRotateOrgX = mRotateNewX;
			mRotateOrgY = mRotateNewY;
		}
	}

	private double getLength(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
	}

	private float[] getRelativePos(float absX, float absY)
	{
		float[] pos = new float[]{
				absX - ((View) this.getParent()).getX(),
				absY - ((View) this.getParent()).getY()
		};
		return pos;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}

	protected abstract View getMainView();

	public void setControlItemsHidden(boolean isHidden)
	{
		if (isHidden)
		{
			mBorderImageView.setVisibility(View.INVISIBLE);
			mScaleImageView.setVisibility(View.INVISIBLE);
			mDeleteImageView.setVisibility(View.INVISIBLE);
			mFlipImageView.setVisibility(View.INVISIBLE);
		} else
		{
			mBorderImageView.setVisibility(View.VISIBLE);
			mScaleImageView.setVisibility(View.VISIBLE);
			mDeleteImageView.setVisibility(View.VISIBLE);
			mFlipImageView.setVisibility(View.VISIBLE);
		}

		mControlsHidden = isHidden;
	}

	public boolean isFlipped()
	{
		return getMainView().getRotationY() == -180f;
	}

	public String getSegmentId()
	{
		return mSegmentId;
	}

	public void setSegmentId(String mSegmentId)
	{
		this.mSegmentId = mSegmentId;
	}

	public void setSize(int width, int height)
	{
		mSize = width;
		ManipulableView.this.getLayoutParams().width = width;
		ManipulableView.this.getLayoutParams().height = height;

		postInvalidate();
		requestLayout();
	}
}