package domain;

import graphics.WorldRenderer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

public class PPC {
	/**
	 * C in the traditional PPC parlance
	 */
	protected Point3 origin;
	
	/**
	 * c in the traditional PPC parlance
	 */
	protected Vec3 view_corner, look_at;
	
	/**
	 * a and b respectively
	 */
	protected Vec3 width, height;
	
	protected int camId;
	
	private static int nextId = 0;
	/**
	 * f
	 */
	protected float focal_length;
	protected BufferedImage pixels;
	protected BufferedImage simulatedPixels;
	
	private PPC() {}
	
	public PPC(float horizontal_fov, int width, int height, Point3 origin) {
		this.camId = PPC.nextId++;
		this.origin = origin;
		this.width = new Vec3(1.0f, 0.0f, 0.0f);
		this.height = new Vec3(0.0f, -1.0f, 0.0f);
		this.focal_length = (float) (width / (2.0f * Math.tan(horizontal_fov / 2.0f * (Math.PI / 180.0f))));
		this.view_corner = new Vec3(-(float)width / 2.0f, (float)height / 2.0f, -this.focal_length);
		this.look_at = new Vec3(0.0f, 0.0f, -1.0f);
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
	
	public BufferedImage getPixels() {
		return this.pixels;
	}
	
	public void setPixels(BufferedImage pixels) {
		this.pixels = pixels;
	}
	
	public Vec3 getLook_at() {
		return look_at;
	}

	public void setLook_at(Vec3 look_at) {
		this.look_at = look_at;
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
			Vec3 copy = new Vec3(axis);
			copy.normalize();
			this.view_corner = this.view_corner.rotate(copy, radians).toVec3();
			this.look_at = this.look_at.rotate(copy, radians).toVec3();
			this.height = this.height.rotate(copy, radians).toVec3();
			this.width = this.width.rotate(copy, radians).toVec3();
		}
	}
	
	public void translate(Vec3 vec) {
		this.origin = this.origin.translate(vec).toPoint3();
	}
	
	public ByteBuffer getByteBuffer() {
		if(this.pixels != null) {
			BufferedImage copy = new BufferedImage(this.pixels.getWidth(), this.pixels.getHeight(), this.pixels.getType());
			Graphics2D g = copy.createGraphics();
			AffineTransform gt = new AffineTransform();
			gt.translate(0, this.pixels.getHeight());
		    gt.scale(1, -1d);
		    g.transform(gt);	    
		    g.drawImage(this.pixels, null, 0, 0);
			
			byte src[] = ((DataBufferByte)copy.getRaster().getDataBuffer()).getData();
			ByteBuffer result = ByteBuffer.allocate(src.length);
			result.put(src, 0, src.length);
			result.rewind();
			g.dispose();
			return result;
		}
		
		return null;
	}
	
	public PPC copy(boolean copyPixels) {
		PPC copy = new PPC();
		copy.focal_length = this.focal_length;
		copy.height = new Vec3(this.height);
		copy.width = new Vec3(this.width);
		copy.view_corner = new Vec3(this.view_corner);
		copy.origin = new Point3(this.origin);
		copy.look_at = new Vec3(this.look_at);
//		if(copyPixels) {
//			copy.pixels = this.pixels.duplicate();
//		}
		
		return copy;
	}

	public BufferedImage getSimulatedPixels() {
		return simulatedPixels;
	}

	public void setSimulatedPixels(BufferedImage simulatedPixels) {
		this.simulatedPixels = simulatedPixels;
	}

	public int getCamId() {
		return camId;
	}

	public void setCamId(int camId) {
		this.camId = camId;
	}

	public static PPC makeAndTransformCamera(PPC original, float x_trans,
			float y_trans, float z_trans, float x_rot, float y_rot, float z_rot) {
		Vec3 trans = new Vec3(x_trans, y_trans, z_trans);
		Vec3 rot = new Vec3(x_rot, y_rot, z_rot);
		rot.normalize();

		PPC newCam = original.copy(false);
		newCam.translate(trans);
		newCam.rotate(rot, WorldRenderer.ROTATION_AMT);

		return newCam;
	}
	
	public static int nextId() {
		return PPC.nextId++;
	}
}
