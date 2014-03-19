package domain;

public class Vec3 extends Tuple3{
	
	public Vec3(Point3 p1, Point3 p2) {
		super(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
	}
	
	public Vec3(float x_dir, float y_dir, float z_dir) {
		super(x_dir, y_dir, z_dir);
	}
	
	public Vec3(Vec3 vec){
		super(vec.x, vec.y, vec.z);
	}
	
	public void multiply(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
	}
	
	public void divide(float scale) {
		this.x /= scale;
		this.y /= scale;
		this.z /= scale;
	}
	
	public void normalize(){
		float distance = this.distance();
				
		this.x /= distance;
		this.y /= distance;
		this.z /= distance;
	}
	
	public float distance() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public float distance2() {
		return (x * x + y * y + z * z);
	}
	
	public float dot(Vec3 vec) {
		return (this.x * vec.x + this.y * vec.y + this.z * vec.z);
	}
	
	public Vec3 cross(Vec3 vec) {
		return new Vec3(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
	}
}
