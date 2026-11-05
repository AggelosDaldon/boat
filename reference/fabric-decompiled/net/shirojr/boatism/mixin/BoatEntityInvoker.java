package net.shirojr.boatism.mixin;

import net.minecraft.class_1690;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_1690.class})
public interface BoatEntityInvoker {
   @Invoker("getMaxPassengers")
   int invokeGetMaxPassenger();
}
