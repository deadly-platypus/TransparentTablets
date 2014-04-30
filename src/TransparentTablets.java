import graphics.TTKeyHandler;
import graphics.WorldRenderer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventListener;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;

import domain.EnvironmentState;

public class TransparentTablets {
	
	public static void main(String[] args) {
		// Init the environment
		final String title = "Transparent Tablets";		
		
		// Run the GUI codes in the event-dispatching thread for thread safety
	    SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	        	 // Create the window
	        	 Frame worldFrame = new Frame(title + " - World");
		
	        	 GLCanvas worldCanvas = new GLCanvas();
	        	 EventListener listener = new TTKeyHandler(worldCanvas);
	        	 worldCanvas.addKeyListener((KeyListener) listener);
	        	 worldCanvas.addMouseMotionListener((MouseMotionListener) listener);
	        	 worldCanvas.addMouseListener((MouseListener) listener);
	        	 final FPSAnimator animator = new FPSAnimator(worldCanvas, 60);
	        	 worldCanvas.addGLEventListener(new WorldRenderer());
	        	 if(!EnvironmentState.getInstance().isUseCapturedImage()) {
	        		 worldCanvas.setPreferredSize(new Dimension(EnvironmentState.getInstance().getWindowWidth() * 3, EnvironmentState.getInstance().getWindowHeight() * 3));
	        	 } else {
	        		 // This is the size of the picture frames
	        		 worldCanvas.setPreferredSize(new Dimension(1920, 1080));
	        	 }
	        	 
	        	 worldFrame.add(worldCanvas);
	        	 worldFrame.addWindowListener(new WindowAdapter() {
	                 @Override 
	                 public void windowClosing(WindowEvent e) {
	                    // Use a dedicate thread to run the stop() to ensure that the
	                    // animator stops before program exits.
	                    new Thread() {
	                       @Override 
	                       public void run() {
	                          if (animator.isStarted()) animator.stop();
	                          System.exit(0);
	                       }
	                    }.start();
	                 }
	              });
	        	 
	        	 worldFrame.pack();
	        	 worldFrame.setVisible(true);
	        	 
	        	 animator.start();
	         }
	    });
	}

}
