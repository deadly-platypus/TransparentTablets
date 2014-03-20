package domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class CameraDeltaTest {

	@Test
	public void testGetDifference() {
		Point3 origin1 = new Point3();
		EnvironmentState state = EnvironmentState.getInstance();
		
		PPC cam1 = state.getCurrentCamera();
		cam1.pixels = makeRandomPixels();
		
		Vec3 trans = new Vec3(0.1f, 0.0f, 0.0f);
		Point3 origin2 = origin1.translate(trans);
		
		PPC cam2 = new PPC(state.getHorizontal_fov(), state.getWidth(), state.getHeight(), origin2);
		float pix2[] = new float[cam1.pixels.length];
		for(int y = 0; y < state.getHeight(); y++) {
			for(int x = 0; x < state.getWidth(); x++){
				Tuple3 t = cam1.project(new Point3(x, y, cam2.getView_corner().z));
				pix2[state.getWidth() * y + x] = cam1.getPixel((int)t.x, (int)t.y);
			}
		}
		cam2.pixels = pix2;
		
		CameraDelta cd = CameraDelta.getDifference(cam2, cam1);
		
		assertEquals(trans.x, cd.x_trans, 0.0f);
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
