package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class Matrix9Test {

	@Test
	public void testMultiply() {
		Matrix9 i = Matrix9.identity();
		
		Tuple3 in = new Tuple3(1.0f, 1.0f, 1.0f);
		assertEquals(in, i.multiply(in));
		
		Matrix9 mat = new Matrix9(new float[] {2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f});
		assertEquals(in.multiply(6.0f), mat.multiply(in));
	}

	@Test
	public void testGetColumn() {
		Matrix9 i = Matrix9.identity();
		
		assertEquals(new Vec3(1.0f, 0.0f, 0.0f), i.getColumn(0));
		assertEquals(new Vec3(0.0f, 1.0f, 0.0f), i.getColumn(1));
		assertEquals(new Vec3(0.0f, 0.0f, 1.0f), i.getColumn(2));
	}

	@Test
	public void testSetColumn() {
		Matrix9 test = new Matrix9();
		
		test.setColumn(0, new Tuple3(1.0f, 0.0f, 0.0f));
		test.setColumn(1, new Tuple3(0.0f, 1.0f, 0.0f));
		test.setColumn(2, new Tuple3(0.0f, 0.0f, 1.0f));
		
		assertEquals(Matrix9.identity(), test);
	}

	@Test
	public void testInverted() {
		Matrix9 i = Matrix9.identity();
		
		assertEquals(i, i.inverted());
		
		float[] testArray = {1.0f / 3.0f, 1.0f / 3.0f, -1.0f / 3.0f, 2.0f / 15.0f, -1.0f / 15.0f, 4.0f / 15.0f, -1.0f / 5.0f, 3.0f / 5.0f, -2.0f / 5.0f};
		Matrix9 test = new Matrix9(testArray);
		
		float[] expectedArray = {2.0f, 1.0f, -1.0f, 0.0f, 3.0f, 2.0f, -1.0f, 4.0f, 1.0f};
		Matrix9 expected = new Matrix9(expectedArray);
		
		test = test.inverted();
		for(int j = 0; j < expected.indices.length; j++) {
			assertEquals("Index: " + j, expected.indices[j], test.indices[j], Tuple3.epsilon);
		}
	}

}
