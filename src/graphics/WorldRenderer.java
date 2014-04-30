package graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import domain.CameraDelta;
import domain.EnvironmentState;
import domain.PPC;
import domain.Point3;
import domain.Ray3D;
import domain.Vec3;

public class WorldRenderer extends TTRenderer implements GLEventListener {

	public static float ROTATION_AMT 		= 0.005f;
	public static float ROTATION_WIGGLE		= 0.05f;
	public static float WIGGLE 				= 0.1f;
	
	private boolean firstFrame = true;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
//		gl.glFrustumf(-1.0f,
//				1.0f,
//				-1.0f,
//				1.0f,
//				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
		gl.glOrtho(this.state.orthoLeft,
				this.state.orthoRight,
				this.state.orthoBottom,
				this.state.orthoTop,
				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		super.reshape(drawable, x, y, width, height);
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
//		gl.glFrustumf(-1.0f,
//				1.0f,
//				-1.0f,
//				1.0f,
//				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
		gl.glOrtho(this.state.orthoLeft,
				this.state.orthoRight,
				this.state.orthoBottom,
				this.state.orthoTop,
				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		Date start = new Date();
		super.display(drawable);
        
		if(this.firstFrame) {
			this.firstFrame = false;
			this.state.getPreviousCamera().setPixels(this.state.getFrame());
			this.state.setCurrentCamera(this.state.getPreviousCamera().copy(false));
			this.state.getFrames().push(this.state.getPreviousCamera());
			this.state.getPreviousCamera().setSimulatedPixels(generateSimulatedView(false));
			this.drawCameraFrames(drawable);
			return;
		}
		
        if(!this.state.isStopped()) {
        	this.findAndPositionCamera();
        	//this.state.setUserHead(new PPC(this.state.getHorizontal_fov(), this.state.getSimulatedWidth(), this.state.getSimulatedHeight(), this.state.getCurrentCamera().getOrigin().translate(EnvironmentState.USER_DISPLACEMENT)));
        	
        	this.state.getCurrentCamera().setSimulatedPixels(generateSimulatedView(false));
        	this.state.getFrames().push(this.state.getCurrentCamera());
        }
        
        this.drawCameraFrames(drawable);
        //this.drawWholeFrame(drawable);
        Date end = new Date();
        
        System.out.println(String.format("head location: %f %f %f", this.state.getUserHead().getOrigin().getX(), this.state.getUserHead().getOrigin().getY(), this.state.getUserHead().getOrigin().getZ()));
        System.out.println(String.format("Elapsed time: %d ms\n", end.getTime() - start.getTime()));
        this.state.setPreviousCamera(this.state.getCurrentCamera());
	}
	
	private void drawCameraFrames(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(this.state.orthoLeft,
				this.state.orthoRight,
				this.state.orthoBottom,
				this.state.orthoTop,
				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		if(this.state.isStopped()) {
			Texture frameTex = AWTTextureIO.newTexture(drawable.getGLProfile(), generateSimulatedView(false), false);
			
			PPC cam = this.state.getCurrentCamera();
			
			Point3 topLeft = cam.getOrigin().translate(cam.getView_corner());
			Point3 bottomLeft = cam.getOrigin().translate(cam.getView_corner()).add(cam.getHeight().multiply(this.state.getSimulatedHeight())).toPoint3();
			Point3 bottomRight = cam.getOrigin().translate(cam.getView_corner()).add(cam.getHeight().multiply(this.state.getSimulatedHeight())).add(cam.getWidth().multiply(this.state.getSimulatedWidth())).toPoint3();
			Point3 topRight = cam.getOrigin().translate(cam.getView_corner()).add(cam.getWidth().multiply(this.state.getSimulatedWidth())).toPoint3();
			
			Ray3D tlRay = new Ray3D(cam.getOrigin(), topLeft);
			Ray3D blRay = new Ray3D(cam.getOrigin(), bottomLeft);
			Ray3D brRay = new Ray3D(cam.getOrigin(), bottomRight);
			Ray3D trRay = new Ray3D(cam.getOrigin(), topRight);
			
			topLeft = this.state.getWall().intersect(tlRay);
			bottomLeft = this.state.getWall().intersect(blRay);
			bottomRight = this.state.getWall().intersect(brRay);
			topRight = this.state.getWall().intersect(trRay);
			
			frameTex.enable(gl);
			frameTex.bind(gl);
			
			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glEnable(GL.GL_TEXTURE_2D);
			
			gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomLeft.getX(), bottomLeft.getY(), bottomLeft.getZ());
			gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomRight.getX(), bottomRight.getY(), bottomRight.getZ());
			gl.glTexCoord2f(1, 1); gl.glVertex3f(topRight.getX(), topRight.getY(), topRight.getZ());
			gl.glTexCoord2f(0, 1); gl.glVertex3f(topLeft.getX(), topLeft.getY(), topLeft.getZ());
			
			gl.glEnd();
			
			frameTex.destroy(gl);
		} else {
//			this.glu.gluLookAt(this.state.getUserHead().getOrigin().getX(), 
//					this.state.getUserHead().getOrigin().getY(), 
//					this.state.getUserHead().getOrigin().getZ(), 
//					this.state.getUserHead().getLook_at().getX(), 
//					this.state.getUserHead().getLook_at().getY(), 
//					this.state.getUserHead().getLook_at().getZ(), 
//					0.0f, 
//					1.0f, 
//					0.0f);
			gl.glDisable(GL.GL_DEPTH_TEST);
			
			for(PPC cam : this.state.getFrames()) {
				gl.glPushMatrix();
				Texture frameTex = AWTTextureIO.newTexture(drawable.getGLProfile(), cam.getSimulatedPixels(), false);
				frameTex.enable(gl);
				frameTex.bind(gl);
				
				gl.glBegin(GL2GL3.GL_QUADS);
				gl.glEnable(GL.GL_TEXTURE_2D);
				
				Point3 topLeft = cam.getOrigin().translate(cam.getView_corner());
				Point3 bottomLeft = cam.getOrigin().translate(cam.getView_corner()).add(cam.getHeight().multiply(this.state.getSimulatedHeight())).toPoint3();
				Point3 bottomRight = cam.getOrigin().translate(cam.getView_corner()).add(cam.getHeight().multiply(this.state.getSimulatedHeight())).add(cam.getWidth().multiply(this.state.getSimulatedWidth())).toPoint3();
				Point3 topRight = cam.getOrigin().translate(cam.getView_corner()).add(cam.getWidth().multiply(this.state.getSimulatedWidth())).toPoint3();
				
				Ray3D tlRay = new Ray3D(cam.getOrigin(), topLeft);
				Ray3D blRay = new Ray3D(cam.getOrigin(), bottomLeft);
				Ray3D brRay = new Ray3D(cam.getOrigin(), bottomRight);
				Ray3D trRay = new Ray3D(cam.getOrigin(), topRight);
				
//				topLeft = this.state.getWall().intersect(tlRay).multiply(cam.getFocal_length() * 10.0f).toPoint3();
//				bottomLeft = this.state.getWall().intersect(blRay).multiply(cam.getFocal_length() * 10.0f).toPoint3();
//				bottomRight = this.state.getWall().intersect(brRay).multiply(cam.getFocal_length() * 10.0f).toPoint3();
//				topRight = this.state.getWall().intersect(trRay).multiply(cam.getFocal_length() * 10.0f).toPoint3();
				
				topLeft = this.state.getWall().intersect(tlRay);
				bottomLeft = this.state.getWall().intersect(blRay);
				bottomRight = this.state.getWall().intersect(brRay);
				topRight = this.state.getWall().intersect(trRay);
				
//				if(topLeft.getY() > this.state.orthoTop) {
//					this.state.orthoTop *= 2.0f;
//				} 
//				
//				if(bottomLeft.getY() < this.state.orthoBottom) {
//					this.state.orthoBottom *= 2.0f;
//				}
//				
//				if(bottomRight.getX() > this.state.orthoRight) {
//					this.state.orthoRight *= 2.0f;
//				}
//				
//				if(bottomLeft.getX() < this.state.orthoLeft) {
//					this.state.orthoLeft *= 2.0f;
//				}
				
				gl.glTexCoord2f(0, 0); gl.glVertex3f(bottomLeft.getX(), bottomLeft.getY() - 0.5f, bottomLeft.getZ());
				gl.glTexCoord2f(1, 0); gl.glVertex3f(bottomRight.getX(), bottomRight.getY() - 0.5f, bottomRight.getZ());
				gl.glTexCoord2f(1, 1); gl.glVertex3f(topRight.getX(), topRight.getY() - 0.5f, topRight.getZ());
				gl.glTexCoord2f(0, 1); gl.glVertex3f(topLeft.getX(), topLeft.getY() - 0.5f, topLeft.getZ());
				
				gl.glEnd();
				
				frameTex.destroy(gl);
				gl.glPopMatrix();
			}
		}
		gl.glPopMatrix();
	}
	
