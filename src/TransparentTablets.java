import java.awt.Frame;

import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

import domain.EnvironmentState;



public class TransparentTablets {
	
	public static void main(String[] args) {
		// Init the environment
		EnvironmentState state = EnvironmentState.getInstance();
		
		// Create the window
		Frame frame = new Frame(args[0]);
		
		GLCanvas canvas = new GLCanvas();
		FPSAnimator animator = new FPSAnimator(canvas, 60);
	}

}
