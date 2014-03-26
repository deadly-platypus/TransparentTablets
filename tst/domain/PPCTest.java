package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class PPCTest {

	@Test
	public void testToCameraCoords() {
		EnvironmentState state = EnvironmentState.getInstance();
		
		PPC cam = state.getCurrentCamera();
		
		Tuple3 testPt = cam.toCameraCoords(new Point3(cam.getOrigin().x, cam.getOrigin().y, 0.0f));
		assertNotNull(testPt);
		assertEquals(state.getWidth() / 2, testPt.getX(), Tuple3.epsilon);
		assertEquals(state.getHeight() / 2, testPt.getY(), Tuple3.epsilon);
		assertEquals(-cam.getView_corner().getZ(), testPt.getZ(), Tuple3.epsilon);
	}
	
	@Test
	public void testToWorldCoords() {
		EnvironmentState state = EnvironmentState.getInstance();
		Point3 original = state.getCurrentCamera().getOrigin();
		Tuple3 proj = state.getCurrentCamera().toCameraCoords(original);
		Tuple3 orig = state.getCurrentCamera().toWorldCoords(proj);
		
		assertEquals(original.x, orig.x, Tuple3.epsilon);
		assertEquals(original.y, orig.y, Tuple3.epsilon);
		assertEquals(original.z, orig.z, Tuple3.epsilon);
	}
}
