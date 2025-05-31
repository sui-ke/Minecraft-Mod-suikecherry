package suike.suikecherry.data;

public class RenderPosition{
    private final double x, y, z;
    private final float rotation;

    public RenderPosition(double x, double z, float rotation) {
        this(x, 0.0, z, rotation);
    }
    public RenderPosition(double x, double y, double z, float rotation) {
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
        return String.format("[渲染位置: x%.2f, y%.2f, z%.2f, 旋转角度%.1f]", this.x, this.y, this.z, this.rotation);
    }
}