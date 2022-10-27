package eu.asangarin.arikeys.forge;

import eu.asangarin.arikeys.AriKeys;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.network.NetworkConstants;

@Mod(AriKeys.MOD_ID)
public class AriKeysForge {
	public AriKeysForge() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
				() -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		if (Environment.get().getDist().isClient()) AriKeys.init();
	}
}