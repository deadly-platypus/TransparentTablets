package graphics;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import domain.EnvironmentState;

public class TTRenderer implements GLEventListener {
	protected EnvironmentState state;
	protected GLU glu;

	@Override
	public void init(GLAutoDrawable drawable) {
		this.state = EnvironmentState.getInstance();
		this.glu = new GLU();
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		
		if(!this.state.isUse_hardware()) {
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
		}
		
		if(this.state.getWall().getTex() != null) {
	         // Use linear filter for texture if image is larger than the original texture
	         gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	         // Use linear filter for texture if image is smaller than the original texture
	         gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) { }

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
        final GL2 gl = drawable.getGL().getGL2();

        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }
        
        this.state.resize();
        
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        this.glu.gluPerspective(this.state.getHorizontal_fov(), h, EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

	}

}
