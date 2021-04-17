package maninhouse.epicfight.animation;

import java.util.ArrayList;
import java.util.List;

import maninhouse.epicfight.utils.math.VisibleMatrix4f;

public class Joint {
	private final List<Joint> subJoints = new ArrayList<Joint> ();
	private final int jointId;
	private final String jointName;
	private final VisibleMatrix4f localTransform;
	private VisibleMatrix4f inversedTransform = new VisibleMatrix4f();
	private VisibleMatrix4f animatedTransform = new VisibleMatrix4f();
	
	public Joint(String name, int jointID, VisibleMatrix4f localTransform) {
		this.jointId = jointID;
		this.jointName = name;
		this.localTransform = localTransform;
	}

	public void addSubJoint(Joint... joints) {
		for (Joint joint : joints) {
			this.subJoints.add(joint);
		}
	}
	
	public void setAnimatedTransform(VisibleMatrix4f animatedTransform) {
		this.animatedTransform = animatedTransform;
	}

	public void initializeAnimationTransform() {
		this.animatedTransform.setIdentity();

		for (Joint joint : this.subJoints) {
			joint.initializeAnimationTransform();
		}
	}
	
	public void setInversedModelTransform(VisibleMatrix4f superTransform) {
		VisibleMatrix4f modelTransform = VisibleMatrix4f.mul(superTransform, this.localTransform, null);
		VisibleMatrix4f.invert(modelTransform, this.inversedTransform);
		
		for (Joint joint : this.subJoints) {
			joint.setInversedModelTransform(modelTransform);
		}
	}
	
	public VisibleMatrix4f getLocalTrasnform() {
		return this.localTransform;
	}

	public VisibleMatrix4f getAnimatedTransform() {
		return this.animatedTransform;
	}

	public VisibleMatrix4f getInversedModelTransform() {
		return this.inversedTransform;
	}
	
	public List<Joint> getSubJoints() {
		return this.subJoints;
	}

	public String getName() {
		return this.jointName;
	}

	public int getId() {
		return this.jointId;
	}
	/**
	public void showInfo()
	{
		System.out.println("id = " + this.jointId);
		System.out.println("name = " + this.jointName);
		System.out.print("children = ");
		for(Joint joint : subJoints)
		{
			System.out.print(joint.jointName + " ");
		}
		System.out.println();
		for(Joint joint : subJoints)
		{
			joint.showInfo();
		}
	}**/
}
