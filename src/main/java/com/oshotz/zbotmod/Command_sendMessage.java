
package com.oshotz.zbotmod;

import java.util.ArrayList;
import java.util.Arrays;

public class Command_sendMessage extends Command {

  // Inherit the constructors from Command:
  public Command_sendMessage() { super(); }
  public Command_sendMessage(String... args) { super(args); }

  public void run() throws InterruptedException {

    // Send the message:
    ZBot.sendMessage(args.get(0));

  }

  public void onChat(String rawMessage) {

    // Check that the message was not sent by a player:
    if (!ZBot.isMessageSentByPlayer(rawMessage)) {

      // Clean the message:
      String message = ZBot.removeFormatting(rawMessage);

      // Check for any error messages:
      if (message.equals("Chat slow is enabled! You are sending messages too fast please wait and try again.") || message.equals("You cannot say the same message twice!") || message.equals("Advertising is against the rules. You will receive a punishment on the server if you attempt to advertise.") || message.indexOf("You are currently muted for ") == 0)
        ZBot.commandHandler.onError("The message could not be sent.", "error");

    }

    // Ensure the message was sent by the bot (ignore the chat otherwise):
    if (!ZBot.isMessageSentByBot(rawMessage))
      return;

    // Check that the message content matches what was expected:
    if (ZBot.getMessageFromRaw(rawMessage).equals(args.get(0)))
      ZBot.commandHandler.onComplete("The message was sent successfully.", new ArrayList<String>(Arrays.asList("exact")));
    else
      ZBot.commandHandler.onComplete("The message was sent successfully, but does not exactly match the intended message.", new ArrayList<String>(Arrays.asList("altered")));

  }

  public String toString() {

    return "[Command_sendMessage]";
    
  }
  
}
