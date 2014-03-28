package graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;

import domain.EnvironmentState;

public class ScreenRenderer extends TTRenderer implements GLEventListener {

	@Override
	public void display(GLAutoDrawable drawable) {
		super.display(drawable);
		
		GL2 gl = drawable.getGL().getGL2();
		
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
        
        IntBuffer frameBuffers = IntBuffer.allocate(1);
        gl.glGenFramebuffers(1, frameBuffers);
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, frameBuffers.get(0));
        
        int [] pbo = new int[1];

        gl.glGenBuffers(1, pbo, 0);
        gl.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER, pbo[0]);
        gl.glBufferData(GL2GL3.GL_PIXEL_UNPACK_BUFFER, this.state.getHeight() * this.state.getWidth() * Buffers.SIZEOF_FLOAT, null, GL.GL_STATIC_DRAW);
        
        IntBuffer textures = IntBuffer.allocate(1);
        gl.glGenTextures(1, textures);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures.get(0));
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, this.state.getWidth(), this.state.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        
        gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, textures.get(0), 0);
        
        int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
        if(status != GL.GL_FRAMEBUFFER_COMPLETE) {
        	System.out.println("Incomplete: " + FBObject.getStatusString(status));
        }
        
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        
        this.glu.gluPerspective(this.state.getHorizontal_fov(), this.state.getWidth() / this.state.getHeight(), EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
        this.glu.gluLookAt(this.state.getCurrentCamera().getOrigin().getX(), 
        		this.state.getCurrentCamera().getOrigin().getY(), 
        		this.state.getCurrentCamera().getOrigin().getZ(), 
        		this.state.getCurrentCamera().getOrigin().getX(), 
        		this.state.getCurrentCamera().getOrigin().getY(), 
        		this.state.getCurrentCamera().getOrigin().getZ(), 0.0f, 1.0f, 0.0f);
        
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        
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
        
        gl.glReadBuffer(frameBuffers.get(0));
        ByteBuffer pixels = ByteBuffer.allocate(this.state.getHeight() * this.state.getWidth() * Buffers.SIZEOF_FLOAT);
        gl.glReadPixels(0, 0, this.state.getWidth(), this.state.getHeight(), GL.GL_RGBA, GL.GL_BYTE, pixels);
        this.state.getCurrentCamera().setPixels(pixels);
        
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
        gl.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER, 0);
        
        
        // Render to screen here
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL.GL_TEXTURE_2D);
        
        TextureData td = new TextureData(this.state.getCaps().getGLProfile(), GL.GL_RGBA8, this.state.getWidth(), this.state.getHeight(), 0, GL.GL_RGBA, GL.GL_BYTE, false, false, false, pixels, null);
        Texture tex = new Texture(gl, td);
        textureCoords = tex.getImageTexCoords();
        
        textureTop = textureCoords.top();
        textureBottom = textureCoords.bottom();
        textureLeft = textureCoords.left();
        textureRight = textureCoords.right();
        
        this.glu.gluPerspective(this.state.getHorizontal_fov(), this.state.getWidth() / this.state.getHeight(), EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
        this.glu.gluLookAt(this.state.getCurrentCamera().getOrigin().getX(), 
        		this.state.getCurrentCamera().getOrigin().getY(), 
        		this.state.getCurrentCamera().getOrigin().getZ(), 
        		this.state.getCurrentCamera().getOrigin().getX(), 
        		this.state.getCurrentCamera().getOrigin().getY(), 
        		this.state.getCurrentCamera().getOrigin().getZ(), 0.0f, 1.0f, 0.0f);
        
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
}
