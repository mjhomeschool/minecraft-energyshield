package energyshield;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = EnergyShield.MODID, version = EnergyShield.VERSION)
public class EnergyShield {
	// you also need to update the modid and version in two other places as well:
	// build.gradle file (the version, group, and archivesBaseName parameters)
	// resources/mcmod.info (the name, description, and version parameters)
	public static final String MODID = "energyshield";
	public static final String VERSION = "0.1";
    
	// The instance of your mod that Forge uses. Optional.
	@Mod.Instance(EnergyShield.MODID)
	public static EnergyShield instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ItemEnergyShield.INSTANCE.setRegistryName(ItemEnergyShield.ITEMNAME);
		ForgeRegistries.ITEMS.register(ItemEnergyShield.INSTANCE);
		ModelLoader.setCustomModelResourceLocation(
				ItemEnergyShield.INSTANCE, 
				0, 
				new ModelResourceLocation(prependModID("energyshield")));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
	}

	public static String prependModID(String name) {
		return MODID + ":" + name;
	}
}
