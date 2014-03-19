package domain;

public class CameraDelta {
	float x_trans, y_trans, z_trans;
	float x_rot, y_rot, z_rot;
	
	protected CameraDelta() { 
		x_trans = 0.0f;
		y_trans = 0.0f;
		z_trans = 0.0f;
		
		x_rot = 0.0f;
		y_rot = 0.0f;
		z_rot = 0.0f;
	}
	
	static CameraDelta getDifference(PPC currentCamera, PPC previousCamera) {
		// TODO: implement this
		return null;
	}
}
