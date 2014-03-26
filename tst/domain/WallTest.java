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
		Wall wall = new Wall(new Point3(-1.0f, 1.0f, 0.0f), 
				new Point3(1.0f, 1.0f, 0.0f), 
				new Point3(-1.0f, -1.0f, 0.0f), 
				new Point3(1.0f, -1.0f, 0.0f));
		
		Ray3D ray = new Ray3D(new Point3(0.0f, 0.0f, 2.0f), new Vec3(0.0f, 0.0f, -1.0f));
		
		Point3 test = wall.intersect(ray);
		assertNotNull(test);
		
		assertEquals(new Point3(0.0f, 0.0f, 0.0f), test);
		
		ray = new Ray3D(new Point3(0.0f, 0.0f, 2.0f), new Vec3(0.0f, 0.0f, 1.0f));
		test = wall.intersect(ray);
		assertNull(test);
		
		ray = new Ray3D(new Point3(0.0f, 0.0f, 2.0f), new Vec3(1.0f, 0.0f, 0.0f));
		test = wall.intersect(ray);
		assertNull(test);
	}

}
