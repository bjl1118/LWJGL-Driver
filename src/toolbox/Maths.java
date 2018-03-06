package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths {
	
	//convert translation, rotation, and scale into a 4x4 matrix
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, 
			float rz, float scale) {
		
		//create a new matrix
		Matrix4f matrix = new Matrix4f();
		
		//set this matrix as the identity matrix
		matrix.setIdentity();
		
		//create the transformation matrix and return it
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rx, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(ry, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rz, new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}
	
	//creates the view matrix, which moves all the models to the right
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), 
				new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), 
				new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
}
