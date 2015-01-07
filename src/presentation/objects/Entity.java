package presentation.objects;

public class Entity {

    private double x, y, z, vX, vY, vZ;
    private float width, height;
    private boolean isDead;
    private String id;

    public Entity(double x, double y, double z) {
        this(x, y, z, 0, 0, 0, 0, 0, false, "");
    }

    public Entity(double x, double y, double z, float width, float height) {
        this(x, y, z, width, height, 0, 0, 0, false, "");
    }

    public Entity(double x, double y, double z, float width, float height, double vx, double vy, double vz, boolean isDead, String id) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.vX = vx;
        this.vY = vy;
        this.vZ = vz;
        this.isDead = isDead;
        this.id = id;
    }



    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getvX() {
        return vX;
    }

    public double getvY() {
        return vY;
    }

    public double getvZ() {
        return vZ;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isDead() {
        return isDead;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", vX=" + vX +
                ", vY=" + vY +
                ", vZ=" + vZ +
                ", width=" + width +
                ", height=" + height +
                ", isDead=" + isDead +
                ", id='" + id + '\'' +
                '}';
    }
}
