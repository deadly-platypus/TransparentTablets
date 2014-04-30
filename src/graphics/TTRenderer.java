package graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import domain.EnvironmentState;

public class TTRenderer implements GLEventListener {
	protected EnvironmentState state;
	protected GLU glu;
	protected int numFrames = 345;

	@Override
	public void init(GLAutoDrawable drawable) {
		this.state = EnvironmentState.getInstance();
		this.glu = new GLU();
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		
		if(!this.state.isUse_hardware()) {
			if(!this.state.isUseCapturedImage()) {
				try {
					URI uri = new File("crate.png").toURI();
					URL url = uri.toURL();
					Texture tex = TextureIO.newTexture(
					           url, // relative to project root 
					           false, ".png");
					this.state.getWall().setTex(tex);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				if(this.state.getWall().getTex() != null) {
			         // Use linear filter for texture if image is larger than the original texture
			         gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			         // Use linear filter for texture if image is smaller than the original texture
			         gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
				}
			}
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) { }

	@Override
	public void display(GLAutoDrawable drawable) {
		if(this.state.getCounter() == 0) {
			// Images are numbered starting at 1
			this.state.setCounter(1);
		}
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		if(this.state.isUseCapturedImage() && (!this.state.isStopped() || this.state.getFrame() == null)) {
			String filename = String.format(EnvironmentState.IMAGE_LOCATION + EnvironmentState.IMAGE_NAME_FORMAT, this.state.getCounter());
			System.out.println("Using frame " + this.state.getCounter());
			File file = new File(filename);
			try {
				this.state.setFrame(ImageIO.read(file));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			
			if(!this.state.isStopped()) {
				this.state.setCounter((this.state.getCounter() + 15) % numFrames);
			}
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		if(width > 0 && height > 0) {
			this.state.resize(width, height);
		}
	}
	
	protected BufferedImage createRenderableImage(BufferedImage bi) {
		BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
		Graphics2D g = copy.createGraphics();
		AffineTransform gt = new AffineTransform();
		gt.translate(0, bi.getHeight());
	    gt.scale(1, -1d);
	    g.transform(gt);	    
	    g.drawImage(bi, null, 0, 0);
	    
	    g.dispose();
	    
	    return copy;
	}

}
