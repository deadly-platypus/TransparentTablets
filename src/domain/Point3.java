package domain;

public class Point3 extends Tuple3{
	public Point3(float x, float y, float z) {
		super(x, y, z);
	}
	
	public Point3() {
		super(0.0f, 0.0f, 0.0f);
	}
	
	public Point3 translate(Vec3 vec) {
		return new Point3(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
}
