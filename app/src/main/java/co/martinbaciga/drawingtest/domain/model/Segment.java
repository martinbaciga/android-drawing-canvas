package co.martinbaciga.drawingtest.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Segment
{
    private List<Point> points = new ArrayList<Point>();
    private int color;
    private int strokeWidth;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Segment() {
    }

    public Segment(int color, int strokeWidth) {
        this.color = color;
		this.strokeWidth = strokeWidth;
    }

    public void addPoint(int x, int y) {
        Point p = new Point(x, y);
        points.add(p);
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getColor() {
        return color;
    }

	public int getStrokeWidth()
	{
		return strokeWidth;
	}

	public void setStrokeWidth(int mStrokeWidth)
	{
		this.strokeWidth = mStrokeWidth;
	}
}
