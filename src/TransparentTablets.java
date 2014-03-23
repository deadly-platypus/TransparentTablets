import graphics.ScreenRenderer;
import graphics.TTKeyHandler;
import graphics.WorldRenderer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.media.opengl.awt.GLCanvas;

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
	        	 final FPSAnimator animator = new FPSAnimator(worldCanvas, 60);
	        	 worldCanvas.addGLEventListener(new WorldRenderer());
	        	 worldCanvas.setPreferredSize(new Dimension(EnvironmentState.getInstance().getWidth() * 3, EnvironmentState.getInstance().getHeight() * 3));
	        	 
	        	 worldFrame.add(worldCanvas);
	        	 worldFrame.addKeyListener(new TTKeyHandler(worldCanvas));
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
	        	 
	        	 Frame screenFrame = new Frame(title + " - Screen");
	        	 screenFrame.setLocation(worldFrame.getLocation().x + worldFrame.getSize().width + 10, worldFrame.getLocation().y);
	        	 screenFrame.addWindowListener(new WindowAdapter() {
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
	        	 
	        	 GLCanvas screenCanvas = new GLCanvas();
	        	 screenFrame.addKeyListener(new TTKeyHandler(screenCanvas));
	        	 
	        	 screenCanvas.addGLEventListener(new ScreenRenderer());
	        	 screenCanvas.setPreferredSize(new Dimension(EnvironmentState.getInstance().getWidth(), EnvironmentState.getInstance().getHeight()));
	        	 screenFrame.add(screenCanvas);
	        	 animator.add(screenCanvas);
	        	 screenFrame.pack();
	        	 screenFrame.setVisible(true);
	        	 
	        	 animator.start();
	         }
	    });
	}

}
