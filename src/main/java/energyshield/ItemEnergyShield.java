package energyshield;

import javax.annotation.Nullable;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.item.IPseudoDamageItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnergyShield extends ItemShield implements IPseudoDamageItem, IElectricItem {
	public static final String ITEMNAME = EnergyShield.prependModID("energyshield");
	public static final Item INSTANCE = new ItemEnergyShield();
	
	private EntityLivingBase player;
	private static final int MaxAcceptedDamage = 100;
	private static final int MaxCharge = 1000 * 1000;
	private static final int ChargePerDamage = MaxCharge / MaxAcceptedDamage;
	
	public ItemEnergyShield()
	{
		super();
		
		// Max damage is set to max accepted + 1 to prevent the item from breaking.
        this.setMaxDamage(MaxAcceptedDamage + 1);
        
        this.addPropertyOverride(new ResourceLocation("inactive"), new IItemPropertyGetter()
        {
        	// Show the inactive form when energy is drained.
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return ElectricItem.manager.getCharge(stack) > 0 ? 0.0F : 1.0F;
            }
        });
        this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter()
        {
        	// Show the blocking form when the user is right-clicking.
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return entityIn != null 
            		&& entityIn.isHandActive() 
            		&& entityIn.getActiveItemStack() == stack 
            		&& ElectricItem.manager.getCharge(stack) > 0
                		? 1.0F 
        				: 0.0F;
            }
        });
	}
	
	// Shield can't be used as a battery.
	@Override
	public boolean canProvideEnergy(ItemStack arg0) {
		return false;
	}

	// The charging limit.
	@Override
	public double getMaxCharge(ItemStack arg0) {
		return (double)MaxCharge;
	}

	// The required charging item tier.
	@Override
	public int getTier(ItemStack arg0) {
		return 3;
	}

	// The maximum charge speed.
	@Override
	public double getTransferLimit(ItemStack arg0) {
		return 1600.0D;
	}
	
	// Where to assign the item when auto-equipped.
	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EntityEquipmentSlot.OFFHAND;
	}
	
	// Only acknowledge itself as a shield.
	@Override
	public boolean isShield(ItemStack stack, EntityLivingBase entity) {
		player = entity;
		return stack.getItem() == ItemEnergyShield.INSTANCE;
	}
	
	// Show the durability bar whenever charge drops below max.
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return isDamaged(stack);
	}
	
	// Show charge percentage as the durability.
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - (double)(ElectricItem.manager.getCharge(stack) / this.getMaxCharge(stack));
	}
	
	// The shield is "damaged" when some of its charge is drained.
	@Override
	public boolean isDamaged(ItemStack stack) {
		return ElectricItem.manager.getCharge(stack) < this.getMaxCharge(stack);
	}

	// No action.
	@Override
	public void setStackDamage(ItemStack var1, int var2) {
	}
	
	// Intercept the damage amount, convert it to discharge, and prevent the item from breaking.
	@Override
	public void setDamage(ItemStack stack, int damage) {
		// Calculate the diff between old and new, and discharge that amount of energy.
		int prev = this.getDamage(stack);
		int diff = damage - prev;
		ElectricItem.manager.use(stack, diff * 5000, player);
		
		// Adjust the damage value to match the charge, and prevent it from matching/exceeding the limit.
		damage = MaxAcceptedDamage - (int)(ElectricItem.manager.getCharge(stack) / ChargePerDamage);
		if (damage > MaxAcceptedDamage)
		{
			damage = MaxAcceptedDamage;
		}
		
		super.setDamage(stack, damage);
	}
	
	// If the shield is fully discharged, it can't be used to block anymore.
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		if (ElectricItem.manager.getCharge(stack) > 0)
		{
			return EnumAction.BLOCK;
		}
		else
		{
			return EnumAction.NONE;
		}
	}
	
	// The shield can't be repaired on an anvil, because its damage is just energy discharge.
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	// Always show the enchantment shimmer, for effect.
    @SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return ElectricItem.manager.getCharge(stack) > 0;
	}
}
