
package com.oshotz.zbotmod;

import java.util.ArrayList;

public class MongoDBCommsEventHandler extends MongoDBEventHandler {

  /*
    
    Types of Comms methods ("!" signifies that it is ignored here):
     - "ping" [TODO]
       > no args
     - "run"
       > [ <command>, <command args>... ]
    !- "res"
       > [ <response args>... ]
    !- "err"
       > [ <error message> ]
    
  */

  public <T extends MongoDBEvent> void onEvent(T mongoDBEvent) {

    try {

      MongoDBCommsEvent event = (MongoDBCommsEvent)mongoDBEvent;

      switch(event.getMethod()) {

        case "ping":

          System.out.println("pong"); // [TODO] send a message back to mongoDB

        break;
        case "run":

          // Log the event:
          System.out.println("Captured \"run\" event: " + event.toString());

          // Get the commsID:
          String commsID = event.getCommsID();

          // Get the command's args:
          ArrayList<String> args = event.getArgs();

          // Run the command:
          switch(args.get(0)) {

            case "getHousing":
              ZBot.commandHandler.run(new Command_getHousing(), commsID);
            break;
            case "getHousingList":
              ZBot.commandHandler.run(new Command_getHousingList(args.get(1)), commsID);
            break;
            case "getHousingOnline":
              ZBot.commandHandler.run(new Command_getHousingOnline(args.get(1)), commsID);
            break;
            case "getHousingPlayers":
              ZBot.commandHandler.run(new Command_getHousingPlayers(), commsID);
            break;
            case "getHousingStats":
              ZBot.commandHandler.run(new Command_getHousingStats(args.get(1), args.get(2)), commsID);
            break;
            case "joinHousing":
              ZBot.commandHandler.run(new Command_joinHousing(args.get(1), args.get(2)), commsID);
            break;
            case "sendMessage":
              ZBot.commandHandler.run(new Command_sendMessage(args.get(1)), commsID);
            break;

          }

        break;

      }
    
    } catch(Exception e) {}

  }

  public Class<? extends MongoDBEvent> getEventClass() {
    
    return MongoDBCommsEvent.class;
  
  }
  
}
