package domain;

public class Vec3 extends Tuple3{
	
	public Vec3(Point3 p1, Point3 p2) {
		super(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
	}
	
	public Vec3(float x_dir, float y_dir, float z_dir) {
		super(x_dir, y_dir, z_dir);
	}
	
	public Vec3() {
		super(0.0f, 0.0f, 0.0f);
	}
	
	public Vec3(Vec3 vec){
		super(vec.x, vec.y, vec.z);
	}
	
	public Vec3(Tuple3 t3) {
		super(t3.x, t3.y, t3.z);
	}
	
	public void normalize(){
		float distance = this.distance();
				
		if(distance != 0.0f) {
			this.x /= distance;
			this.y /= distance;
			this.z /= distance;
		}
	}
	
	public Vec3 multiply(float scale) {
		return new Vec3(super.multiply(scale));
	}
	
	public Vec3 divide(float scale) {
		return new Vec3(super.divide(scale));
	}
	
	public float dot(Vec3 vec) {
		return (this.x * vec.x + this.y * vec.y + this.z * vec.z);
	}
	
	public Vec3 cross(Vec3 vec) {
		return new Vec3(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
	}
}
