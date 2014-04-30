package domain;

import com.jogamp.opengl.util.texture.Texture;

public class Wall {
	protected Point3 corners[];
	protected Vec3 normal;
	protected Texture tex; // Used for when there is no hardware
	protected boolean isInfinite = true;
	
	/**
	 * The points are used for determining the normal, so order matters.
	 * Normal is (topRight - topLeft) x (topRight - bottomRight)
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
	
	public Texture getTex() {
		return tex;
	}

	public void setTex(Texture tex) {
		this.tex = tex;
	}

	public Point3 intersect(Ray3D ray) {
		float test = ray.getDirection().dot(this.normal);
		// This assumes that the ray is in front of the wall
		if(test >= 0.0f){
			return null;
		}
		
		Point3 x0 = this.corners[0];
		
		float d = -(this.normal.x * x0.x + this.normal.y * x0.y + this.normal.z * x0.z);
		
		float numerator = (d - this.normal.x * ray.getOrigin().x - this.normal.y * ray.getOrigin().y - this.normal.z * ray.getOrigin().z);
		
		float denominator = this.normal.x * ray.getDirection().x + this.normal.y * ray.getDirection().y + this.normal.z * ray.getDirection().z;
		
		float t = numerator / denominator;
		
		return new Point3(ray.origin.x + t * ray.direction.x, ray.origin.y + t * ray.direction.y, ray.origin.z + t * ray.direction.z);
	}
	
	public Point3 getTopLeft() {
		return this.corners[0];
	}
	
	public Point3 getTopRight() {
		return this.corners[1];
	}
	
	public Point3 getBottomLeft() {
		return this.corners[2];
	}
	
	public Point3 getBottomRight() {
		return this.corners[3];
	}
	
	public boolean isInfinite() {
		return isInfinite;
	}

	public void setInfinite(boolean isInfinite) {
		this.isInfinite = isInfinite;
	}

	public void translate(Vec3 vec) {
		for(int i = 0; i < this.corners.length; i++) {
			this.corners[i] = this.corners[i].translate(vec);
		}
	}
	
	public void rotate(Vec3 axis, float radiens) {
		for(int i = 0; i < this.corners.length; i++) {
			Tuple3 tmp = this.corners[i].rotate(axis, radiens);
			this.corners[i].setX(tmp.getX());
			this.corners[i].setY(tmp.getY());
			this.corners[i].setZ(tmp.getZ());
		}
		
		Tuple3 tmp = this.normal.rotate(axis, radiens);
		this.normal = new Vec3(tmp.x, tmp.y, tmp.z);
	}
}
