package domain;

public class Vec3D {
	protected float x_dir, y_dir, z_dir;
	
	public Vec3D(Point3D p1, Point3D p2) {
		this.x_dir = p1.x - p2.x;
		this.y_dir = p1.y - p2.y;
		this.z_dir = p1.z - p2.z;
	}
	
	public Vec3D(float x_dir, float y_dir, float z_dir) {
		this.x_dir = x_dir;
		this.y_dir = y_dir;
		this.z_dir = z_dir;
	}
	
	public Vec3D(Vec3D vec){
		this.x_dir = vec.x_dir;
		this.y_dir = vec.y_dir;
		this.z_dir = vec.z_dir;
	}
	
	public void multiply(float scale) {
		this.x_dir *= scale;
		this.y_dir *= scale;
		this.z_dir *= scale;
	}
	
	public void normalize(){
		float distance = this.distance();
				
		this.x_dir /= distance;
		this.y_dir /= distance;
		this.z_dir /= distance;
	}
	
	public float distance() {
		return (float) Math.sqrt(x_dir * x_dir + y_dir * y_dir + z_dir * z_dir);
	}
	
	public float distance2() {
		return (x_dir * x_dir + y_dir * y_dir + z_dir * z_dir);
	}
	
	public float dot(Vec3D vec) {
		return (this.x_dir * vec.x_dir + this.y_dir * vec.y_dir + this.z_dir * vec.z_dir);
	}
	
	public Vec3D cross(Vec3D vec) {
		return new Vec3D(this.y_dir * vec.z_dir - this.z_dir * vec.y_dir, this.z_dir * vec.x_dir - this.x_dir * vec.z_dir, this.x_dir * vec.y_dir - this.y_dir * vec.x_dir);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Vec3D)) {
			return false;
		}
		
		Vec3D vec = (Vec3D)obj;
		return (this.x_dir == vec.x_dir && this.y_dir == vec.y_dir && this.z_dir == vec.z_dir);
	}
	
	@Override
	public String toString() {
		return "(" + x_dir + ", " + y_dir + ", " + z_dir + ")";
	}
}
