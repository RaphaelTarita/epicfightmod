package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.collada.AnimationDataExtractor;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.model.Armature;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation mirrorAnimation;
	
	public MirrorAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2)
	{
		super(id, convertTime, repeatPlay, path1);
		mirrorAnimation = new StaticAnimation(convertTime, repeatPlay, path2);
	}
	
	public StaticAnimation checkHandAndReturnAnimation(Hand hand)
	{
		switch(hand)
		{
		case MAIN_HAND:
			return this;
		case OFF_HAND:
			return mirrorAnimation;
		}
		
		return null;
	}
	
	@Override
	public StaticAnimation bindFull(Armature armature)
	{
		if(mirrorAnimation.animationDataPath != null)
		{
			AnimationDataExtractor.extractAnimation(new ResourceLocation(EpicFightMod.MODID, mirrorAnimation.animationDataPath), mirrorAnimation, armature);
			mirrorAnimation.animationDataPath = null;
		}
		
		return super.bindFull(armature);
	}
}