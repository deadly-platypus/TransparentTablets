package exceptions;

import domain.Vec3D;

public class NonUnitaryException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6597800056910129901L;

	private Vec3D vec;
	
	public NonUnitaryException(Vec3D vec){
		this.vec = vec;
	}
	
	@Override
	public String getMessage() {
		return String.format("Vector has length %f but should be of length 1", this.vec.distance());
	}
}
