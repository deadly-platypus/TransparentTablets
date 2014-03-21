package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class PPCTest {

	@Test
	public void testProject() {
		EnvironmentState state = EnvironmentState.getInstance();
		
		PPC cam = state.getCurrentCamera();
		
		Tuple3 testPt = cam.project(new Point3(cam.getOrigin().x, cam.getOrigin().y, 0.0f));
		assertNotNull(testPt);
		assertEquals(state.getWidth() / 2, testPt.getX(), Tuple3.epsilon);
		assertEquals(state.getHeight() / 2, testPt.getY(), Tuple3.epsilon);
		assertEquals(-cam.getView_corner().getZ(), testPt.getZ(), Tuple3.epsilon);
	}

}
