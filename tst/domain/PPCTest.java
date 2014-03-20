package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class PPCTest {

	@Test
	public void testProject() {
		EnvironmentState state = EnvironmentState.getInstance();
		
		PPC cam = state.getCurrentCamera();
		
		Tuple3 testPt = cam.project(cam.getOrigin());
		assertEquals(cam.getOrigin().getX(), testPt.getX(), Tuple3.epsilon);
		assertEquals(cam.getOrigin().getY(), testPt.getY(), Tuple3.epsilon);
		
		Tuple3 testVec = cam.project(cam.getWidth());
		assertEquals(cam.getWidth().getX(), testVec.getX(), Tuple3.epsilon);
		assertEquals(cam.getWidth().getY(), testVec.getY(), Tuple3.epsilon);
		assertEquals(cam.getWidth().getZ(), testVec.getZ(), Tuple3.epsilon);
	}

}
