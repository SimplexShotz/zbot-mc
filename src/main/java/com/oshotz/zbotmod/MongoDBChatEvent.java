
package com.oshotz.zbotmod;

import org.bson.types.ObjectId;

public class MongoDBChatEvent extends MongoDBEvent {

  // Private class variables:
  private ObjectId id;
  private String prefix;
  private String message;

  // Constructors:
  MongoDBChatEvent() { }

  MongoDBChatEvent(String rawMessage) {

    int rankIndex = ZBot.getMessageSenderRank(rawMessage).equals("") ? (rawMessage.indexOf(":") - ZBot.getMessageSender(rawMessage).length() - 4) : (rawMessage.lastIndexOf("[", rawMessage.indexOf(":")) - 2);
    int prefixIndex = (rankIndex > 0) ? (rawMessage.lastIndexOf("[", rankIndex) - 2) : 0;

    prefix = (rankIndex > 0) ? (rawMessage.substring(prefixIndex, rawMessage.indexOf("]", prefixIndex) + 1)) : "";
    message = rawMessage.substring(rankIndex);

  }

  MongoDBChatEvent(String prefix, String message) {

    this.prefix = prefix;
    this.message = message;

  }

  // Public getters and setters:
  public ObjectId getID() {

    return id;

  }

  public void setID(ObjectId id) {

    this.id = id;

  }

  public void setPrefix(String prefix) {

    this.prefix = prefix;

  }

  public String getPrefix() {

    return prefix;

  }

  public void setMessage(String message) {

    this.message = message;

  }

  public String getMessage() {

    return message;

  }
  
  public String type() {

    return "MongoDBChatEvent";

  }

  @Override
  public String toString() {

    return "MongoDBChatEvent [id=" + id + ", prefix=" + prefix + ", message=" + message + "]";

  }
  
}
