package suike.suikecherry.data;

public class AxisPosition{
    private final double x, y, z;
    private final float rotation;

    public AxisPosition(double x, double z) {
        this(x, 0.0, z, 0.0F);
    }
    public AxisPosition(double x, double z, float rotation) {
        this(x, 0.0, z, rotation);
    }
    public AxisPosition(double x, double y, double z, float rotation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getRotation() {
        return this.rotation;
    }

    public String toString() {
        return String.format("[相对位置: x%.2f, y%.2f, z%.2f, 旋转角度%.1f]", this.x, this.y, this.z, this.rotation);
    }
}