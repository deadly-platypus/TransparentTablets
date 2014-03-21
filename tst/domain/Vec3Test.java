package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class Vec3Test {

	@Test
	public void testMultiply() {
		Vec3 test = new Vec3(1.0f, 1.0f, 1.0f);
		test = test.multiply(2.0f);
		assertEquals(new Vec3(2.0f, 2.0f, 2.0f), test);
	}

	@Test
	public void testDivide() {
		Vec3 test = new Vec3(2.0f, 2.0f, 2.0f);
		test = test.divide(2.0f);
		
		assertEquals(new Vec3(1.0f, 1.0f, 1.0f), test);
	}

	@Test
	public void testNormalize() {
		Vec3 test = new Vec3(2.0f, 0.0f, 0.0f);
		test.normalize();
		
		assertEquals(new Vec3(1.0f, 0.0f, 0.0f), test);
	}

	@Test
	public void testDistance() {
		Vec3 test = new Vec3(3.0f, 4.0f, 0.0f);
		
		assertEquals(5.0f, test.distance(), 0.0f);
	}

	@Test
	public void testDistance2() {
		Vec3 test = new Vec3(3.0f, 4.0f, 0.0f);
		assertEquals(25.0f, test.distance2(), 0.0f);
	}

	@Test
	public void testDot() {
		Vec3 v1 = new Vec3(2.0f, 3.0f, 4.0f);
		Vec3 v2 = new Vec3(5.0f, 6.0f, 7.0f);
		
		assertEquals(2.0f * 5.0f + 3.0f * 6.0f + 4.0f * 7.0f, v1.dot(v2), 0.0f);
	}

	@Test
	public void testCross() {
		Vec3 v1 = new Vec3(1.0f, 0.0f, 0.0f);
		Vec3 v2 = new Vec3(0.0f, 1.0f, 0.0f);
		
		assertEquals(new Vec3(0.0f, 0.0f, 1.0f), v1.cross(v2));
	}

}
