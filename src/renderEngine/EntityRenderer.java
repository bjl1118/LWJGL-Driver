package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

//A class that can render the model from the VAO
public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	//call this once per frame, prepares openGL to render the game 
	public void prepare() {
		
		//test which triangles are in front of each other
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		//clear the color
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model: entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			
			for (Entity entity:batch) {
				prepareInstance(entity);
				
				//render everything, with GL_TRIANGLES primative
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		
		//bind the VAO and activate the attribute arrays we want
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		//Load the shine to the shaders
		ModelTexture texture = model.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		//disable culling if it has transparency 
		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		
		//load Fake lighting if applicable
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		
		//activate one of open GL's texture banks 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		//bind the texture model to OpenGL's texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTexturedModel() {
		//re-enable culling for the next model
		MasterRenderer.enableCulling();
		
		//disable the attribute arrays now that they've been rendered
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		//unbind the VAO array
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		//Load the entities transformation to the vertex shader
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
