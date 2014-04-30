package domain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

public class EnvironmentState {
	protected PPC currentCamera, previousCamera, userHead;
	protected Wall wall;
	protected Float horizontal_fov;
	protected boolean use_hardware;
	protected int windowHeight, windowWidth, simulatedHeight, simulatedWidth;
	protected GLCapabilities caps;
	protected boolean infiniteWall = true;
	
	public static Vec3 USER_DISPLACEMENT = new Vec3(0.0f, 0.0f, 1.0f);
	
	protected boolean useCapturedImage = true;
	protected boolean outputPixels = false;
	protected BufferedImage frame;
	protected int counter = 1;
	
	protected boolean isStopped = false;
	
	protected Stack<PPC> frames;
	
	private static EnvironmentState instance;
	
	public float orthoLeft, orthoRight, orthoTop, orthoBottom;
	
	public static final float MAX_Z = 10000.0f;
	public static final float MIN_Z = 0.0001f;
	
	public static String IMAGE_NAME_FORMAT = "img-%03d.png";
	public static String IMAGE_LOCATION = "C:\\Users\\Derrick\\Videos\\frames\\rotate_x2\\";
	
	private EnvironmentState() throws RuntimeException{
		this.caps = new GLCapabilities(GLProfile.getDefault());
		this.frames = new Stack<PPC>();
		
		if(useCapturedImage) {
			File file = new File(String.format(IMAGE_LOCATION + IMAGE_NAME_FORMAT, 1));
			BufferedImage image;
			try {
				image = ImageIO.read(file);
				this.windowHeight = image.getHeight();
				this.windowWidth = image.getWidth();
				
				this.simulatedWidth = 640;
				this.simulatedHeight = 480;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}			
		}
		
		this.resize(windowWidth, windowHeight);
		
		this.previousCamera = new PPC(this.horizontal_fov, this.simulatedWidth, this.simulatedHeight, new Point3(0.0f, 0.0f, 1.0f));
		this.currentCamera = null;
		this.userHead = new PPC(this.horizontal_fov, this.simulatedWidth, this.simulatedHeight, this.previousCamera.getOrigin().translate(USER_DISPLACEMENT));
		
		this.wall = new Wall(new Point3(-1.0f, 1.0f, 0.0f), 
								new Point3(1.0f, 1.0f, 0.0f), 
								new Point3(-1.0f, -1.0f, 0.0f), 
								new Point3(1.0f, -1.0f, 0.0f));
		
		this.orthoBottom = this.wall.getBottomLeft().getY();
		this.orthoLeft = this.wall.getBottomLeft().getX();
		this.orthoRight = this.wall.getBottomRight().getX();
		this.orthoTop = this.wall.getTopLeft().getY();
		
		this.wall.setInfinite(infiniteWall);
	}
	
	public void resize(int width, int height) {
		this.use_hardware = test_for_hardware();
		this.horizontal_fov = calculate_hfov();
		
		if(width > 0 && height > 0) {
			this.windowHeight = height;
			this.windowWidth = width;
		}
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

	public boolean isUseCapturedImage() {
		return useCapturedImage;
	}

	public void setUseCapturedImage(boolean useCapturedImage) {
		this.useCapturedImage = useCapturedImage;
	}

	public BufferedImage getFrame() {
		return frame;
	}

	public void setFrame(BufferedImage frame) {
		this.frame = frame;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean isOutputPixels() {
		return outputPixels;
	}

	public Stack<PPC> getFrames() {
		return frames;
	}

	public void setFrames(Stack<PPC> frames) {
		this.frames = frames;
	}

	public void setOutputPixels(boolean outputPixels) {
		this.outputPixels = outputPixels;
	}
	
	public void toggleFrameStop() {
		this.isStopped = !this.isStopped;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public PPC getUserHead() {
		return userHead;
	}

	public void setUserHead(PPC userHead) {
		this.userHead = userHead;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public int getSimulatedHeight() {
		return simulatedHeight;
	}

	public void setSimulatedHeight(int simulatedHeight) {
		this.simulatedHeight = simulatedHeight;
	}

	public int getSimulatedWidth() {
		return simulatedWidth;
	}

	public void setSimulatedWidth(int simulatedWidth) {
		this.simulatedWidth = simulatedWidth;
	}
}
