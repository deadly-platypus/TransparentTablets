package graphics;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import domain.EnvironmentState;

public class TTRenderer implements GLEventListener {
	private EnvironmentState state;
	private GLU glu;

	@Override
	public void init(GLAutoDrawable drawable) {
		this.state = EnvironmentState.getInstance();
		this.glu = new GLU();
		
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub

	}

}
