package RayCaster.raycast;
public class player {
    public double x;
    public double y;
    public double angle;

    public player(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void rotate(double da) {
        angle += da;
    }

    public double[] getPos() {
        return new double[] {x, y};
    }

    public double getAngle() {
        return angle;
    }
}
