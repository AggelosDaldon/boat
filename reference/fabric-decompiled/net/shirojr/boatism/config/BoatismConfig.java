package net.shirojr.boatism.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.Config.Gui.Background;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.RequiresRestart;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(
   name = "boatism"
)
@Background("minecraft:textures/block/stone.png")
public class BoatismConfig implements ConfigData {
   @Category("general")
   @Comment("Use Scrolling in a boat to change Power Level")
   public boolean powerLevelScrolling = true;
   @Category("engine_data")
   @Comment("General Health value")
   @RequiresRestart
   public float health = 6.0F;
   @Category("engine_data")
   @Comment("Indication of low health")
   public float lowHealth = 3.0F;
   @Category("engine_data")
   @Comment("Base fuel capacity")
   public int maxBaseFuel = 1000;
   @Category("engine_data")
   @Comment("Overheat limit")
   public int maxBaseOverheat = 1000;
}
