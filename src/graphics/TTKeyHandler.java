package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.awt.GLCanvas;

import domain.EnvironmentState;
import domain.Point3;
import domain.Vec3;

public class TTKeyHandler implements KeyListener, MouseMotionListener, MouseListener {
	
	protected GLCanvas canvas;
	protected int prevX;
	protected int prevY;
	
	public TTKeyHandler(GLCanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
        switch(arg0.getKeyCode()) {
        case KeyEvent.VK_SPACE:
        	EnvironmentState.getInstance().toggleFrameStop();
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

	@Override
	public void mouseDragged(MouseEvent e) {
		if(EnvironmentState.getInstance().isStopped()) {
			Point3 pt = EnvironmentState.getInstance().getUserHead().getOrigin().translate(new Vec3((this.prevX - e.getX()) / 5.0f, (this.prevY - e.getY()) / 5.0f, 0.0f));
			EnvironmentState.getInstance().getUserHead().setOrigin(pt);
			this.prevX = e.getX();
			this.prevY = e.getY();
		}		
	}

	@Override
	public void mouseMoved(MouseEvent e) {	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.prevX = e.getX();
		this.prevY = e.getY();		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		EnvironmentState.getInstance().getUserHead().setOrigin(EnvironmentState.getInstance().getCurrentCamera().getOrigin().translate(EnvironmentState.USER_DISPLACEMENT));
	}
}
