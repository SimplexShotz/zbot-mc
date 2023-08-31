
package com.oshotz.zbotmod;

import java.util.ArrayList;

public class Command_joinHousing extends Command {

  private boolean visited;

  // Inherit the constructors from Command:
  public Command_joinHousing() { super(); }
  public Command_joinHousing(String... args) { super(args); }

  public void run() {

    visited = false;

    // If the bot is already in a housing server, run the /visit command:
    if (ZBot.isInHousing()) {
      visit();
      return;
    }

    // Otherwise, join a housing lobby first so the /visit command is available:
    ZBot.sendCommand("lobby", "housing");

  }

  private void visit() {

    if (visited)
      return;
    
    // Visit the specified housing (if the bot hasn't already done so):
    ZBot.sendCommand("visit", args.get(0));
    visited = true;

  }

  public void onSidebarHeaderChange(String sidebarHeader) {

    // Ensure that the bot is now in a housing server:
    if (!ZBot.isInHousing())
      return;

    // If it is, visit the housing:
    visit();

  }

  public void onHousingChange(Housing housing) {

    // Once the bot is in the correct housing, the command has completed running:
    if (housing.exists() && housing.getOwner().toLowerCase().equals(args.get(0).toLowerCase()))
      ZBot.commandHandler.onComplete("Successfully joined " + housing.getOwner() + "'s housing #" + args.get(1) + ".", true);

  }

  public void onChat(String rawMessage) {

    // Clean the message:
    String message = ZBot.removeFormatting(rawMessage);

    // Something went wrong trying to connect ("Try again in a second." is ignored as it is sent when already in the housing):
    if (message.contains("An exception occurred in your connection, so you were put in the Housing Lobby!") || message.contains("Exception Connecting:ReadTimeoutException : null"))
      ZBot.commandHandler.onError("Something went wrong trying to connect to " + args.get(0) + "'s housing; please try again.", true);
    
    // The bot is already in the specified housing:
    if (message.contains("You are already connected to this server"))
      ZBot.commandHandler.onError("The bot was already connected to " + args.get(0) + "'s housing #" + args.get(1) + ".", true);

    // The specified player doesn't have any houses:
    if (message.contains("No houses found for that player!"))
      ZBot.commandHandler.onError("The player " + args.get(0) + " doesn't have any housings.", true);
    
    // The specified housing is private:
    if (message.contains("You can't visit that house because of their visiting rules!"))
      ZBot.commandHandler.onError("The player " + args.get(0) + "'s housing #" + args.get(1) + " has its visiting rules set to private!", true);
    
  }

  public void onGUI(String GUIName, ArrayList<GUIStack> GUIInventory, int GUIID) {

    // Log the intention:
    commandLog("Joining " + args.get(0) + "'s housing #" + args.get(1) + ".");

    // Ensure that the housing is valid:
    if (Integer.parseInt(args.get(1)) < 1 || Integer.parseInt(args.get(1)) > GUIInventory.size()) {
    
      // No housing was found; close the GUI and return an error:
      ZBot.closeGUI();
      ZBot.commandHandler.onError("Invalid housing number.", "Unable to join " + args.get(0) + "'s housing: Housing #" + args.get(1) + " does not exist.");
      return;

    }

    // Get the slotID of the correct housing:
    int slotID = GUIInventory.get(Integer.parseInt(args.get(1)) - 1).getSlotID();

    // Join the specified housing:
    ZBot.mc.playerController.windowClick(GUIID, slotID, 0, 0, ZBot.mc.thePlayer);

  }

  public String toString() {

    return "[Command_joinHousing]";
    
  }
  
}
