package co.martinbaciga.drawingtest.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Segment
{
    private List<Point> mPoints = new ArrayList<Point>();
    private int mColor;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Segment() {
    }

    public Segment(int color) {
        this.mColor = color;
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
}
