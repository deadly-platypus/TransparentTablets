package domain;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

public class CameraDelta {
	float x_trans, y_trans, z_trans;
	float x_rot, y_rot, z_rot;
	
	public CameraDelta(float x_trans, float y_trans, float z_trans, float x_rot, float y_rot, float z_rot) { 
		this.x_trans = x_trans;
		this.y_trans = y_trans;
		this.z_trans = z_trans;
		
		this.x_rot = x_rot;
		this.y_rot = y_rot;
		this.z_rot = z_rot;
	}
	
	public String toString() {
		return String.format("Translate: (%f %f %f)\tRotate: (%f %f %f)", x_trans, y_trans, z_trans, x_rot, y_rot, z_rot);
	}
	
	public static CameraDelta getDifference(PPC currentCamera, PPC previousCamera, GL2 gl) {
		float min_transx 	= 0.0f;
		float min_transy 	= 0.0f;
		float min_transz 	= 0.0f;
		float min_rotx 		= 0.0f;
		float min_roty		= 0.0f;
		float min_rotz		= 0.0f;
		
		float min_error		= Float.MAX_VALUE;
		
		float wiggle = 0.5f; // TODO: Should I be using the same value for everything?
		float delta = wiggle / 5.0f;
		for(float x_trans = -wiggle; x_trans < wiggle; x_trans += delta) {
			for(float y_trans = -wiggle; y_trans < wiggle; y_trans += delta) {
				for(float z_trans = -wiggle; z_trans < wiggle; z_trans += delta) {
					for(float x_rot = -wiggle; x_rot < wiggle; x_rot += delta) {
						for(float y_rot = -wiggle; y_rot < wiggle; y_rot += delta) {
							for(float z_rot = -wiggle; z_rot < wiggle; z_rot += delta) {
								float error = CameraDelta.error(gl, currentCamera, previousCamera, min_error, x_trans, y_trans, z_trans, x_rot, y_rot, z_rot);
								if(error < min_error) {
									min_error = error;
									min_transx = x_trans;
									min_transy = y_trans;
									min_transz = z_trans;
									min_rotx = x_rot;
									min_roty = y_rot;
									min_rotz = z_rot;
								}
							}
						}
					}
				}
			}
		}
		
		return new CameraDelta(min_transx, min_transy, min_transz, min_rotx, min_roty, min_rotz);
	}
	
	
	
	private static float error(GL2 gl, PPC currentCamera, PPC previousCamera, float min_error, float x_trans, float y_trans, float z_trans, float x_rot, float y_rot, float z_rot) {
		float error = 0.0f;
		EnvironmentState state = null;
		GLU glu = new GLU();
		try {
			state = EnvironmentState.getInstance();
		} catch (Exception e) {
			// TODO: handle this properly
			System.out.println(e.getMessage());
		}
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(state.getHorizontal_fov(), state.getWidth() / state.getHeight(), EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
		
		Tuple3 tmp = currentCamera.getOrigin().translate(new Vec3(x_trans, y_trans, z_trans));
		Vec3 rot = new Vec3(x_rot, y_rot, z_rot);
		float angle = rot.distance();
		rot.normalize();
		tmp = tmp.rotate(rot, angle);
		
		glu.gluLookAt(tmp.getX(), tmp.getY(), tmp.getZ(), tmp.getX(), tmp.getY(), -1.0f, 0.0f, 1.0f, 0.0f);
		
		
		
		return error;
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
}
