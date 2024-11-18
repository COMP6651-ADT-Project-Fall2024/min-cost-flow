package graph;

public class Vertex {
    private final int id;
    private double x;
    private double y;

    public Vertex(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
