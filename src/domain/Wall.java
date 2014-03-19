package domain;

public class Wall {
	protected Point3 corners[];
	protected Vec3 normal;
	
	/**
	 * The points are used for determining the normal, so order matters.
	 * Normal is (topRight - topLeft) x (bottomRight - topRight)
	 * @param topLeft
	 * @param topRight
	 * @param bottomLeft
	 * @param bottomRight
	 */
	public Wall(Point3 topLeft, Point3 topRight, Point3 bottomLeft, Point3 bottomRight) {
		this.corners = new Point3[4];
		this.corners[0] = topLeft;
		this.corners[1] = topRight;
		this.corners[2] = bottomLeft;
		this.corners[3] = bottomRight;
		
		this.normal = new Vec3(topRight, topLeft).cross(new Vec3(topRight, bottomRight));
		this.normal.normalize();
	}
	
	public Wall(Point3 topLeft, Point3 topRight, Point3 bottomLeft, Point3 bottomRight, Vec3 normal){
		this.corners = new Point3[4];
		this.corners[0] = topLeft;
		this.corners[1] = topRight;
		this.corners[2] = bottomLeft;
		this.corners[3] = bottomRight;
		
		this.normal = normal;
		this.normal.normalize();
	}

	public Point3[] getCorners() {
		return corners;
	}

	public void setCorners(Point3[] corners) {
		this.corners = corners;
	}

	public Vec3 getNormal() {
		return normal;
	}

	public void setNormal(Vec3 normal) {
		this.normal = normal;
	}
	
	public Point3 intersect(Ray3D ray) {
		// TODO: implement this
		return null;
	}
}
