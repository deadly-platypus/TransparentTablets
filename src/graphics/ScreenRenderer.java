package graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Timer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.util.texture.TextureCoords;

import domain.CameraDelta;
import domain.EnvironmentState;
import domain.PPC;
import domain.Point3;

public class ScreenRenderer extends TTRenderer implements GLEventListener {

	private GL2 gl = null;
	private int fboId;
	private int texId;
	
	@Override 
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		gl = drawable.getGL().getGL2();
		
		IntBuffer tmp = IntBuffer.allocate(1);
		gl.glGenTextures(1, tmp);
		this.texId = tmp.get();
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, this.texId);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        
		ByteBuffer pixels = ByteBuffer.allocate(3 * this.state.getHeight() * this.state.getWidth());
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, this.state.getWidth(), this.state.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, this.texId);
		
		tmp = IntBuffer.allocate(1);
		gl.glGenFramebuffers(1, tmp);
		this.fboId = tmp.get();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, this.fboId);
		gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, this.texId, 0);
		
		int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
		if(status != GL.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error making FBObject: " + FBObject.getStatusString(status));
			System.exit(status);
		}
		
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {        
		super.display(drawable);
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		ByteBuffer buffer = this.produceScreenPixels(this.state.getCurrentCamera().getOrigin(), true);
		this.state.getCurrentCamera().setPixels(buffer);		
		
		gl.glPopAttrib();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
		CameraDelta delta = this.findCameraDelta();
		System.out.println("found delta..." + delta);
		
		this.state.setPreviousCamera(this.state.getCurrentCamera());
		this.state.setCurrentCamera(new PPC(this.state.getHorizontal_fov(), this.state.getWidth(), this.state.getHeight(), this.state.getCurrentCamera().getOrigin()));
	}
	
	private void drawWall() {		
		TextureCoords textureCoords = null;
		Float textureTop = null;
		Float textureBottom = null;
		Float textureLeft = null;
		Float textureRight = null;

		// Render the wall
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
	}
	
	private ByteBuffer produceScreenPixels(Point3 pt, boolean resetProj) {
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		if(resetProj) {
			gl.glLoadIdentity();
		}

		this.glu.gluPerspective(this.state.getHorizontal_fov(), this.state.getWidth() / this.state.getHeight(), EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
		this.glu.gluLookAt(pt.getX(), 
				pt.getY(), 
				pt.getZ(), 
				pt.getX(), 
				pt.getY(), 
				-1.0f, 0.0f, 1.0f, 0.0f);
		
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, this.fboId);
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, this.state.getWidth(), this.state.getHeight());
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);	
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		drawWall();
		
		// Copy the pixels
		ByteBuffer buffer = ByteBuffer.allocate(3 * this.state.getHeight() * this.state.getWidth());
		gl.glReadPixels(0, 0, this.state.getWidth(), this.state.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
		
		return buffer;
	}
	
	private CameraDelta findCameraDelta() {
		float min_transx 	= 0.0f;
		float min_transy 	= 0.0f;
		float min_transz 	= 0.0f;
		float min_rotx 		= 0.0f;
		float min_roty		= 0.0f;
		float min_rotz		= 0.0f;
		
		float min_error		= Float.MAX_VALUE;
		
		float wiggle = 0.5f; // TODO: Should I be using the same value for everything?
		float delta = wiggle;
		for(float x_trans = -wiggle; x_trans < wiggle; x_trans += delta) {
			for(float y_trans = -wiggle; y_trans < wiggle; y_trans += delta) {
				for(float z_trans = -wiggle; z_trans < wiggle; z_trans += delta) {
					for(float x_rot = -wiggle; x_rot < wiggle; x_rot += delta) {
						for(float y_rot = -wiggle; y_rot < wiggle; y_rot += delta) {
							for(float z_rot = -wiggle; z_rot < wiggle; z_rot += delta) {
								float error = error(min_error, x_trans, y_trans, z_trans, x_rot, y_rot, z_rot);
								if(error < min_error) {
									min_error = error;
									min_transx = x_trans;
									min_transy = y_trans;
									min_transz = z_trans;
									min_rotx = x_rot;
									min_roty = y_rot;
									min_rotz = z_rot;
								} else if (error == min_error) {
									// Keep the smallest change
									float tmp_x = Math.min(Math.abs(x_trans), Math.abs(min_transx));
									if(tmp_x == Math.abs(x_trans)) {
										min_transx = x_trans;
									}
									tmp_x = Math.min(Math.abs(x_rot), Math.abs(min_rotx));
									if(tmp_x == Math.abs(x_rot)) {
										min_rotx = x_rot;
									}
									
									float tmp_y = Math.min(Math.abs(y_trans), Math.abs(min_transy));
									if(tmp_y == Math.abs(y_trans)) {
										min_transy = y_trans;
									}
									tmp_y = Math.min(Math.abs(y_rot), Math.abs(min_roty));
									if(tmp_y == Math.abs(y_rot)) {
										min_roty = y_rot;
									}
									
									float tmp_z = Math.min(Math.abs(z_trans), Math.abs(min_transz));
									if(tmp_z == Math.abs(z_trans)) {
										min_transz = z_trans;
									}
									tmp_z = Math.min(Math.abs(z_rot), Math.abs(min_rotz));
									if(tmp_z == Math.abs(z_rot)) {
										min_rotz = z_rot;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return new CameraDelta(min_transx, min_transy, min_transz, min_rotx, min_roty, min_rotz);
	}
	
	private float error(float min_error, float x_trans, float y_trans, float z_trans, float x_rot, float y_rot, float z_rot) {
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		this.gl.glRotatef(1.0f, x_rot, y_rot, z_rot);
		this.gl.glTranslatef(x_trans, y_trans, z_trans);
		
		ByteBuffer buf = this.produceScreenPixels(this.state.getCurrentCamera().getOrigin(), false);
		
		float err = 0.0f;
		
		if(this.state.getPreviousCamera() != null && this.state.getPreviousCamera().getPixels() != null) {
			for(int i = 0; i < this.state.getHeight() * this.state.getWidth() * 3; i++) {
				err += (buf.get(i) - this.state.getPreviousCamera().getPixels().get(i)) * (buf.get(i) - this.state.getPreviousCamera().getPixels().get(i));
				
				if(err > min_error) {
					// There's no point in going further
					break;
				}
			}
		}
		
		return err;
	}
}
