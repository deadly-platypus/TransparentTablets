package graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.util.texture.TextureCoords;

import domain.CameraDelta;
import domain.EnvironmentState;
import domain.PPC;
import domain.Point3;
import domain.Ray3D;
import domain.Vec3;

public class ScreenRenderer extends TTRenderer implements GLEventListener {

	private GL2 gl = null;
	private int fboId;
	private int render_buf;

	public static float ROTATION_AMT 		= 0.1f;
	public static float WIGGLE 				= 0.1f;
	
	public static final int PIXEL_TYPE 		= GL.GL_UNSIGNED_BYTE;
	public static final int PIXEL_FORMAT	= 1;

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		gl = drawable.getGL().getGL2();

		//gl.glViewport(0, 0, this.state.getWidth(), this.state.getHeight());

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glFrustumf(this.state.getCurrentCamera().getView_corner().getX(),
				-this.state.getCurrentCamera().getView_corner().getX(),
				-this.state.getCurrentCamera().getView_corner().getY(),
				this.state.getCurrentCamera().getView_corner().getY(),
				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		
		IntBuffer tmpFbo = IntBuffer.allocate(1);
		gl.glGenFramebuffers(1, tmpFbo);
		this.fboId = tmpFbo.get();
		
		IntBuffer tmpRb = IntBuffer.allocate(1);
		gl.glGenRenderbuffers(1, tmpRb);
		this.render_buf = tmpRb.get();
		gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, this.render_buf);
		//gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_RGB, this.state.getWidth(), this.state.getHeight());
		
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, this.fboId);
		gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_RENDERBUFFER, this.render_buf);
		
		int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
		if (status != GL.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error making FBObject: "
					+ FBObject.getStatusString(status));
			System.exit(status);
		}

		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		super.display(drawable);
		
		BufferedImage buffer = this.produceCameraPixels(this.state
				.getCurrentCamera(), Integer.toString(this.state.getCounter()));
		this.state.getCurrentCamera().setPixels(buffer);

		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		//gl.glDrawPixels(this.state.getWidth(), this.state.getHeight(),
			//	GL.GL_RGB, GL.GL_UNSIGNED_BYTE, this.state.getCurrentCamera().getByteBuffer());

		CameraDelta delta = this.findCameraDelta();
		System.out.println(delta);

		this.state.setPreviousCamera(this.state.getCurrentCamera());
		
		PPC cam = this.makeAndTransformCamera(this.state.getPreviousCamera(),
				delta.getX_trans(), delta.getY_trans(), delta.getZ_trans(),
				delta.getX_rot(), delta.getY_rot(), delta.getZ_rot());
		
		this.state.setCurrentCamera(cam);
	}

	private void drawWall() {
		TextureCoords textureCoords = null;
		Float textureTop = null;
		Float textureBottom = null;
		Float textureLeft = null;
		Float textureRight = null;

		// Render the wall
		if (this.state.getWall().getTex() != null) {
			this.state.getWall().getTex().enable(gl);
			this.state.getWall().getTex().bind(gl);

			textureCoords = this.state.getWall().getTex().getImageTexCoords();
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();
		}

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPushMatrix();

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBegin(GL2GL3.GL_QUADS);

		if(textureCoords != null) {
        	gl.glTexCoord2f(textureLeft, textureTop);
        }
        gl.glVertex3f(this.state.getWall().getTopLeft().getX(), this.state.getWall().getTopLeft().getY(), this.state.getWall().getTopLeft().getZ());
        
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureRight, textureTop);
        }
        gl.glVertex3f(this.state.getWall().getTopRight().getX(), this.state.getWall().getTopRight().getY(), this.state.getWall().getTopLeft().getZ());
        
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureRight, textureBottom);
        }
        gl.glVertex3f(this.state.getWall().getBottomRight().getX(), this.state.getWall().getBottomRight().getY(), this.state.getWall().getBottomRight().getZ());
        
        if(textureCoords != null) {
        	gl.glTexCoord2f(textureLeft, textureBottom);
        }
        gl.glVertex3f(this.state.getWall().getBottomLeft().getX(), this.state.getWall().getBottomLeft().getY(), this.state.getWall().getBottomLeft().getZ());

		gl.glEnd();

		gl.glPopMatrix();
	}

	private BufferedImage produceCameraPixels(PPC camera, String fileName) {
		BufferedImage bi = null;
//		if(this.state.isUseCapturedImage()) {
//			bi = this.state.getFrame().getSubimage(this.state.getFrame().getWidth() / 2 - this.state.getWidth() / 2, this.state.getFrame().getHeight() / 2 - this.state.getHeight() / 2, this.state.getWidth(), this.state.getHeight());
//		} else {
//			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//			gl.glLoadIdentity();
//			this.glu.gluLookAt(camera.getOrigin().getX(), camera.getOrigin().getY(), camera.getOrigin().getZ(), camera.getLook_at().getX(),
//					camera.getLook_at().getY(), camera.getLook_at().getZ(), 0.0f, 1.0f, 0.0f);
//			
//			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, this.fboId);
//	
//			drawWall();
//	
//			// Copy the pixels
//			ByteBuffer buffer = ByteBuffer.allocate(3
//					* this.state.getHeight() * this.state.getWidth());
//			gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0);
//			gl.glReadPixels(0, 0, this.state.getWidth(), this.state.getHeight(),
//					GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
//			
//			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
//			
//			bi = new BufferedImage(this.state.getWidth(),
//					this.state.getHeight(), BufferedImage.TYPE_INT_RGB);
//			int[] bd = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
//	
//			for (int y = 0; y < this.state.getHeight(); y++) {
//				for (int x = 0; x < this.state.getWidth(); x++) {
//					int r = buffer.get() & 0xFF;
//					int g = buffer.get() & 0xFF;
//					int b = buffer.get() & 0xFF;
//	
//					bd[(this.state.getHeight() - y - 1) * this.state.getWidth() + x] = (r << 16)
//							| (g << 8) | b;// | 0xFF000000;
//				}
//			}
//			
//		}
//		if(this.state.isOutputPixels()) {
//			outputScreenshot(bi, fileName);
//		}
		return bi;
	}

	private CameraDelta findCameraDelta() {
		if (this.state.getPreviousCamera() == null) {
			return new CameraDelta();
		}

		float min_transx = 0.0f;
		float min_transy = 0.0f;
		float min_transz = 0.0f;
		float min_rotx = 0.0f;
		float min_roty = 0.0f;
		float min_rotz = 0.0f;

		float min_error = Float.MAX_VALUE;

		float delta = ScreenRenderer.WIGGLE;
		for (float x_trans = -ScreenRenderer.WIGGLE; x_trans <= ScreenRenderer.WIGGLE; x_trans += delta) {
			for (float y_trans = -ScreenRenderer.WIGGLE; y_trans <= ScreenRenderer.WIGGLE; y_trans += delta) {
				for (float z_trans = -ScreenRenderer.WIGGLE; z_trans <= ScreenRenderer.WIGGLE; z_trans += delta) {
					for (float x_rot = -ScreenRenderer.WIGGLE; x_rot <= ScreenRenderer.WIGGLE; x_rot += delta) {
						for (float y_rot = -ScreenRenderer.WIGGLE; y_rot <= ScreenRenderer.WIGGLE; y_rot += delta) {
							for (float z_rot = -ScreenRenderer.WIGGLE; z_rot <= ScreenRenderer.WIGGLE; z_rot += delta) {
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

		PPC newCam = this.makeAndTransformCamera(
				this.state.getPreviousCamera(), x_trans, y_trans, z_trans,
				x_rot, y_rot, z_rot);

//		for (int v = 0; v < this.state.getHeight(); v+=this.state.getHeight() / 10) {
//			for (int u = 0; u < this.state.getWidth(); u++) {
////				if(u == 83 && v == 125) {
////					System.out.println("stop");
////				}
//				Vec3 direction = (newCam.getWidth().multiply(u).add(newCam.getHeight().multiply(v).add(newCam.getView_corner()))).toVec3();
//				Ray3D wsRay = new Ray3D(newCam.getOrigin(), direction);
//				Point3 intersect = this.state.getWall().intersect(wsRay);
//
//				if (intersect != null) {
//					Point3 intersectPrev = this.state.getPreviousCamera()
//							.toCameraCoords(intersect).toPoint3();
//
//					try {
//						// Find the location in the previous camera's pixels
//						// sample
//						int prevs[] = { (int)intersectPrev.getX(), (int)intersectPrev.getY() };
//						float tmpErr = 0.0f;
//
//						// Read in channels separately
//						int prevByte = this.state
//								.getPreviousCamera()
//								.getPixels()
//								.getRGB(prevs[0], (prevs[1]));
//						int currByte = this.state
//								.getCurrentCamera()
//								.getPixels()
//								.getRGB(u, (v));
//						
//						Color prevColor = new Color(prevByte);
//						Color currColor = new Color(currByte);
//						
//						int prevRed = prevColor.getRed();
//						int prevBlue = prevColor.getBlue();
//						int prevGreen = prevColor.getGreen();
//						
//						int currRed = currColor.getRed();
//						int currBlue = currColor.getBlue();
//						int currGreen = currColor.getGreen();
//						
//						tmpErr += (prevRed - currRed) * (prevRed - currRed);
//						tmpErr += (prevBlue - currBlue) * (prevBlue - currBlue);
//						tmpErr += (prevGreen - currGreen) * (prevGreen - currGreen);
//						
//						err += tmpErr / 3.0f;
//
//						pixelsCounted++;
//					} catch (Exception e) {
//						// The sample isn't available everywhere so move on....
//					}
//				}
//			}
//		}

		if (pixelsCounted != 0) {
			err = (float) Math.sqrt(err);
			
			return err / pixelsCounted;
		} else {
			return -1.0f;
		}
	}

	private PPC makeAndTransformCamera(PPC original, float x_trans,
			float y_trans, float z_trans, float x_rot, float y_rot, float z_rot) {
		Vec3 trans = new Vec3(x_trans, y_trans, z_trans);
		Vec3 rot = new Vec3(x_rot, y_rot, z_rot);
		rot.normalize();

		PPC newCam = original.copy(false);
		newCam.rotate(rot, ScreenRenderer.ROTATION_AMT);
		newCam.translate(trans);

		return newCam;
	}
	
	private void outputScreenshot(BufferedImage bi, String nameBase) {
		File file = new File(nameBase + ".png");
		System.out.println(file.getAbsolutePath());
		try {
			ImageIO.write(bi, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
