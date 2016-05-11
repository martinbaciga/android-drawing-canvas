package co.martinbaciga.drawingtest.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Segment
{
    private List<Point> mPoints = new ArrayList<Point>();
    private int mColor;
    private int mStrokeWidth;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Segment() {
    }

    public Segment(int color, int strokeWidth) {
        this.mColor = color;
		this.mStrokeWidth = strokeWidth;
    }

    public void addPoint(int x, int y) {
        Point p = new Point(x, y);
        mPoints.add(p);
    }

    public List<Point> getPoints() {
        return mPoints;
    }

    public int getColor() {
        return mColor;
    }

	public int getStrokeWidth()
	{
		return mStrokeWidth;
	}

	public void setStrokeWidth(int mStrokeWidth)
	{
		this.mStrokeWidth = mStrokeWidth;
	}
}
