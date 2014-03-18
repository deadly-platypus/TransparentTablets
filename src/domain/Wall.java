package domain;

public class Wall {
	protected Point3D corners[];
	protected Vec3D normal;
	
	/**
	 * The points are used for determining the normal, so order matters.
	 * Normal is (topRight - topLeft) x (bottomRight - topRight)
	 * @param topLeft
	 * @param topRight
	 * @param bottomLeft
	 * @param bottomRight
	 */
	public Wall(Point3D topLeft, Point3D topRight, Point3D bottomLeft, Point3D bottomRight) {
		this.corners = new Point3D[4];
		this.corners[0] = topLeft;
		this.corners[1] = topRight;
		this.corners[2] = bottomLeft;
		this.corners[3] = bottomRight;
		
		this.normal = new Vec3D(topRight, topLeft).cross(new Vec3D(bottomRight, topRight));
		this.normal.normalize();
	}
	
	public Wall(Point3D topLeft, Point3D topRight, Point3D bottomLeft, Point3D bottomRight, Vec3D normal){
		this.corners = new Point3D[4];
		this.corners[0] = topLeft;
		this.corners[1] = topRight;
		this.corners[2] = bottomLeft;
		this.corners[3] = bottomRight;
		
		this.normal = normal;
		this.normal.normalize();
	}

	public Point3D[] getCorners() {
		return corners;
	}

	public void setCorners(Point3D[] corners) {
		this.corners = corners;
	}

	public Vec3D getNormal() {
		return normal;
	}

	public void setNormal(Vec3D normal) {
		this.normal = normal;
	}
	
}
