package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

//Class that accesses the shaders through our java code
//its abstract because this is a generic shader program that contains 
//all the functionality that would be in any shader program
public abstract class ShaderProgram {
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	//to load a matrix into a uniform variable, we need to store it as a floatbuffer
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	//Constructor, converts shader files into 
	public ShaderProgram(String vertexFile, String fragmentFile) {
		
		//load the shaders from the file, convert them to an ID
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		
		//create a program ID
		programID = GL20.glCreateProgram();
		
		//attach the shader to the program
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		
		//bind the attributes from the VAO to the shader
		bindAttributes();
		
		//link the program 
        GL20.glLinkProgram(programID);
        
		//validate the program 
		GL20.glValidateProgram(programID);
		
		//get all the uniform locations 
		getAllUniformLocations();

	}
	
	protected abstract void getAllUniformLocations();
	
	//get the location of the uniform variable 
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	//load a float into a uniform variable location
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	//load a vector into a uniform variable location
	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	//load a boolean into a uniform variable location
	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		
		//store the matrix in the float buffer
		matrix.store(matrixBuffer);
		
		//flip the buffer so that we can read from it 
		matrixBuffer.flip();
		
		//load the buffer to the matrix, without transposing it 
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	
	//start the program
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	//stop the program
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	//clean up, memory management
	public void cleanUp() {
		//stop the program
		stop();
		
		//detach the shaders
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		
		//delete the shaders
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		
		//delete the program
		GL20.glDeleteProgram(programID);
	}
	
	//link up the inputs to the shader programs to one of the attributes of the VAO that we pass in 
	protected abstract void bindAttributes();
	
	//bind an attribute to the shader 
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	//loads the shader files 
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append('\n');
			}
			reader.close();
		} catch (IOException e){
			System.err.println("Couldn't read file!");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}
		return shaderID;
	}
}
