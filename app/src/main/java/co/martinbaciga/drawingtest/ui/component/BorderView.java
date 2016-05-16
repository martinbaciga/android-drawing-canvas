package co.martinbaciga.drawingtest.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class BorderView extends View
{
	public BorderView(Context context)
	{
		super(context);
	}

	public BorderView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public BorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();

		Rect border = new Rect();
		border.left = (int) this.getLeft() - params.leftMargin;
		border.top = (int) this.getTop() - params.topMargin;
		border.right = (int) this.getRight() - params.rightMargin;
		border.bottom = (int) this.getBottom() - params.bottomMargin;
		Paint borderPaint = new Paint();
		borderPaint.setStrokeWidth(6);
		borderPaint.setColor(Color.DKGRAY);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
		canvas.drawRect(border, borderPaint);
	}
}
