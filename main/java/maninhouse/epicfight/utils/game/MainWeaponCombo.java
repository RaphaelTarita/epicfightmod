package maninhouse.epicfight.utils.game;

import java.util.List;

import com.google.common.collect.Lists;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class MainWeaponCombo
{
	private Class<? extends Item> mainHand;
	private Class<? extends Item> offHand;
	private List<StaticAnimation> combo;
	
	public MainWeaponCombo(Class<? extends Item> mainHand, Class<? extends Item> offHand, StaticAnimation... comboSeries)
	{
		this.mainHand = mainHand;
		this.offHand = offHand;
		this.combo = Lists.<StaticAnimation>newArrayList();
		
		for(StaticAnimation combo : comboSeries)
		{
			this.combo.add(combo);
		}
	}
	
	public List<StaticAnimation> getCombo(PlayerData<?> player)
	{
		if(this.isMatching(player))
		{
			return combo;
		}
		
		return null;
	}
	
	public boolean isMatching(PlayerData<?> player)
	{
		PlayerEntity orgPlayer = player.getOriginalEntity();
		Item offHand = orgPlayer.getHeldItemOffhand().getItem();
		Item mainHand = orgPlayer.getHeldItemMainhand().getItem();
		
		return MainWeaponCombo.compatible(mainHand.getClass(), this.mainHand) && MainWeaponCombo.compatible(offHand.getClass(), this.offHand);
	}
	
	private static boolean compatible(Class<? extends Item> a, Class<? extends Item> b)
	{
		if(a==null || b==null) return true;
		else return a==b;
	}
}