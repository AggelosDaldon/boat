package net.shirojr.boatism.util;

import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.boatism.Boatism;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
   public static final Logger LOGGER;

   private LoggerUtil() {
   }

   public static void devLogger(String input) {
      devLogger(input, false, (Exception)null);
   }

   public static void devLogger(String input, boolean isError, Exception exception) {
      if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
         String printText = "DEV - [ " + input + " ]";
         if (!isError) {
            LOGGER.info(printText);
         } else if (exception == null) {
            LOGGER.error(printText);
         } else {
            LOGGER.error(printText, exception);
         }
      }
   }

   static {
      LOGGER = LoggerFactory.getLogger(Boatism.MODID);
   }
}
