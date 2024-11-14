package eu.asangarin.arikeys.neoforge;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysAddKeyPayload;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysLoadPayload;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.network.KeyAddData;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class AKClientHandler {
	public static void handleKeyData(AriKeysAddKeyPayload payload, PlayPayloadContext context) {
		KeyAddData key = payload.keyAddData();
		context.workHandler().submitAsync(() -> AriKeys.add(key));
	}

	public static void handleLoad(AriKeysLoadPayload payload, PlayPayloadContext context) {
		context.workHandler().submitAsync(AriKeysIO::load);
	}
}
