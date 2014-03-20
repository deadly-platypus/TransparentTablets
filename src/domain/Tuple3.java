package domain;

import exceptions.NonUnitaryException;

public class Tuple3 {
	protected float x, y, z;
	public static final float epsilon = 0.0000005f;
	
	public Tuple3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Tuple3() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass() != obj.getClass() || !(obj instanceof Tuple3)) {
			return false;
		}
		
		Tuple3 t3 = (Tuple3) obj;
		return (this.x == t3.x && this.y == t3.y && this.z == t3.z);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public Tuple3 add(Tuple3 in) {
		Tuple3 result = new Tuple3();
		result.x = this.x + in.x;
		result.y = this.y + in.y;
		result.z = this.z + in.z;
		
		return result;
	}
	
	public Tuple3 subtract(Tuple3 in) {
		Tuple3 result = new Tuple3();
		result.x = this.x - in.x;
		result.y = this.y - in.y;
		result.z = this.z - in.z;
		
		return result;
	}
	
	public Tuple3 multiply(float scale) {
		return new Tuple3(this.x * scale, this.y * scale, this.z * scale);
	}
	
	public Tuple3 divide(float scale) {
		return new Tuple3(this.x / scale, this.y / scale, this.z / scale);
	}
	
	public float distance() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public float distance2() {
		return (x * x + y * y + z * z);
	}
	
	public Tuple3 rotate(Vec3 axis, float radians) {
		if(axis.distance2() != 1.0f) {
			throw new NonUnitaryException(axis);
		}
		
		float indices[] = new float[9];
		
		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);
		
		indices[0] = cos + axis.x * axis.x * (1 - cos);
		indices[1] = axis.x * axis.y * (1 - cos) - axis.z * sin;
		indices[2] = axis.x * axis.z * (1 - cos) + axis.y * sin;
		indices[3] = axis.y * axis.x * (1 - cos) + axis.z * sin;
		indices[4] = cos + axis.y * axis.y * (1 - cos);
		indices[5] = axis.y * axis.z * (1 - cos) - axis.x * sin;
		indices[6] = axis.z * axis.x * (1 - cos) - axis.y * sin;
		indices[7] = axis.z * axis.y * (1 - cos) + axis.x * sin;
		indices[8] = cos + axis.z * axis.z * (1 - cos);
		
		Matrix9 mat = new Matrix9(indices);
		return mat.multiply(this);
	}
}
