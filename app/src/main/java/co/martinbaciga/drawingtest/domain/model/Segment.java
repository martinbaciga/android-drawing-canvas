package co.martinbaciga.drawingtest.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Segment
{
	public static final String TYPE_LINE = "line";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_IMAGE = "image";

	public static final String TEXT_ALIGN_LEFT = "left";
	public static final String TEXT_ALIGN_CENTER = "center";
	public static final String TEXT_ALIGN_RIGHT = "right";

	private String type;

	// Line
    private List<Point> points = new ArrayList<Point>();
    private int color;
    private int strokeWidth;
	private int opacity;

	// Text
	private float x;
	private float y;
	private float width;
	private float height;
	private String text;
	private float textSize;
	private String alignment;

	// Image
	private String url;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Segment() {
    }

    public Segment(String type, int color, int strokeWidth) {
		this.type = type;
        this.color = color;
		this.strokeWidth = strokeWidth;
    }

	public Segment(String type, float x, float y, float width, float height, String text, float textSize, int textColor, String alignment)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.textSize = textSize;
		this.color = textColor;
		this.alignment = alignment;
	}

	public Segment(String type, float x, float y, float width, float height, String url)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.url = url;
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public List<Point> getPoints()
	{
		return points;
	}

	public void setPoints(List<Point> points)
	{
		this.points = points;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public int getStrokeWidth()
	{
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth)
	{
		this.strokeWidth = strokeWidth;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public float getTextSize()
	{
		return textSize;
	}

	public void setTextSize(float textSize)
	{
		this.textSize = textSize;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getAlignment()
	{
		return alignment;
	}

	public void setAlignment(String alignment)
	{
		this.alignment = alignment;
	}

	public int getOpacity()
	{
		return opacity;
	}

	public void setOpacity(int opacity)
	{
		this.opacity = opacity;
	}
}
