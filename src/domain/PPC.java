package domain;

public class PPC {
	protected Point3 origin, view_corner;
	protected Vec3 width, height;
	protected float focal_length;
	protected float pixels[];
	
	public PPC(float horizontal_fov, int width, int height, Point3 origin) {
		this.origin = origin;
		this.width = new Vec3(1.0f, 0.0f, 0.0f);
		this.height = new Vec3(0.0f, -1.0f, 0.0f);
		this.focal_length = (float) (width / 2.0f / Math.tan(horizontal_fov / 180.0f * Math.PI / 2.0f));
		this.view_corner = new Point3(-(float)width / 2.0f, (float)height / 2.0f, -this.focal_length);
	}

	public Point3 getOrigin() {
		return origin;
	}

	public void setOrigin(Point3 origin) {
		this.origin = origin;
	}

	public Point3 getView_corner() {
		return view_corner;
	}

	public void setView_corner(Point3 view_corner) {
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
	
	public float[] getPixels() {
		// TODO: implement this
		return this.pixels;
	}
	
	public float getPixel(int x, int y) {
		return this.pixels[EnvironmentState.getInstance().width * y + x];
	}
	
	public void setPixel(int x, int y, float color) {
		this.pixels[EnvironmentState.getInstance().width * y + x] = color;
	}
	
	public Tuple3 project(Tuple3 in) {
		Matrix9 mat = new Matrix9();
		
		mat.setColumn(0, this.width);
		mat.setColumn(1, this.height);
		mat.setColumn(2, this.view_corner);
		
		Tuple3 q = mat.inverted().multiply(in.subtract(this.origin));
		
		if(q.z < 0.0f){
			return null;
		}
		
		Tuple3 result = new Tuple3();
		result.x = q.x / q.z;
		result.y = q.y / q.z;
		result.z = 1.0f / q.z;
		
		return result;
	}
	
	public Ray3D project(Ray3D ray) {
		Point3 origin = (Point3) this.project(ray.getOrigin());
		Vec3 dir = (Vec3) this.project(ray.getDirection());
		
		return new Ray3D(origin, dir);
	}
}
