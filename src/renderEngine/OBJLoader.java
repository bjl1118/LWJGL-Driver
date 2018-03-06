package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

//Parses OBJ files into Raw Models 
public class OBJLoader {
	
	//parses the OBJ file, stores it in the VAO, and returns it as a raw model
	public static RawModel loadObjModel (String filename, Loader loader) {
		FileReader fr = null;
		try {
			fr = new FileReader(new File("./res/" + filename + ".obj"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file for OBJ loader");
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] texturesArray = null;
		float[] normalsArray = null;
		int[] indicesArray = null;
		
		try {
			while(true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), 
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), 
							Float.parseFloat(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), 
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					texturesArray = new float[vertices.size() * 2];
					normalsArray = new float[vertices.size() * 3];
					break;
				}
			}
			
			while (line != null) {
				if (!line.startsWith("f")) {
					line = reader.readLine();
					continue;
				}
				
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
				
				line = reader.readLine();
			}
			
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		for (Vector3f vertex:vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		for (int i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVao(verticesArray, texturesArray, normalsArray, indicesArray);
	}
	
	//We need to take the vertices provided by the face and get the corresponding normals 
	//and textures, and then put them into a format that can be read by the VAO
	private static void processVertex(String[] vertexData, List<Integer> indices, 
			List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, 
			float[] normalArray) {
		
		//index of the vertex position in the vertex positions list, and add it to the indices list
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
		
		//Get the correct texture based on the index provided by the vertex array, and add it to the texture array
		Vector2f currentTexture = textures.get(Integer.parseInt(vertexData[1]) - 1);
		textureArray[currentVertexPointer*2] = currentTexture.x;
		textureArray[currentVertexPointer*2 + 1] = 1 - currentTexture.y;
		
		//Get the correct normal based on the index provided by the vertex array, and add it to the normal array
		Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalArray[currentVertexPointer*3] = currentNormal.x;
		normalArray[currentVertexPointer*3 + 1] = currentNormal.y;
		normalArray[currentVertexPointer*3 + 2] = currentNormal.z;
		
	}
}
