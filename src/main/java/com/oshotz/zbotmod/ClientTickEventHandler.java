
package com.oshotz.zbotmod;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTickEventHandler {

  private static int tick = 0;
  private static final int tickRate = (int)Math.round(0.5 * 20);

  private static String prevTabHeader = "";
  private static String prevTabFooter = "";
  private static String prevSidebarHeader = "";
  private static Housing prevHousing = new Housing();

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onClientTick(TickEvent.ClientTickEvent event) {

    // Keep track of the current tick:
    tick++;

    // Run every 0.5 seconds:
    if (tick % tickRate == 0) {

      String tabHeader = ZBot.getTabHeader();
      String tabFooter = ZBot.getTabFooter();
      String sidebarHeader = ZBot.getSidebarHeader();
      Housing housing = ZBot.getHousing();
      
      // Detect tab header changes:
      if (!tabHeader.equals(prevTabHeader))
        ZBot.commandHandler.onTabHeaderChange(tabHeader);

      // Detect tab footer changes:
      if (!tabFooter.equals(prevTabFooter))
        ZBot.commandHandler.onTabFooterChange(tabFooter);

      // Detect sidebar header changes:
      if (!sidebarHeader.equals(prevSidebarHeader))
        ZBot.commandHandler.onSidebarHeaderChange(sidebarHeader);

      // Detect housing updates (guests or cookies change, but housing stays the same):
      if (housing.equals(prevHousing) && !housing.equalsStrict(prevHousing))
        ZBot.commandHandler.onHousingUpdate(housing);

      // Detect housing changes (entire housing changes):
      if (!housing.equals(prevHousing))
        ZBot.commandHandler.onHousingChange(housing);
      
      prevTabHeader = tabHeader;
      prevTabFooter = tabFooter;
      prevSidebarHeader = sidebarHeader;
      prevHousing = housing;

    }

  }
  
}

// MongoCollection<Ping> collection = ZBot.getCollection("ping", Ping.class);
// collection.find().forEach(doc -> System.out.println(doc.toString()));