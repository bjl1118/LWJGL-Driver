package models;

//a 3D model stored in memory 
public class RawModel {
	
	//ID in the VAO
	private int vaoID;
	
	//number of  vertices
	private int vertexCount;
	
	//constructor
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	//vapID getter
	public int getVaoID() {
		return vaoID;
	}
	
	//vertex count getter
	public int getVertexCount() {
		return vertexCount;
	}
	
}
