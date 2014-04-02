package domain;

import java.nio.ByteBuffer;

public class PPC {
	/**
	 * C in the traditional PPC parlance
	 */
	protected Point3 origin;
	
	/**
	 * c in the traditional PPC parlance
	 */
	protected Vec3 view_corner;
	
	/**
	 * a and b respectively
	 */
	protected Vec3 width, height;
	
	/**
	 * f
	 */
	protected float focal_length;
	protected ByteBuffer pixels;
	
	private PPC() {}
	
	public PPC(float horizontal_fov, int width, int height, Point3 origin) {
		this.origin = origin;
		this.width = new Vec3(1.0f, 0.0f, 0.0f);
		this.height = new Vec3(0.0f, -1.0f, 0.0f);
		this.focal_length = (float) (width / (2.0f * Math.tan(horizontal_fov / 2.0f * (Math.PI / 180.0f))));
		this.view_corner = new Vec3(-(float)width / 2.0f, (float)height / 2.0f, -this.focal_length);
	}

	public Point3 getOrigin() {
		return origin;
	}

	public void setOrigin(Point3 origin) {
		this.origin = origin;
	}

	public Vec3 getView_corner() {
		return view_corner;
	}

	public void setView_corner(Vec3 view_corner) {
		this.view_corner = view_corner;
	}

	public Vec3 getWidth() {
		return width;
	}

	public void setWidth(Vec3 width) {
		this.width = width;
	}

	public Vec3 getHeight() {
		return height;
	}

	public void setHeight(Vec3 height) {
		this.height = height;
	}

	public float getFocal_length() {
		return focal_length;
	}

	public void setFocal_length(float focal_length) {
		this.focal_length = focal_length;
	}
	
	public ByteBuffer getPixels() {
		return this.pixels;
	}
	
	public void setPixels(ByteBuffer pixels) {
		this.pixels = pixels;
	}
	
	public Tuple3 toCameraCoords(Tuple3 in) {
		Matrix9 mat = new Matrix9();
		
		mat.setColumn(0, this.width);
		mat.setColumn(1, this.height);
		mat.setColumn(2, this.view_corner);
		
		Tuple3 pmc = in.subtract(this.origin);
		Matrix9 inv = mat.inverted();
		Tuple3 q = inv.multiply(pmc);
		
		if(q.z <= 0.0f){
			return null;
		}
		
		Tuple3 result = new Tuple3();
		result.x = q.x / q.z;
		result.y = q.y / q.z;
		result.z = 1.0f / q.z;
		
		return result;
	}
	
	public Tuple3 toWorldCoords(Tuple3 in) {
		Tuple3 x = this.width.multiply(in.x);
		Tuple3 y = this.height.multiply(in.y);
		Tuple3 result = x.add(y);
		result = result.add(this.view_corner);
		result = result.multiply(1.0f / in.z);
		result = result.add(this.origin);
		return result;
	}
	
	public Ray3D toCameraCoords(Ray3D ray) {
		Point3 origin = this.toCameraCoords(ray.getOrigin()).toPoint3();
		Vec3 dir = this.toCameraCoords(ray.getDirection()).toVec3();
		
		return new Ray3D(origin, dir);
	}
	
	public Ray3D toWorldCoords(Ray3D csRay) {
		Point3 origin = this.toWorldCoords(csRay.getOrigin()).toPoint3();
		Vec3 dir = this.toWorldCoords(csRay.getDirection()).toVec3();
		
		return new Ray3D(origin, dir);
	}
	
	public void rotate(Vec3 axis, float radians) {
		if((axis.distance2() - 0.0f) >= Tuple3.epsilon) {
			this.origin = this.origin.rotate(axis, radians).toPoint3();
			this.width = this.width.rotate(axis, radians).toVec3();
			this.height = this.height.rotate(axis, radians).toVec3();
			this.view_corner = this.view_corner.rotate(axis, radians).toVec3();
		}
	}
	
	public void translate(Vec3 vec) {
		this.origin = this.origin.translate(vec).toPoint3();
	}
	
	public PPC copy(boolean copyPixels) {
		PPC copy = new PPC();
		copy.focal_length = this.focal_length;
		copy.height = new Vec3(this.height);
		copy.width = new Vec3(this.width);
		copy.view_corner = new Vec3(this.view_corner);
		copy.origin = new Point3(this.origin);
		if(copyPixels) {
			copy.pixels = this.pixels.duplicate();
		}
		
		return copy;
	}
}
