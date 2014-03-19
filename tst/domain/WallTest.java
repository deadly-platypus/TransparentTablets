package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WallTest {

	@Test
	public void testWallPoint3DPoint3DPoint3DPoint3D() {
		Wall wall = new Wall(new Point3(-100.0f, 100.0f, 0.0f), 
				new Point3(100.0f, 100.0f, 0.0f), 
				new Point3(-100.0f, -100.0f, 0.0f), 
				new Point3(100.0f, -100.0f, 0.0f));
		
		assertEquals(new Vec3(0.0f, 0.0f, 1.0f), wall.getNormal());
	}

}
