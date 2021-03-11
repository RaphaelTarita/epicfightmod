package maninhouse.epicfight.client.opengl;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexArrayObject
{	
	private final int vaoID;
	private final List<VertexBufferObject> vbos = new ArrayList<VertexBufferObject> ();
	private VertexBufferObject elementVbo;
	
	public VertexArrayObject()
	{
		vaoID = GL30.glGenVertexArrays();
	}
	
	public void bufferDataFloat(int attrib, int size, float[] data)
	{
		GL30.glBindVertexArray(vaoID);
		
		VertexBufferObject vbo = new VertexBufferObject();
		vbo.storeFloatInVbo(attrib, size, data);
		vbos.add(vbo);
		
		GL30.glBindVertexArray(0);
	}
	
	public void bufferDataFloat(int attrib, int size, FloatBuffer data)
	{
		GL30.glBindVertexArray(vaoID);
		
		VertexBufferObject vbo = new VertexBufferObject();
		vbo.storeFloatInVbo(attrib, size, data);
		vbos.add(vbo);
		
		GL30.glBindVertexArray(0);
	}
	
	public void modifyBufferData(int attrib, int size, float[] data)
	{
		GL30.glBindVertexArray(vaoID);
		
		VertexBufferObject vbo = vbos.get(attrib);
		vbo.storeFloatInVbo(attrib, size, data);
		
		GL30.glBindVertexArray(0);
	}
	
	public void modifyBufferData(int attrib, int size, FloatBuffer data)
	{
		GL30.glBindVertexArray(vaoID);
		
		VertexBufferObject vbo = vbos.get(attrib);
		vbo.storeFloatInVbo(attrib, size, data);
		
		GL30.glBindVertexArray(0);
	}
	
	public void bufferDataInt(int attrib, int size, int[] data)
	{
		GL30.glBindVertexArray(vaoID);
		
		VertexBufferObject vbo = new VertexBufferObject();
		vbo.storeIntInVbo(attrib, size, data);
		vbos.add(vbo);
		
		GL30.glBindVertexArray(0);
	}
	
	public void bufferDataElement(int[] data)
	{
		GL30.glBindVertexArray(vaoID);
		if(elementVbo==null)
		{
			elementVbo = new VertexBufferObject();
		}
		elementVbo.storeIndices(data);
		
		GL30.glBindVertexArray(0);
	}
	
	public void bindVao()
	{
		GL30.glBindVertexArray(vaoID);
		
		for(int i = 0; i < vbos.size(); i++)
		{
			GL20.glEnableVertexAttribArray(i);
		}
		
		if(elementVbo != null)
		{
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementVbo.getVboID());
		}
	}
	
	public void unbindVao()
	{
		GL30.glBindVertexArray(0);
		
		for(int i = 0; i < vbos.size(); i++)
		{
			GL20.glDisableVertexAttribArray(i);
		}
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void clearVao()
	{
		GL30.glDeleteVertexArrays(vaoID);
		
		for(VertexBufferObject vbo : vbos)
		{
			vbo.clearVbo();
		}
		if(elementVbo != null)
		{
			elementVbo.clearVbo();
		}
	}
	
	public int getVertexNumber()
	{
		return 0;
	}
}