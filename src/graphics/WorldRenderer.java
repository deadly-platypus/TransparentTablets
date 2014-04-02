package graphics;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.texture.TextureCoords;

import domain.EnvironmentState;

public class WorldRenderer extends TTRenderer implements GLEventListener {

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		super.display(drawable);
		
		GL2 gl = drawable.getGL().getGL2();
        
		// Change to projection matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        
        this.glu.gluPerspective(this.state.getHorizontal_fov(), this.state.getWidth() / this.state.getHeight(), EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
        this.glu.gluLookAt(0.0f, 0.0f, 2.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
        
        // Change back to model view matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // Render the wall
        TextureCoords textureCoords = null;
        Float textureTop = null;
        Float textureBottom = null;
        Float textureLeft = null;
        Float textureRight = null;
        if(this.state.getWall().getTex() != null){
        	this.state.getWall().getTex().enable(gl);
        	this.state.getWall().getTex().bind(gl);
        	
            textureCoords = this.state.getWall().getTex().getImageTexCoords();
            textureTop = textureCoords.top();
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right();
        }
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBegin(GL2GL3.GL_QUADS);
        
        // Render the wall
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureTop, textureLeft);
        }
        gl.glVertex3f(this.state.getWall().getTopLeft().getX(), this.state.getWall().getTopLeft().getY(), this.state.getWall().getTopLeft().getZ());
        
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureTop, textureRight);
        }
        gl.glVertex3f(this.state.getWall().getTopRight().getX(), this.state.getWall().getTopRight().getY(), this.state.getWall().getTopLeft().getZ());
        
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureBottom, textureRight);
        }
        gl.glVertex3f(this.state.getWall().getBottomRight().getX(), this.state.getWall().getBottomRight().getY(), this.state.getWall().getBottomRight().getZ());
        
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureBottom, textureLeft);
        }
        gl.glVertex3f(this.state.getWall().getBottomLeft().getX(), this.state.getWall().getBottomLeft().getY(), this.state.getWall().getBottomLeft().getZ());
        
        gl.glEnd();
        
        // Draw the camera box
        gl.glDisable(GL.GL_TEXTURE_2D);
        //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
        gl.glBegin(GL2GL3.GL_LINE_LOOP);
        
        // Draw the camera box
        //gl.glColor4f(0.0f, 1.0f, 0.0f, 0.0f);
        //Point3 topLeft = this.state.getCurrentCamera().getOrigin().translate(this.state.getCurrentCamera().getView_corner());
//        gl.glVertex3f(topLeft.getX(), topLeft.getY(), 0.1f);
//        gl.glVertex3f(topLeft.getX() + this.state.getWidth(), topLeft.getY(), 0.1f);
//        gl.glVertex3f(topLeft.getX() + this.state.getWidth(), topLeft.getY() - this.state.getHeight(), 0.1f);
//        gl.glVertex3f(topLeft.getX(), topLeft.getY() - this.state.getHeight(), 0.1f);
        
        gl.glEnd();
        
        // Draw the camera
        float length = 0.01f;
        gl.glBegin(GL2GL3.GL_QUADS);
        gl.glVertex3f(this.state.getCurrentCamera().getOrigin().getX() - length, this.state.getCurrentCamera().getOrigin().getY() + length, this.state.getCurrentCamera().getOrigin().getZ());
        gl.glVertex3f(this.state.getCurrentCamera().getOrigin().getX() + length, this.state.getCurrentCamera().getOrigin().getY() + length, this.state.getCurrentCamera().getOrigin().getZ());
        gl.glVertex3f(this.state.getCurrentCamera().getOrigin().getX() + length, this.state.getCurrentCamera().getOrigin().getY() - length, this.state.getCurrentCamera().getOrigin().getZ());
        gl.glVertex3f(this.state.getCurrentCamera().getOrigin().getX() - length, this.state.getCurrentCamera().getOrigin().getY() - length, this.state.getCurrentCamera().getOrigin().getZ());
        gl.glEnd();
	}
}
