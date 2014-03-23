package graphics;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.texture.TextureCoords;

import domain.CameraDelta;

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
        
        CameraDelta delta = CameraDelta.getDifference(this.state.getCurrentCamera(), this.state.getPreviousCamera());        
        
        
        this.state.setPreviousCamera(this.state.getCurrentCamera());
	}
}
