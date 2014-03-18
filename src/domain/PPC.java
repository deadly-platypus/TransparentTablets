package domain;

public class PPC {
	protected Point3D origin, view_corner;
	protected Vec3D width, height;
	protected float focal_length;
	
	public PPC(float horizontal_fov, int width, int height, Point3D origin) {
		this.origin = origin;
		this.width = new Vec3D(1.0f, 0.0f, 0.0f);
		this.height = new Vec3D(0.0f, -1.0f, 0.0f);
		this.focal_length = (float) (width / 2.0f / Math.tan(horizontal_fov / 180.0f * Math.PI / 2.0f));
		this.view_corner = new Point3D(-(float)width / 2.0f, (float)height / 2.0f, -this.focal_length);
	}

	public Point3D getOrigin() {
		return origin;
	}

	public void setOrigin(Point3D origin) {
		this.origin = origin;
	}

	public Point3D getView_corner() {
		return view_corner;
	}

	public void setView_corner(Point3D view_corner) {
		this.view_corner = view_corner;
	}

	public Vec3D getWidth() {
		return width;
	}

	public void setWidth(Vec3D width) {
		this.width = width;
	}

	public Vec3D getHeight() {
		return height;
	}

	public void setHeight(Vec3D height) {
		this.height = height;
	}

	public float getFocal_length() {
		return focal_length;
	}

	public void setFocal_length(float focal_length) {
		this.focal_length = focal_length;
	}
	
}
