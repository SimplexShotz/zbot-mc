
package com.oshotz.zbotmod;

import java.util.ArrayList;

public class Command_getHousingOnline extends Command {

  // Inherit the constructors from Command:
  public Command_getHousingOnline() { super(); }
  public Command_getHousingOnline(String... args) { super(args); }

  public void run() throws InterruptedException {

    // Ensure the bot is in a house:
    if (!ZBot.isInHouse()) {
      ZBot.commandHandler.onError("The bot is not currently in a Housing.", true);
      return;
    }

    // Determine if the specified player is online:
    ArrayList<String> playerList = ZBot.getPlayerList();

    for (int i = 0; i < playerList.size(); i++) {

      if (playerList.get(i).toLowerCase().equals(args.get(0).toLowerCase())) {
        ZBot.commandHandler.onComplete("The player " + playerList.get(i) + " is online.", playerList.get(i));
        return;
      }

    }

    ZBot.commandHandler.onError("The player " + args.get(0) + " is not online.", true);

  }

  public String toString() {

    return "[Command_getHousingOnline]";
    
  }
  
}
