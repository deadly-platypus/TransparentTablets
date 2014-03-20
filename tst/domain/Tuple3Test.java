package domain;

import static org.junit.Assert.*;

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
	}

}
