package co.martinbaciga.drawingtest.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Segment
{
	public static final String TYPE_LINE = "line";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_IMAGE = "image";

	private String type;

	// Line
    private List<Point> points = new ArrayList<Point>();
    private int color;
    private int strokeWidth;

	// Text
	private float x;
	private float y;
	private float width;
	private float height;
	private String text;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Segment() {
    }

    public Segment(String type, int color, int strokeWidth) {
		this.type = type;
        this.color = color;
		this.strokeWidth = strokeWidth;
    }

	public Segment(String type, float x, float y, float width, float height, String text)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
	}

	public Segment(String type, List<Point> points, int color, int strokeWidth)
	{
		this.type = type;
		this.points = points;
		this.color = color;
		this.strokeWidth = strokeWidth;
	}

    public void addPoint(float x, float y) {
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}
}
