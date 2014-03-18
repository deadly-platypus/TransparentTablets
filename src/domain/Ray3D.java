package domain;

public class Ray3D {
	protected Point3D origin;
	protected Vec3D direction;
	
	public Ray3D(Point3D origin, Point3D p){
		this.origin = origin;
		this.direction = new Vec3D(origin, p);
		this.direction.normalize();
	}
	
	public Ray3D(Point3D origin, Vec3D direction){
		this.origin = origin;
		this.direction = new Vec3D(direction);
		this.direction.normalize();
	}

	public Point3D getOrigin() {
		return origin;
	}

	public void setOrigin(Point3D origin) {
		this.origin = origin;
	}

	public Vec3D getDirection() {
		return direction;
	}

	public void setDirection(Vec3D direction) {
		this.direction = direction;
	}
}
