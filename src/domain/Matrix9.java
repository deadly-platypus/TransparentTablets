package domain;

public class Matrix9 {
	protected float indices[];
	
	public Matrix9(float indices[]) {
		if(indices.length != 9) {
			throw new RuntimeException(String.format("Expected 9 elements but input had %d element(s)", indices.length));
		}
		
		this.indices = indices;
	}
	
	public Tuple3 multiply(Tuple3 t3) {
		float x, y, z;
		
		x = t3.x * this.indices[0] + t3.y * this.indices[1] + t3.z * this.indices[2];
		y = t3.x * this.indices[3] + t3.y * this.indices[4] + t3.z * this.indices[5];
		z = t3.x * this.indices[6] + t3.y * this.indices[7] + t3.z * this.indices[8];
		
		return new Tuple3(x, y, z);
	}
}
