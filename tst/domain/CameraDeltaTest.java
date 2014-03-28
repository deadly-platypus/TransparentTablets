package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class CameraDeltaTest {

	@Test
	public void testGetDifference() {
		Point3 origin1 = new Point3();
		EnvironmentState state = EnvironmentState.getInstance();
	}
	
	private float[] makeRandomPixels() {
		EnvironmentState state = EnvironmentState.getInstance();
		// Make sure that we have enough pixels
		int size = state.getHeight() * 2 *state.getWidth() * 2;
		float result[] = new float[size];
		
		for(int i = 0; i < size; i++) {
			result[i] = (float) Math.random();
		}
		
		return result;
	}

}