	private BufferedImage generateSimulatedView(boolean createBorder) {
		BufferedImage sub = new BufferedImage(this.state.getSimulatedWidth(), this.state.getSimulatedHeight(), this.state.getFrame().getType());
		
		int xoffset = this.state.getFrame().getWidth() / 2 - sub.getWidth() / 2;
		int yoffset = this.state.getFrame().getHeight() / 2 - sub.getHeight() / 2;
		
		int borderColor = 0xFF0000FF; // Red...
		int borderWidth = 4;
		
		// Create the subtexture
		for(int u = 0; u < this.state.getSimulatedWidth(); u++) {
			for(int v = 0; v < this.state.getSimulatedHeight(); v++) {
				// Create a border
				if(createBorder && (u < borderWidth || u > this.state.getSimulatedWidth() - borderWidth || v < borderWidth || v > this.state.getSimulatedHeight() - borderWidth)) {
					sub.setRGB(u, v, borderColor);
				} else {
					Vec3 direction = this.state.getUserHead().getOrigin().add(this.state.getUserHead().getView_corner()).add(this.state.getUserHead().getWidth().multiply(u)).add(this.state.getUserHead().getHeight().multiply(v)).toVec3();
					Ray3D wsRay = new Ray3D(this.state.getUserHead().getOrigin(), direction);
					Point3 intersect = this.state.getWall().intersect(wsRay);
					
					try{
						intersect = this.state.getUserHead().toCameraCoords(intersect).toPoint3();
						int color = this.state.getFrame().getRGB((int)intersect.getX() + xoffset, (int)intersect.getY() + yoffset);
						sub.setRGB(u, this.state.getSimulatedHeight() - v, color);
					} catch(Exception e) {
						
					}
				}
			}
		}
		return sub;
		//return this.createRenderableImage(sub);
	}

