package domain;

public class Ray3D {
	protected Point3 origin;
	protected Vec3 direction;
	
	public Ray3D(Point3 origin, Point3 p){
		this.origin = origin;
		this.direction = new Vec3(p, origin);
		this.direction.normalize();
	}
	
	public Ray3D(Point3 origin, Vec3 direction){
		this.origin = origin;
		this.direction = new Vec3(direction);
		this.direction.normalize();
	}

	public Point3 getOrigin() {
		return origin;
	}

	public void setOrigin(Point3 origin) {
		this.origin = origin;
	}

	public Vec3 getDirection() {
		return direction;
	}

	public void setDirection(Vec3 direction) {
		this.direction = direction;
	}
}
