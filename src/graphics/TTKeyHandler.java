package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.awt.GLCanvas;

import domain.EnvironmentState;
import domain.Vec3;

public class TTKeyHandler implements KeyListener {
	
	protected GLCanvas canvas;
	
	public TTKeyHandler(GLCanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
        switch(arg0.getKeyCode()) {
        case KeyEvent.VK_UP:
        	EnvironmentState.getInstance().getCurrentCamera().setOrigin(EnvironmentState.getInstance().getCurrentCamera().getOrigin().translate(new Vec3(0.0f, 10.0f, 0.0f)));
        	break;
        case KeyEvent.VK_DOWN:
        	EnvironmentState.getInstance().getCurrentCamera().setOrigin(EnvironmentState.getInstance().getCurrentCamera().getOrigin().translate(new Vec3(0.0f, -10.0f, 0.0f)));
        	break;
        case KeyEvent.VK_LEFT:
        	EnvironmentState.getInstance().getCurrentCamera().setOrigin(EnvironmentState.getInstance().getCurrentCamera().getOrigin().translate(new Vec3(-10.0f, 0.0f, 0.0f)));
        	break;
        case KeyEvent.VK_RIGHT:
        	EnvironmentState.getInstance().getCurrentCamera().setOrigin(EnvironmentState.getInstance().getCurrentCamera().getOrigin().translate(new Vec3(10.0f, 0.0f, 0.0f)));
        	break;
        }
		this.canvas.display();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}