package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

//Load 3D models into memory, by storing visual data in a VAO
public class Loader {
	
	//Lists of VAOs, VBOs, and textures for memory management
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	/**Take in positions of a model's vertices
	  *Load this data into a VAO, 
	  *Return information about VAO as a raw model object **/
	public RawModel loadToVao(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		
		//create the VAO, store it as an ID
		int vaoID = createVao();
		
		//store it in the VAO list, so we can delete it later;
		vaos.add(vaoID);
		
		bindIndicesBuffer(indices);
		
		//store the vertex positions at attribute list 0 in the VAO
		storeDataInAttributeList(0, 3, positions);
		
		//store the texture positions at attribute list 1 in the VAO
		storeDataInAttributeList(1, 2, textureCoords);
		
		//store the normals at attribute list 2 in the VAO
		storeDataInAttributeList(2, 3, normals);
		
		//unbind the VAO
		unbindVao();
		
		//return the VAO information as a raw models
		return new RawModel(vaoID, indices.length);
	}
	
	//load a texture into memory and return the texture ID
	public int loadTexture(String file) {
		Texture texture = null;
		
		try {
			//read from a texture file, texture files are always stored in the res file
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + file + ".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//create the texture ID from the texture and return it
		int textureID = texture.getTextureID();
		textures.add(textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		return textureID;
	}
	
	//delete the VAOs and the VBOs when we close the game
	public void cleanUp() {
		
		//delete all the VAOs 
		for (int vao:vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		
		//delete all the VBOs
		for (int vbo:vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		
		//delete all the texture IDs
		for (int texture:textures) {
			GL11.glDeleteTextures(texture);
		}
	}
	
	//create a new, empty, VAO, and return its ID 
	private int createVao() {
		
		//create an empty VAO and return its ID 
		int vaoID = GL30.glGenVertexArrays();
		
		//activate the vaoID by "binding it", then return the ID
		GL30.glBindVertexArray(vaoID);
		return vaoID;
		
	}
	
	//Store data into one of the VAO's attribute lists 
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		
		//create an empty VBO
		int vboID = GL15.glGenBuffers();
		
		//store the VBO in the vbos array, so we can delete it later
		vbos.add(vboID);
		
		//"Bind" the VBO so that we can do stuff to it
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		//convert the data into a buffer
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		
		//Store the buffer into the VBO 
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
		//store the VBO into the VAO
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT,false, 0, 0);
		
		//now that we're finished with the VBO, we can unbind it
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void unbindVao() {
		
		//instead of putting in a VAO ID, pass in 0 to "unbind" it 
		GL30.glBindVertexArray(0);
	}
	
	//load an indices buffer array and bind it to a VAO
	private void bindIndicesBuffer(int[] indices) {
		
		//generate an empty buffer
		int vboID = GL15.glGenBuffers();
		
		//add it to the array of vbos so it gets deleted in the end
		vbos.add(vboID);
		
		//bind the vbo
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		
		//convert the data into an intbuffer
		IntBuffer buffer = storeDataInIntBuffer(indices);
		
		//store the buffer in the VBO
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	//convert an array of indidices into an IntBuffer
	private IntBuffer storeDataInIntBuffer(int[] data) {
		
		//create an empty intbuffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		
		//same as with the float buffers, we have to put the data in and then flip it
		buffer.put(data);
		buffer.flip();
		return buffer;
		
	}
	
	//convert an array of floats to a buffer
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		
		//create a new buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		
		//put the data in the buffer
		buffer.put(data);
		
		//we have to flip it, because buffer expects to be written to not read
		buffer.flip();
		return buffer;
	}
}
