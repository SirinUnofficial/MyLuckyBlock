package io.github.sycamore0.myluckyblock.mixin;

import io.github.sycamore0.myluckyblock.lucky.addon.AddonPackProvider;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public abstract class MixinPackRepository {
    @Mutable
    @Shadow @Final private Set<RepositorySource> sources;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void myluckyblock$injectAddonProvider(RepositorySource[] providers, CallbackInfo ci) {
        for (RepositorySource source : providers) {
            if (source instanceof BuiltInPackSourceAccessor accessor) {
                this.sources = new LinkedHashSet<>(this.sources);
                this.sources.add(new AddonPackProvider(accessor.getPackType()));
                return;
            }
        }
    }
}