package domain;

import static org.junit.Assert.*;
import graphics.ScreenRenderer;

import org.junit.Test;

public class Tuple3Test {

	@Test
	public void testAdd() {
		Tuple3 t1 = new Tuple3(1.0f, 1.0f, 1.0f);
		assertEquals(new Tuple3(2.0f, 2.0f, 2.0f), t1.add(new Tuple3(1.0f, 1.0f, 1.0f)));
	}

	@Test
	public void testSubtract() {
		Tuple3 t1 = new Tuple3(1.0f, 1.0f, 1.0f);
		assertEquals(new Tuple3(0.0f, 0.0f, 0.0f), t1.subtract(new Tuple3(1.0f, 1.0f, 1.0f)));
	}

	@Test
	public void testRotate() {
		Tuple3 x = new Tuple3(1.0f, 0.0f, 0.0f);
		
		Vec3 axis = new Vec3(0.0f, 0.0f, 1.0f);
		Tuple3 test = x.rotate(axis, (float)Math.PI / 2.0f);
		
		assertEquals(1.0f, test.y, Tuple3.epsilon);
		assertEquals(0.0f, test.x, Tuple3.epsilon);
		assertEquals(0.0f, test.z, Tuple3.epsilon);
		
		axis = new Vec3(0.0f, 1.0f, 0.0f);
		test = x.rotate(axis, (float)Math.PI / 2.0f);
		
		assertEquals(-1.0f, test.z, Tuple3.epsilon);
		assertEquals(0.0f, test.x, Tuple3.epsilon);
		assertEquals(0.0f, test.y, Tuple3.epsilon);
		
		PPC cam = EnvironmentState.getInstance().getCurrentCamera();
		Vec3 orig = new Vec3(cam.getView_corner());
		axis = new Vec3(ScreenRenderer.WIGGLE, 0.0f, 0.0f);
		axis.normalize();
		test = cam.getView_corner().rotate(axis , ScreenRenderer.ROTATION_AMT / 1000.0f);
		assertEquals(orig.getX(), test.x, Tuple3.epsilon);
		
		Vec3 y = new Vec3(0.0f, 1.0f, 0.0f);
		axis = new Vec3(1.0f, 0.0f, 0.0f);
		y = y.rotate(axis, (float)Math.PI / 2.0f).toVec3();
		assertEquals(0.0f, y.x, Tuple3.epsilon);
		assertEquals(0.0f, y.y, Tuple3.epsilon);
		assertEquals(1.0f, y.z, Tuple3.epsilon);
	}

}
