package domain;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

public class EnvironmentState {
	protected PPC currentCamera, previousCamera;
	protected Wall wall;
	protected Float horizontal_fov;
	protected boolean use_hardware;
	protected int height, width;
	protected GLCapabilities caps;
	protected boolean infiniteWall = true;
	
	private static EnvironmentState instance;
	
	public static final float MAX_Z = 10000.0f;
	public static final float MIN_Z = 0.1f;
	
	private EnvironmentState() throws RuntimeException{
		this.resize();
		this.caps = new GLCapabilities(GLProfile.getDefault());
		
		this.previousCamera = null;
		this.currentCamera = new PPC(this.horizontal_fov, this.width, this.height, new Point3(0.0f, 0.0f, 1.0f));
		
		this.wall = new Wall(new Point3(-1.0f, 1.0f, 0.0f), 
								new Point3(1.0f, 1.0f, 0.0f), 
								new Point3(-1.0f, -1.0f, 0.0f), 
								new Point3(1.0f, -1.0f, 0.0f));
		
		this.wall.setInfinite(infiniteWall);
	}
	
	public void resize() {
		this.use_hardware = test_for_hardware();
		this.horizontal_fov = calculate_hfov();
		this.height = calculate_height();
		this.width = calculate_width();
	}
	
	public PPC getCurrentCamera() {
		return currentCamera;
	}
	public void setCurrentCamera(PPC currentCamera) {
		this.currentCamera = currentCamera;
	}
	public PPC getPreviousCamera() {
		return previousCamera;
	}
	public void setPreviousCamera(PPC previousCamera) {
		this.previousCamera = previousCamera;
	}
	public Wall getWall() {
		return wall;
	}
	public void setWall(Wall wall) {
		this.wall = wall;
	}
	public Float getHorizontal_fov() {
		return horizontal_fov;
	}
	public void setHorizontal_fov(Float horizontal_fov) {
		this.horizontal_fov = horizontal_fov;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public GLCapabilities getCaps() {
		return caps;
	}

	public void setCaps(GLCapabilities caps) {
		this.caps = caps;
	}

	public boolean isUse_hardware() {
		return use_hardware;
	}

	public void setUse_hardware(boolean use_hardware) {
		this.use_hardware = use_hardware;
	}

	public static EnvironmentState getInstance() throws RuntimeException {
		if(instance == null) {
			instance = new EnvironmentState();
		}
		return instance;
	}
	
	/**
	 * This method is where the Android calls needed to get the hardware camera hfov reside when it's ready
	 * @return the hardware hfov or 45.0f if use_hardware is false
	 * @throws RuntimeException if hardware is supposed to be used, but an exception is raised trying to get the hfov
	 */
	private Float calculate_hfov() throws RuntimeException {
		if(!use_hardware) {
			return 45.0f;
		} else {
			return null;
		}
	}
	
	/**
	 * Makes sure that all necessary hardware is present
	 * @return true if all hardware is necessary
	 */
	private boolean test_for_hardware() {
		try {
			// TODO: implement hardware tests here and return true if everything is present
		} catch (Exception e) { }
		
		return false;
	}
	
	/**
	 * 
	 * @return screen height or 300 if use_hardware is false
	 */
	private int calculate_height() {
		if(!use_hardware) {
			return 300;
		} else {
			// TODO: get hardware value
			return -1;
		}
	}
	
	/**
	 * 
	 * @return screen width or 300 if use_hardware is false
	 */
	private int calculate_width() {
		if(!use_hardware) {
			return 300;
		} else {
			// TODO: get hardware value
			return -1;
		}
	}
}