	private void findAndPositionCamera() {
        CameraDelta delta = this.findCameraDelta();
        System.out.println(delta);
		
		PPC cam = PPC.makeAndTransformCamera(this.state.getCurrentCamera(),
				delta.getX_trans(), delta.getY_trans(), delta.getZ_trans(),
				delta.getX_rot(), delta.getY_rot(), delta.getZ_rot());
		cam.setPixels(this.state.getFrame());
		cam.setCamId(PPC.nextId());
		
		
		this.state.setUserHead(PPC.makeAndTransformCamera(this.state.getCurrentCamera(),
				delta.getX_trans() + EnvironmentState.USER_DISPLACEMENT.getX(), delta.getY_trans() + EnvironmentState.USER_DISPLACEMENT.getY(), delta.getZ_trans() + EnvironmentState.USER_DISPLACEMENT.getZ(),
				delta.getX_rot(), delta.getY_rot(), delta.getZ_rot()));
		this.state.setCurrentCamera(cam);
	}
	
	private CameraDelta findCameraDelta() {
		if (this.state.getPreviousCamera() == null || this.state.getPreviousCamera().getPixels() == null) {
			return new CameraDelta();
		}

		float min_transx = 0.0f;
		float min_transy = 0.0f;
		float min_transz = 0.0f;
		float min_rotx = 0.0f;
		float min_roty = 0.0f;
		float min_rotz = 0.0f;

		float min_error = Float.MAX_VALUE;

		float trans_delta = WorldRenderer.WIGGLE / 1.0f;
		float rot_delta = WorldRenderer.ROTATION_WIGGLE / 1.0f;
		for (float x_trans = -WorldRenderer.WIGGLE; x_trans <= WorldRenderer.WIGGLE; x_trans += trans_delta) {
			for (float y_trans = -WorldRenderer.WIGGLE; y_trans <= WorldRenderer.WIGGLE; y_trans += trans_delta) {
				for (float z_trans = -WorldRenderer.WIGGLE; z_trans <= WorldRenderer.WIGGLE; z_trans += trans_delta) {
					for (float x_rot = -WorldRenderer.ROTATION_WIGGLE; x_rot <= WorldRenderer.ROTATION_WIGGLE; x_rot += rot_delta) {
						for (float y_rot = -WorldRenderer.ROTATION_WIGGLE; y_rot <= WorldRenderer.ROTATION_WIGGLE; y_rot += rot_delta) {
							for (float z_rot = -WorldRenderer.ROTATION_WIGGLE; z_rot <= WorldRenderer.ROTATION_WIGGLE; z_rot += rot_delta) {
								float error = error(x_trans,
										y_trans, z_trans, x_rot, y_rot, z_rot);
								if (error >= 0.0f && error < min_error) {
									min_error = error;
									min_transx = x_trans;
									min_transy = y_trans;
									min_transz = z_trans;
									min_rotx = x_rot;
									min_roty = y_rot;
									min_rotz = z_rot;
								} else if (error == min_error) {
									// Keep the smallest change
									float tmp_x = Math.min(Math.abs(x_trans),
											Math.abs(min_transx));
									if (tmp_x == Math.abs(x_trans)) {
										min_transx = x_trans;
									}
									tmp_x = Math.min(Math.abs(x_rot),
											Math.abs(min_rotx));
									if (tmp_x == Math.abs(x_rot)) {
										min_rotx = x_rot;
									}

									float tmp_y = Math.min(Math.abs(y_trans),
											Math.abs(min_transy));
									if (tmp_y == Math.abs(y_trans)) {
										min_transy = y_trans;
									}
									tmp_y = Math.min(Math.abs(y_rot),
											Math.abs(min_roty));
									if (tmp_y == Math.abs(y_rot)) {
										min_roty = y_rot;
									}

									float tmp_z = Math.min(Math.abs(z_trans),
											Math.abs(min_transz));
									if (tmp_z == Math.abs(z_trans)) {
										min_transz = z_trans;
									}
									tmp_z = Math.min(Math.abs(z_rot),
											Math.abs(min_rotz));
									if (tmp_z == Math.abs(z_rot)) {
										min_rotz = z_rot;
									}
								}
							}
						}
					}
				}
			}
		}

		return new CameraDelta(min_transx, min_transy, min_transz, min_rotx,
				min_roty, min_rotz, min_error);
	}

