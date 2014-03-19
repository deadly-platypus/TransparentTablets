package domain;

public class Point3D extends Tuple3{
	public Point3D(float x, float y, float z) {
		super(x, y, z);
	}
	
	public Point3D translate(Vec3D vec) {
		return new Point3D(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
}
