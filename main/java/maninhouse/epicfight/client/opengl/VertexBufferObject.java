package maninhouse.epicfight.client.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBufferObject
{	
	private int vboID;
	
	public VertexBufferObject()
	{
		vboID = GL15.glGenBuffers();
	}
	
	protected void storeFloatInVbo(int attribNumber, int size, float[] data)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data).flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribNumber, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	protected void storeFloatInVbo(int attribNumber, int size, FloatBuffer data)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribNumber, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	protected void storeIntInVbo(int attribNumber, int size, int[] data)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data).flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attribNumber, size, GL11.GL_INT, size * 4, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	protected void storeIndices(int[] data)
	{
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data).flip();
		
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	protected void clearVbo()
	{
		GL15.glDeleteBuffers(vboID);
	}
	
	protected int getVboID()
	{
		return vboID;
	}
}
