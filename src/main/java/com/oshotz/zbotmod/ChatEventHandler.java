
package com.oshotz.zbotmod;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatEventHandler {

  /*
    onServerChat(event)
     < event: The chat event
     - Handles chats that come in to the client
   
  */
  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onServerChat(ClientChatReceivedEvent event) throws IllegalArgumentException, IllegalAccessException {

    // Ensure that this is an actual chat message, and not a log/action bar update:
    if (event.type != 0)
      return;

    // Get the message as a String:
    String message = event.message.getUnformattedText();

    // Handle the chat messages in a separate thread:
    new Thread(() -> {

      // Invoke the event handler for commands to use:
      ZBot.commandHandler.onChat(message);

      // Put anything that should be run if anyone sends it before this line; anything after this line will be ignored if the bot sent it:
      if (ZBot.isMessageSentByBot(message)) {

        // If the message was sent by the bot, check if the message was sent from Discord and insert it into MongoDB Atlas:
        String cleanMessage = ZBot.getMessageFromRaw(message);
        if (cleanMessage.indexOf("[DISCORD] ") != 0 || !cleanMessage.contains(":") || cleanMessage.indexOf(":") <= cleanMessage.indexOf("[DISCORD] "))
          return;

        MongoDBChatEvent mongoDBChatEvent = new MongoDBChatEvent("\u00A79[DISCORD] \u00A79" + cleanMessage.substring(10, cleanMessage.indexOf(":")) + "\u00A7f" + cleanMessage.substring(cleanMessage.indexOf(":")));
        ZBot.getCollection("chat", MongoDBChatEvent.class).insertOne(mongoDBChatEvent);
        return;

      }

      // If the message was sent by a player, insert the message into MongoDB Atlas:
      if (ZBot.isMessageSentByPlayer(message))
        ZBot.getCollection("chat", MongoDBChatEvent.class).insertOne(new MongoDBChatEvent(message));
        
      // System.out.println(ZBot.getMessageSender(message)); [TODO] moderation tools

      // if (message.contains("[-]") && message.contains("/visit ")) {
      //   ZBot.sendCommand("h", "kick " + ZBot.getMessageSender(message))
      // }

      return;

    }).start();

  }
  
}
