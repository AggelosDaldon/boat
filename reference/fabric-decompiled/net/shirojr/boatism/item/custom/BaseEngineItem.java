package net.shirojr.boatism.item.custom;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import org.jetbrains.annotations.Nullable;

public class BaseEngineItem extends class_1792 {
   public BaseEngineItem(class_1792.class_1793 settings) {
      super(settings);
   }

   public void method_7851(class_1799 stack, @Nullable class_1937 world, List<class_2561> tooltip, class_1836 context) {
      tooltip.add(class_2561.method_43471("tooltip.boatism.base_engine_1"));
      tooltip.add(class_2561.method_43471("tooltip.boatism.base_engine_2"));
      tooltip.add(class_2561.method_43471("tooltip.boatism.base_engine_3"));
      tooltip.add(class_2561.method_43471("tooltip.boatism.base_engine_4"));
      tooltip.add(class_2561.method_43471("tooltip.boatism.base_engine_5"));
      super.method_7851(stack, world, tooltip, context);
   }
}
