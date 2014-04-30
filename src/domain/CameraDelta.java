package domain;

public class CameraDelta {
	float x_trans, y_trans, z_trans;
	float x_rot, y_rot, z_rot;
	float error;
	
	public CameraDelta() {
		this.x_trans = 0.0f;
		this.y_trans = 0.0f;
		this.z_trans = 0.0f;
		
		this.x_rot = 0.0f;
		this.y_rot = 0.0f;
		this.z_rot = 0.0f;
	}
	
	public CameraDelta(float x_trans, float y_trans, float z_trans, float x_rot, float y_rot, float z_rot, float error) { 
		this.x_trans = x_trans;
		this.y_trans = y_trans;
		this.z_trans = z_trans;
		
		this.x_rot = x_rot;
		this.y_rot = y_rot;
		this.z_rot = z_rot;
		
		this.error = error;
	}
	
	public String toString() {
		return String.format("Translate: (%f %f %f)\tRotate: (%f %f %f)\tError: %f", x_trans, y_trans, z_trans, x_rot, y_rot, z_rot, error);
	}

	public float getX_trans() {
		return x_trans;
	}

	public void setX_trans(float x_trans) {
		this.x_trans = x_trans;
	}

	public float getY_trans() {
		return y_trans;
	}

	public void setY_trans(float y_trans) {
		this.y_trans = y_trans;
	}

	public float getZ_trans() {
		return z_trans;
	}

	public void setZ_trans(float z_trans) {
		this.z_trans = z_trans;
	}

	public float getX_rot() {
		return x_rot;
	}

	public void setX_rot(float x_rot) {
		this.x_rot = x_rot;
	}

	public float getY_rot() {
		return y_rot;
	}

	public void setY_rot(float y_rot) {
		this.y_rot = y_rot;
	}

	public float getZ_rot() {
		return z_rot;
	}

	public void setZ_rot(float z_rot) {
		this.z_rot = z_rot;
	}

	public float getError() {
		return error;
	}

	public void setError(float error) {
		this.error = error;
	}
}