	private float error(float x_trans, float y_trans,
			float z_trans, float x_rot, float y_rot, float z_rot) {
		float err = 0.0f;
		int pixelsCounted = 0;

		PPC newCam = PPC.makeAndTransformCamera(
				this.state.getPreviousCamera(), x_trans, y_trans, z_trans,
				x_rot, y_rot, z_rot);

		for (int u = 0; u < this.state.getFrame().getWidth(); u += this.state.getFrame().getWidth() / 10.0f) {
			for (int v = 0; v < this.state.getFrame().getHeight(); v++) {
				Vec3 direction = newCam.getOrigin().add(newCam.getView_corner()).add(newCam.getWidth().multiply(u)).add(newCam.getHeight().multiply(v)).toVec3();
				Ray3D wsRay = new Ray3D(newCam.getOrigin(), direction);
				Point3 intersect = this.state.getWall().intersect(wsRay);

				if (intersect != null) {
					Point3 intersectPrev = this.state.getPreviousCamera()
							.toCameraCoords(intersect).toPoint3();

					try {
						// Find the location in the previous camera's pixels
						// sample
						int prevs[] = { (int)intersectPrev.getX(), (int)intersectPrev.getY() };
						float tmpErr = 0.0f;

						// Read in channels separately
						int prevByte = this.state
								.getPreviousCamera()
								.getPixels()
								.getRGB(prevs[0], (prevs[1]));
						int currByte = this.state.getFrame().getRGB(u, v);
						
						Color prevColor = new Color(prevByte);
						Color currColor = new Color(currByte);
						
						int prevRed = prevColor.getRed();
						int prevBlue = prevColor.getBlue();
						int prevGreen = prevColor.getGreen();
						
						int currRed = currColor.getRed();
						int currBlue = currColor.getBlue();
						int currGreen = currColor.getGreen();
						
						tmpErr += (prevRed - currRed) * (prevRed - currRed);
						tmpErr += (prevBlue - currBlue) * (prevBlue - currBlue);
						tmpErr += (prevGreen - currGreen) * (prevGreen - currGreen);
						
						//err += tmpErr / 3.0f;
						err += tmpErr;
						
						pixelsCounted++;
					} catch (Exception e) {
						// The sample isn't available everywhere so move on....
						//System.out.println(String.format("error: %f %f %f - %f %f %f", x_trans, y_trans, z_trans, x_rot, y_rot, z_rot));
					}
				}
			}
		}

		if (pixelsCounted != 0) {
			err = (float) Math.sqrt(err);
			
			return err / (3 * pixelsCounted);
		} else {
			return -1.0f;
		}
	}
	
//	private void drawWholeFrame(GLAutoDrawable drawable) {
//	// Draw the frame as a texture quad
//	GL2 gl = drawable.getGL().getGL2();
//	gl.glViewport(0, 0, this.state.getFrame().getWidth(), this.state.getFrame().getHeight());
//	gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//	gl.glPushMatrix();
//	gl.glLoadIdentity();
//	this.glu.gluLookAt(this.state.getUserHead().getOrigin().getX(), 
//			this.state.getUserHead().getOrigin().getY(), 
//			this.state.getUserHead().getOrigin().getZ(), 
//			this.state.getUserHead().getLook_at().getX(), 
//			this.state.getUserHead().getLook_at().getY(), 
//			this.state.getUserHead().getLook_at().getZ(), 
//			0.0f, 
//			1.0f, 
//			0.0f);
//	gl.glDisable(GL.GL_DEPTH_TEST);
//	
//	BufferedImage renderable = createRenderableImage(this.state.getFrame());
//	Texture frameTex = AWTTextureIO.newTexture(drawable.getGLProfile(), renderable, false);
//	frameTex.enable(gl);
//	frameTex.bind(gl);
//	gl.glEnable(GL.GL_TEXTURE_2D);
//	
//	// Draw the calculated view
//	this.drawSimulatedView(drawable);
//	
//	gl.glBegin(GL2GL3.GL_QUADS);
//	gl.glTexCoord2f(0, 0); gl.glVertex3f(this.state.getWall().getBottomLeft().getX(), this.state.getWall().getBottomLeft().getY(), this.state.getWall().getBottomLeft().getZ());
//	gl.glTexCoord2f(1, 0); gl.glVertex3f(this.state.getWall().getBottomRight().getX(), this.state.getWall().getBottomRight().getY(), this.state.getWall().getBottomRight().getZ());
//	gl.glTexCoord2f(1, 1); gl.glVertex3f(this.state.getWall().getTopRight().getX(), this.state.getWall().getTopRight().getY(), this.state.getWall().getTopLeft().getZ());
//	gl.glTexCoord2f(0, 1); gl.glVertex3f(this.state.getWall().getTopLeft().getX(), this.state.getWall().getTopLeft().getY(), this.state.getWall().getTopLeft().getZ());
//	gl.glEnd();
//	
//	gl.glPopMatrix();
//	gl.glDisable(GL.GL_TEXTURE_2D);
//	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
//	
//	frameTex.destroy(gl);
//}
	
//	private void drawSimulatedView(GLAutoDrawable drawable) {
//		GL2 gl = drawable.getGL().getGL2();
//		
//		// Create the subtexture
//		BufferedImage sub = this.generateSimulatedView(true);
//		// Draw the new texture
//		int xoffset = this.state.getFrame().getWidth() / 2 - sub.getWidth() / 2;
//		int yoffset = this.state.getFrame().getHeight() / 2 - sub.getHeight() / 2;
//		gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, xoffset, yoffset, sub.getWidth(), sub.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, createBuffer(sub));
//	}
	
//	private ByteBuffer createBuffer(BufferedImage bi) {
//		byte src[] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
//		ByteBuffer result = ByteBuffer.allocate(src.length);
//		result.put(src, 0, src.length);
//		result.rewind();
//		return result;
//	}
	
//	private BufferedImage produceCameraPixels() {
//		BufferedImage bi = null;
//		if(this.state.isUseCapturedImage()) {
//			bi = this.state.getFrame().getSubimage(this.state.getFrame().getWidth() / 2 - this.state.getSimulatedWidth() / 2, this.state.getFrame().getHeight() / 2 - this.state.getSimulatedHeight() / 2, this.state.getSimulatedWidth(), this.state.getSimulatedHeight());
//		}
//		
//		return bi;
//	}
//	
}
