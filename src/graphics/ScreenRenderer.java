package graphics;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.texture.TextureCoords;

import domain.CameraDelta;
import domain.EnvironmentState;
import domain.Point3;
import domain.Tuple3;
import domain.Vec3;
import domain.Wall;

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
//        Wall wall = this.state.getWall();
//        Point3 corners[] = wall.getCorners();
//        for(int i = 0; i < corners.length; i++) {
//        	Tuple3 tmp = 
//        }
        // Draw the wall
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        Vec3 rot = new Vec3(delta.getX_rot(), delta.getY_rot(), delta.getZ_rot());
        float rad = rot.distance();
        gl.glRotatef(rad, rot.getX(), rot.getY(), rot.getZ());
        gl.glTranslatef(delta.getX_trans(), delta.getY_trans(), delta.getZ_trans());
        
        
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        this.glu.gluPerspective(this.state.getHorizontal_fov(), this.state.getWidth() / this.state.getHeight(), EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
        
        this.glu.gluLookAt(this.state.getCurrentCamera().getOrigin().getX(), 
        		this.state.getCurrentCamera().getOrigin().getY(), 
        		this.state.getCurrentCamera().getOrigin().getZ(), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        
        this.state.setPreviousCamera(this.state.getCurrentCamera());
	}
}
