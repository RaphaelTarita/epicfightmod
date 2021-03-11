package maninhouse.epicfight.animation;

import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;

public class JointTransform
{
	public static final JointTransform defaultTransform = new JointTransform(new Vec3f(0.0F,0.0F,0.0F), new Quaternion(0.0F,0.0F,0.0F,1.0F), new Vec3f(1.0F,1.0F,1.0F));
	
	private Vec3f position;
	private Vec3f scale;
	private Quaternion rotation;
	
	public JointTransform(Vec3f position, Quaternion rotation, Vec3f scale)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public Vec3f getPosition()
	{
		return position;
	}
	
	public Quaternion getRotation()
	{
		return rotation;
	}
	
	public Vec3f getScale()
	{
		return scale;
	}
	
	public void setRotation(Quaternion quat)
	{
		this.rotation = quat;
	}
	
	public VisibleMatrix4f toTransformMatrix()
	{
		VisibleMatrix4f matrix = new VisibleMatrix4f();
		matrix.translate(position);
		VisibleMatrix4f.mul(matrix, rotation.toRotationMatrix(), matrix);
		matrix.scale(this.scale);
		return matrix;
	}
	
	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression)
	{
		if(prev == null || next == null)
		{
			return JointTransform.defaultTransform;
		}
		
		Vec3f pos = interpolate(prev.position, next.position, progression);
		Quaternion rot = Quaternion.interpolate(prev.rotation, next.rotation, progression);
		Vec3f scale = interpolate(prev.scale, next.scale, progression);
		return new JointTransform(pos, rot, scale);
	}
	
	private static Vec3f interpolate(Vec3f start, Vec3f end, float progression)
	{
		float x = start.x + (end.x - start.x) * progression;
		float y = start.y + (end.y - start.y) * progression;
		float z = start.z + (end.z - start.z) * progression;
		return new Vec3f(x, y, z);
	}
}