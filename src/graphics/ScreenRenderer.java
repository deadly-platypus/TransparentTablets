package graphics;

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
	private int texId;

	public static float ROTATION_AMT = 1.0f;
	public static float WIGGLE = 0.1f;
	private int counter = 0;

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		gl = drawable.getGL().getGL2();

		gl.glViewport(0, 0, this.state.getWidth(), this.state.getHeight());

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		// this.glu.gluPerspective(this.state.getHorizontal_fov(),
		// this.state.getWidth() / this.state.getHeight(),
		// EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);
		gl.glFrustumf(this.state.getCurrentCamera().getView_corner().getX(),
				-this.state.getCurrentCamera().getView_corner().getX(),
				-this.state.getCurrentCamera().getView_corner().getY(),
				this.state.getCurrentCamera().getView_corner().getY(),
				EnvironmentState.MIN_Z, EnvironmentState.MAX_Z);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		IntBuffer tmp = IntBuffer.allocate(1);
		gl.glGenTextures(1, tmp);
		this.texId = tmp.get();

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, this.texId);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);

		ByteBuffer pixels = ByteBuffer.allocate(4 * this.state.getHeight()
				* this.state.getWidth());
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, this.state.getWidth(),
				this.state.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
				pixels);

		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, this.texId);

		tmp = IntBuffer.allocate(1);
		gl.glGenFramebuffers(1, tmp);
		this.fboId = tmp.get();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, this.fboId);
		gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0,
				GL.GL_TEXTURE_2D, this.texId, 0);

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

		if (this.state.getPreviousCamera() != null) {
			this.state.getCurrentCamera().translate(
					new Vec3(ScreenRenderer.WIGGLE, 0.0f, 0.0f));
		}
		ByteBuffer buffer = this.produceScreenPixels(this.state
				.getCurrentCamera().getOrigin());
		this.state.getCurrentCamera().setPixels(buffer);

		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glDrawPixels(this.state.getWidth(), this.state.getHeight(),
				GL.GL_RGB, GL.GL_UNSIGNED_BYTE, this.state.getCurrentCamera()
						.getPixels());

		if (this.state.getPreviousCamera() != null) {
			float test = this.error(Float.MAX_VALUE, ScreenRenderer.WIGGLE,
					0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
			System.out.println(test);
		}

		CameraDelta delta = new CameraDelta();//this.findCameraDelta();
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

		// Render the wall
		if (textureCoords != null) {
			gl.glTexCoord2f(textureTop, textureLeft);
		}
		gl.glVertex3f(this.state.getWall().getTopLeft().getX(), this.state
				.getWall().getTopLeft().getY(), this.state.getWall()
				.getTopLeft().getZ());

		if (textureCoords != null) {
			gl.glTexCoord2f(textureTop, textureRight);
		}
		gl.glVertex3f(this.state.getWall().getTopRight().getX(), this.state
				.getWall().getTopRight().getY(), this.state.getWall()
				.getTopLeft().getZ());

		if (textureCoords != null) {
			gl.glTexCoord2f(textureBottom, textureRight);
		}
		gl.glVertex3f(this.state.getWall().getBottomRight().getX(), this.state
				.getWall().getBottomRight().getY(), this.state.getWall()
				.getBottomRight().getZ());

		if (textureCoords != null) {
			gl.glTexCoord2f(textureBottom, textureLeft);
		}
		gl.glVertex3f(this.state.getWall().getBottomLeft().getX(), this.state
				.getWall().getBottomLeft().getY(), this.state.getWall()
				.getBottomLeft().getZ());

		gl.glEnd();

		gl.glPopMatrix();
	}

	private ByteBuffer produceScreenPixels(Point3 pt) {
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		this.glu.gluLookAt(pt.getX(), pt.getY(), pt.getZ(), pt.getX(),
				pt.getY(), 0.0f, 0.0f, 1.0f, 0.0f);

		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		int tmpTex;
		IntBuffer tmpTexBuf = IntBuffer.allocate(1);
		gl.glGenTextures(1, tmpTexBuf);
		tmpTex = tmpTexBuf.get(0);

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tmpTex);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);

		ByteBuffer pixels = ByteBuffer.allocate(3 * this.state.getHeight()
				* this.state.getWidth());
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, this.state.getWidth(),
				this.state.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
				pixels);

		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tmpTex);

		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, this.fboId);
		gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
				GL.GL_TEXTURE_2D, tmpTex, 0);

		int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
		if (status != GL.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error making FBObject: "
					+ FBObject.getStatusString(status));
			System.exit(status);
		}

		drawWall();

		// Copy the pixels
		gl.glReadBuffer(GL2.GL_COLOR_ATTACHMENT0);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
		ByteBuffer buffer = ByteBuffer.allocate(3
				* this.state.getHeight() * this.state.getWidth());
		gl.glReadPixels(0, 0, this.state.getWidth(), this.state.getHeight(),
				GL2.GL_BGR, GL.GL_BYTE, buffer);

		gl.glDeleteTextures(1, tmpTexBuf);

		BufferedImage bi = new BufferedImage(this.state.getWidth(),
				this.state.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int[] bd = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

		for (int y = 0; y < this.state.getHeight(); y++) {
			for (int x = 0; x < this.state.getWidth(); x++) {
				int b = 2 * buffer.get();
				int g = 2 * buffer.get();
				int r = 2 * buffer.get();

				bd[(this.state.getHeight() - y - 1) * this.state.getWidth() + x] = (r << 16)
						| (g << 8) | b | 0xFF000000;
			}
		}
		File file = new File("image" + counter++ + ".jpg");
		System.out.println(file.getAbsolutePath());
		try {
			boolean successful = ImageIO.write(bi, "jpg", file);
			System.out.println(successful);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		buffer.rewind();
		return buffer;
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

		float delta = ScreenRenderer.WIGGLE / 2.0f;
		for (float x_trans = -ScreenRenderer.WIGGLE; x_trans < ScreenRenderer.WIGGLE; x_trans += delta) {
			for (float y_trans = -ScreenRenderer.WIGGLE; y_trans < ScreenRenderer.WIGGLE; y_trans += delta) {
				for (float z_trans = -ScreenRenderer.WIGGLE; z_trans < ScreenRenderer.WIGGLE; z_trans += delta) {
					for (float x_rot = -ScreenRenderer.WIGGLE; x_rot < ScreenRenderer.WIGGLE; x_rot += delta) {
						for (float y_rot = -ScreenRenderer.WIGGLE; y_rot < ScreenRenderer.WIGGLE; y_rot += delta) {
							for (float z_rot = -ScreenRenderer.WIGGLE; z_rot < ScreenRenderer.WIGGLE; z_rot += delta) {
								float error = error(min_error, x_trans,
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
				min_roty, min_rotz);
	}

	private float error(float min_error, float x_trans, float y_trans,
			float z_trans, float x_rot, float y_rot, float z_rot) {
		float err = 0.0f;
		boolean tested = false;

		PPC newCam = this.makeAndTransformCamera(
				this.state.getPreviousCamera(), x_trans, y_trans, z_trans,
				x_rot, y_rot, z_rot);

		// Point3 csOrigin =
		// newCam.toCameraCoords(newCam.getOrigin()).toPoint3();
		for (int i = 0; i < this.state.getHeight(); i++) {
			for (int j = 0; j < this.state.getWidth(); j++) {
				Point3 csPixel = new Point3(newCam.getOrigin().getX()
						+ newCam.getView_corner().getX()
						+ newCam.getWidth().getX() * j, newCam.getOrigin()
						.getY()
						+ newCam.getView_corner().getY()
						+ newCam.getHeight().getY() * i, newCam.getOrigin()
						.getZ() + newCam.getView_corner().getZ());
				Point3 wsPixel = newCam.toWorldCoords(csPixel).toPoint3();
				Ray3D wsRay = new Ray3D(newCam.getOrigin(), new Vec3(
						newCam.getOrigin(), wsPixel));
				Point3 intersect = this.state.getWall().intersect(wsRay);

				if (intersect != null) {
					Point3 intersectPrev = this.state.getPreviousCamera()
							.toCameraCoords(intersect).toPoint3();
					Point3 intersectCurr = newCam.toCameraCoords(intersect)
							.toPoint3();

					try {
						// Find the location in the previous camera's pixels
						// sample
						int prevs[] = this.findPixelCoordinates(
								this.state.getPreviousCamera(), intersectPrev);
						int currs[] = this.findPixelCoordinates(newCam,
								intersectCurr);

						// Read in channels separately
						byte prevByte = this.state
								.getPreviousCamera()
								.getPixels()
								.get(prevs[1] * this.state.getWidth()
										+ prevs[0]);
						byte currByte = this.state
								.getCurrentCamera()
								.getPixels()
								.get(currs[1] * this.state.getWidth()
										+ currs[0]);
						err += (prevByte - currByte) * (prevByte - currByte);

						prevByte = this.state
								.getPreviousCamera()
								.getPixels()
								.get(prevs[1] * this.state.getWidth()
										+ prevs[0] + 1);
						currByte = this.state
								.getCurrentCamera()
								.getPixels()
								.get(currs[1] * this.state.getWidth()
										+ currs[0] + 1);
						err += (prevByte - currByte) * (prevByte - currByte);

						prevByte = this.state
								.getPreviousCamera()
								.getPixels()
								.get(prevs[1] * this.state.getWidth()
										+ prevs[0] + 2);
						currByte = this.state
								.getCurrentCamera()
								.getPixels()
								.get(currs[1] * this.state.getWidth()
										+ currs[0] + 2);
						err += (prevByte - currByte) * (prevByte - currByte);

						if (err > min_error) {
							return (float) Math.sqrt(err);
						}
						tested = true;
					} catch (Exception e) {
						// The sample isn't available everywhere so move on....
					}
				}
			}
		}

		if (tested) {
			return (float) Math.sqrt(err);
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

	private int[] findPixelCoordinates(PPC camera, Point3 csIntersect) {
		int u = (int) ((csIntersect.getX() - camera.getView_corner().getX()) / (camera
				.getWidth().getX()));
		int v = (int) ((csIntersect.getY() - camera.getView_corner().getY()) / camera
				.getHeight().getY());

		return new int[] { u, v };
	}
}
