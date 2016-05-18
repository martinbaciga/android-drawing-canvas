package co.martinbaciga.drawingtest.domain.model;

public class Point
{
    float x;
    float y;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Point() {
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
