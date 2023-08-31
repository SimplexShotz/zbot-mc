
package com.oshotz.zbotmod;

public class Command_getHousingPlayers extends Command {

  // Inherit the constructors from Command:
  public Command_getHousingPlayers() { super(); }
  public Command_getHousingPlayers(String... args) { super(args); }

  public void run() throws InterruptedException {

    // Ensure the bot is in a house:
    if (!ZBot.isInHouse()) {
      ZBot.commandHandler.onError("The bot is not currently in a Housing.", true);
      return;
    }

    // Return the player list:
    ZBot.commandHandler.onComplete("Successfully retrieved the player list from the housing.", ZBot.getPlayerList());

  }

  public String toString() {

    return "[Command_getHousingPlayers]";
    
  }
  
}
