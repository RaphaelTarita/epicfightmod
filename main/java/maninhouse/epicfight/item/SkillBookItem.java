package maninhouse.epicfight.item;

import java.util.List;

import javax.annotation.Nullable;

import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkillBookItem extends Item {
	public SkillBookItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().contains("skill");
	}
	
	public static void setContainingSkilll(Skill skill, ItemStack stack) {
		stack.getOrCreateTag().put("skill", StringNBT.valueOf(skill.getRegistryName().getPath()));
	}
	
	public static Skill getContainSkill(ItemStack stack) {
		String skillName = stack.getTag().getString("skill");
		return Skills.MODIFIABLE_SKILLS.get(new ResourceLocation(EpicFightMod.MODID, skillName));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.getTag() != null && stack.getTag().contains("skill")) {
			tooltip.add(new TranslationTextComponent(String.format("skill.%s.%s", EpicFightMod.MODID, stack.getTag().get("skill").getString()))
					.mergeStyle(TextFormatting.GREEN));
		}
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == EpicFightItemGroup.ITEMS) {
			Skills.MODIFIABLE_SKILLS.values().stream().forEach((skill)->{
				ItemStack stack = new ItemStack(this);
				setContainingSkilll(skill, stack);
				items.add(stack);
			});
		}
	}
}