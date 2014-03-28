package domain;

import com.jogamp.opengl.util.texture.Texture;

public class Wall {
	protected Point3 corners[];
	protected Vec3 normal;
	protected Texture tex; // Used for when there is no hardware
	
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
		float denominator = ray.getDirection().dot(this.normal);
		// This assumes that the ray is in front of the wall
		if(denominator >= 0.0f){
			return null;
		}
		
		Tuple3 tmp = this.corners[0].subtract(ray.getOrigin());
		Vec3 tmpVec = new Vec3(tmp.x, tmp.y, tmp.z);
		float numerator = tmpVec.dot(this.normal);
		
		float d = numerator / denominator;
		
		Tuple3 t = ray.getOrigin().add(ray.getDirection().multiply(d));
		
		float x_min = Math.min(getTopLeft().x, getTopRight().x);
		float x_max = Math.max(getTopLeft().x, getTopRight().x);
		float y_min = Math.min(getTopLeft().y, getBottomLeft().y);
		float y_max = Math.max(getTopLeft().y, getBottomLeft().y);
		
		if(t.x >= x_min && t.x <= x_max 
				&& t.y >= y_min && t.y <= y_max) {
			return new Point3(t.x, t.y, t.z);
		}
		
		return null;
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
