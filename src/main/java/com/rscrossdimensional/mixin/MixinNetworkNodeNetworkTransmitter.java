package com.rscrossdimensional.mixin;

import com.rscrossdimensional.TransmitterChunkManager;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkNodeNetworkTransmitter.class)
public class MixinNetworkNodeNetworkTransmitter {
    @Inject(method = "visit", at = @At("HEAD"), remap = false)
    private void rscrossdimensional$updateForcedChunk(INetworkNodeVisitor.Operator operator, CallbackInfo ci) {
        if (operator.getAction() == Action.PERFORM) {
            TransmitterChunkManager.update((NetworkNodeNetworkTransmitter) (Object) this);
        }
    }
}
