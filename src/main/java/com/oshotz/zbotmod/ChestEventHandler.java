
package com.oshotz.zbotmod;

import java.util.ArrayList;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestEventHandler {

  @SubscribeEvent
  public void onGuiOpen(final GuiScreenEvent.InitGuiEvent.Post event) {

    if (event.gui != null && event.gui instanceof GuiChest) {

      new Thread(() -> {

        try {

          Thread.sleep(250);
          ContainerChest chestContainer = ((ContainerChest) ((GuiChest) event.gui).inventorySlots);
          IInventory chestInventory = chestContainer.getLowerChestInventory();

          String GUIName = chestInventory.getName();
          ArrayList<GUIStack> GUIInventory = ZBot.getGUIInventory(chestInventory);
          int GUIID = chestContainer.windowId;
          
          ZBot.commandHandler.onGUI(GUIName, GUIInventory, GUIID);

        } catch (InterruptedException e) {

          e.printStackTrace();

        }
  
      }).start();

    }

  }

}
