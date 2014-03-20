package domain;

import java.security.InvalidParameterException;

public class Matrix9 {
	protected float[] indices;
	
	public Matrix9(float indices[]) {
		if(indices.length != 9) {
			throw new RuntimeException(String.format("Expected 9 elements but input had %d element(s)", indices.length));
		}
		
		this.indices = new float[9];
		for(int i = 0; i < this.indices.length; i++){
			this.indices[i] = indices[i];
		}
	}
	
	public Matrix9() {
		this.indices = new float[9];
	}
	
	public Tuple3 multiply(Tuple3 t3) {
		float x, y, z;
		
		x = t3.x * this.indices[0] + t3.y * this.indices[1] + t3.z * this.indices[2];
		y = t3.x * this.indices[3] + t3.y * this.indices[4] + t3.z * this.indices[5];
		z = t3.x * this.indices[6] + t3.y * this.indices[7] + t3.z * this.indices[8];
		
		return new Tuple3(x, y, z);
	}
	
	public Vec3 getColumn(int columnIndex) {
		if(columnIndex < 0 || columnIndex > 2) {
			throw new InvalidParameterException(String.format("columnIndex is %d but should be [0, 2]", columnIndex));
		}
		
		Vec3 result = new Vec3();
		result.setX(this.indices[columnIndex]);
		result.setY(this.indices[3 + columnIndex]);
		result.setZ(this.indices[6 + columnIndex]);
		
		return result;
	}
	
	public void setColumn(int columnIndex, Tuple3 column) {
		if(columnIndex < 0 || columnIndex > 2) {
			throw new InvalidParameterException(String.format("columnIndex is %d but should be [0, 2]", columnIndex));
		}
		
		this.indices[columnIndex] = column.x;
		this.indices[3 + columnIndex] = column.y;
		this.indices[6 + columnIndex] = column.z;
	}
	
	public Matrix9 inverted() {
		Matrix9 result = new Matrix9(new float[9]);
		
		Vec3 a = this.getColumn(0);
		Vec3 b = this.getColumn(1);
		Vec3 c = this.getColumn(2);
		
		Vec3 _a = b.cross(c);
		_a = _a.divide(a.dot(_a));
		
		Vec3 _b = c.cross(a);
		_b = _b.divide(b.dot(_b));
		
		Vec3 _c = a.cross(b);
		_c = _c.divide(c.dot(_c));
		
		result.indices[0] = _a.x;
		result.indices[1] = _a.y;
		result.indices[2] = _a.z;
		result.indices[3] = _b.x;
		result.indices[4] = _b.y;
		result.indices[5] = _b.z;
		result.indices[6] = _c.x;
		result.indices[7] = _c.y;
		result.indices[8] = _c.z;
		
		return result;
	}
	
	public static Matrix9 identity() {
		float[] array = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
		return new Matrix9(array);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Matrix9)) {
			return false;
		}
		
		Matrix9 mat = (Matrix9) obj;
		for(int i = 0; i < this.indices.length; i++){
			if(this.indices[i] != mat.indices[i]){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		for(int i = 0; i < this.indices.length; i++) {
			if(i % 3 == 0 && i > 0){
				result.append("\n");
			}
			result.append(String.format("%s ", this.indices[i]));
		}
		
		return result.toString();
	}
}
