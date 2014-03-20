package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class WallTest {

	@Test
	public void testWall() {
		Wall wall = new Wall(new Point3(-100.0f, 100.0f, 0.0f), 
				new Point3(100.0f, 100.0f, 0.0f), 
				new Point3(-100.0f, -100.0f, 0.0f), 
				new Point3(100.0f, -100.0f, 0.0f));
		
		assertEquals(new Vec3(0.0f, 0.0f, 1.0f), wall.getNormal());
	}
	
	@Test
	public void testIntersect() {
		fail("Not implemented");
	}

}
