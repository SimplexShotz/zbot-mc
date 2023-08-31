
package com.oshotz.zbotmod;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class ZBot {

  // Public, static variables that can also be used by other classes:
  public static MongoClient mongoClient;
  public static CodecRegistry pojoCodecRegistry;
  public static CommandHandler commandHandler;
  public static Minecraft mc = Minecraft.getMinecraft();

  // Fields for the header and footer of the GuiPlayerTabOverlay:
  private static final Field headerField = ReflectionHelper.findField(GuiPlayerTabOverlay.class, "header" , "field_175256_i");
  private static final Field footerField = ReflectionHelper.findField(GuiPlayerTabOverlay.class, "footer" , "field_175255_h");

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    
  }

  @EventHandler
  public void init(FMLInitializationEvent event) throws IOException {

    // Log that the mod is running:
    System.out.println("zBot is running!");

    log("latest.log", "[zBot 0.1a] Mod Started");

    // Setup MongoDB Atlas for communication with the Discord bot:
    setupMongoDB();

    // Ensure that no one else is using the mod; if so, shut down immediately:
    // [TODO]

    // Setup the Command Handler:
    commandHandler = new CommandHandler();

    // Setup the event handlers:

    // Chat event handler:
    ChatEventHandler chatEventHandler = new ChatEventHandler();
    MinecraftForge.EVENT_BUS.register(chatEventHandler);

    // Tick event handler:
    ClientTickEventHandler clientTickEventHandler = new ClientTickEventHandler();
    MinecraftForge.EVENT_BUS.register(clientTickEventHandler);

    // Chest GUI event handler:
    ChestEventHandler chestEventHandler = new ChestEventHandler();
    MinecraftForge.EVENT_BUS.register(chestEventHandler);

    // MongoDB Atlas event handler:
    MongoDBCommsEventHandler mongoDBCommsEventHandler = new MongoDBCommsEventHandler();
    registerMongoDBEventHandler("comms", mongoDBCommsEventHandler);

    // Allow access to the tab "header" and "footer" fields through reflection:
    headerField.setAccessible(true);
    footerField.setAccessible(true);

  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    
  }

  private void setupMongoDB() {

    // Setup the connection settings:
    String connectionString = Reference.connectionString;
    ServerApi serverApi = ServerApi.builder()
      .version(ServerApiVersion.V1)
      .build();
    MongoClientSettings settings = MongoClientSettings.builder()
      .applyConnectionString(new ConnectionString(connectionString))
      .serverApi(serverApi)
      .build();

    // Create a new client and connect to the MongoDB servers:
    mongoClient = MongoClients.create(settings);

    // Create a POJO codec to handle conversion from BSON documents into POJOs:
    CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
    pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

  }

  private <T extends MongoDBEventHandler> void registerMongoDBEventHandler(String collection, T eventHandler) {

    // Run a change stream in a separate thread to watch changes to the specified collection:
    new Thread(() -> {

      ZBot.getCollection(collection, eventHandler.getEventClass()).watch().forEach(doc -> eventHandler.onEvent(doc.getFullDocument()));

    }).start();

  }

  public <T> void printFullDoc(ChangeStreamDocument<T> doc) {

    // Print the full document (mainly meant for debugging purposes):
    System.out.println(doc.getFullDocument());

  }

  public void log(String name, String data) {

    // Directory path:
    String PATH = "./logs/zBot";

    // Create the zBot logs directory if it doesn't already exist:
    File directory = new File(PATH);
    System.out.println(directory.exists());
    if (!directory.exists())
      directory.mkdirs();

    // Create the file:
    File file = new File(PATH + "/" + name);
    try {

      // Write the data to the file:
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(data);
      bw.close();

    } catch (IOException e) {

      // Print any errors/exceptions:
      e.printStackTrace();

    }

  }

  public static <T> MongoCollection<T> getCollection(String collection, Class<T> className) {

    // Get the specified collection from the zBot-Database and return it with the Codec registry for POJO conversion:
    MongoDatabase database = mongoClient.getDatabase("zBot-Database").withCodecRegistry(pojoCodecRegistry);
    return database.getCollection(collection, className);

  }

  public static void sendMessage(String message) {

    // Send a message in chat (displayed to all other users):
    mc.thePlayer.sendChatMessage(message);

  }

  public static void sendCommand(String command) {

    // Send a command in the chat with no arguments:
    mc.thePlayer.sendChatMessage("/" + command);

  }

  public static void sendCommand(String command, String arg) {

    // Send a command in the chat with one argument:
    mc.thePlayer.sendChatMessage("/" + command + " " + arg);

  }

  public static void sendCommand(String command, ArrayList<String> args) {

    // Send a command in the chat with arguments:
    mc.thePlayer.sendChatMessage("/" + command + " " + String.join(" ", args));

  }

  public static void displayMessage(String message) {

    // Send a message in chat (displayed only to the user of the mod):
    mc.thePlayer.addChatMessage(new ChatComponentText(message));

  }

  public static void closeGUI() {

    mc.thePlayer.closeScreen();

  }

  public static boolean isInHouse() {

    // Get the tab footer:
    String tabFooter = getTabFooter();

    // If the player is in a Housing server, and the footer contains all of the default housing text, the player is most likely in a housing:
    return isInHousing() && tabFooter.contains("You are in ") && tabFooter.contains(", by ") && tabFooter.contains("Guests: ") && tabFooter.contains("Cookies: ");

  }

  public static boolean isInHousing() {

    // Get the sidebar header:
    String sidebarHeader = getSidebarHeader();

    // If the header contains "HOUSING", the player is most likely in housing:
    return sidebarHeader.contains("HOUSING");

  }

  public static String getMessageSender(String rawMessage) {

    // Clean the message:
    String message = removeFormatting(rawMessage);

    // If the message does not contain a colon, it cannot have been sent by a user:
    if (!message.contains(": "))
      return "";
    
    // If the message starts with an asterisk (*), it's a housing message:
    if (message.indexOf("*") == 0)
      return "";

    // All messages will have a colon and a space after the username:
    message = message.split(": ")[0];

    // Remove any ranks from the message:
    message = removeRank(message);

    // In the event that there is some additional suffix, and the player is a non, it may not have been caught; hence, we need to do some additional cleaning:
    int messageSplitLength = message.split(" ").length;
    String player = message.split(" ")[messageSplitLength - 1];

    // Verify that the username is, in fact, a valid username:
    String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
    int usernameLengthLimit = 16;

    // "Username" is longer than 16 characters:
    if (player.length() > usernameLengthLimit)
      return "";

    // "Username" contains an invalid character:
    for (int i = 0; i < player.length(); i++) {
      if (validChars.indexOf(player.charAt(i)) == -1)
        return "";
    }

    // Return the username:
    return player;

  }

  public static String getMessageSenderRank(String rawMessage) {

    // If the message wasn't sent by a player, return immediately:
    if (!isMessageSentByPlayer(rawMessage))
      return "";
    
    // Clean the message:
    String message = removeFormatting(rawMessage);

    // Get the clean string from the message:
    String cleanString = message.substring(0, message.indexOf(": "));

    // Get the rank from the clean string:
    return getRank(cleanString);

  }

  public static String getMessageSenderRankColor(String rawMessage, String rank) {

    // If the message wasn't sent by a player, return immediately:
    if (!isMessageSentByPlayer(rawMessage))
      return "";
    
    // If they don't have a rank, they will always be grey:
    if (rank.equals(""))
      return "grey";
    
    // VIP and VIP+ are always green, return immediately:
    if (rank.equals("[VIP]") || rank.equals("[VIP+]"))
      return "green";

    // MVP and MVP+ are always aqua, return immediately:
    if (rank.equals("[MVP]") || rank.equals("[MVP+]"))
      return "aqua";
    
    // If the user is not MVP++, return immediately:
    if (!rank.equals("[MVP++]"))
      return "red";
    
    // Otherwise, get the color code before the rank:
    String colorCode = rawMessage.charAt(rawMessage.indexOf("[MVP") - 1) + "";

    // Convert the color code to a color and return it:
    return Reference.colors.get(Integer.parseInt(colorCode, 16));
  
  }

  public static String getMessageSenderPlusColor(String rawMessage, String rank) {

    // If the message wasn't sent by a player, return immediately:
    if (!isMessageSentByPlayer(rawMessage))
      return "";
    
    // VIP+ is always gold, return immediately:
    if (rank.equals("[VIP+]"))
      return "gold";

    // If the rank isn't an MVP+ variant, return immediately:
    if (!rank.equals("[MVP+]") && !rank.equals("[MVP++]"))
      return "";
    
    // Otherwise, get the color code before the "+":
    String colorCode = substringBetween(rawMessage, "[MVP\u00A7", "+");

    // Convert the color code to a color and return it:
    return Reference.colors.get(Integer.parseInt(colorCode, 16));
  
  }

  public static String getMessageFromRaw(String rawMessage) {

    // If the message wasn't sent by a player, return immediately:
    if (!isMessageSentByPlayer(rawMessage))
      return "";

    // Get the clean string from the message:
    String message = removeFormatting(rawMessage);

    // Return everything after the ": " part of the message:
    return message.substring(message.indexOf(": ") + 2);

  }

  public static boolean isMessageSentByPlayer(String rawMessage) {

    // Return whether or not the message sender is a player:
    return !getMessageSender(rawMessage).equals("");

  }

  public static boolean isMessageSentByBot(String rawMessage) {

    // Get the bot's username:
    String playerName = mc.thePlayer.getName();

    // Return whether or not the message sender is the bot:
    return getMessageSender(rawMessage).equals(playerName);

  }

  // worry about this later lol
  // public static String getkiller and getthe other dude [TODO]

  public static String removeFormatting(String rawMessage) {

    String cleanMessage = "", curCharacter;
    for (int i = 0; i < rawMessage.length(); i++) {

      curCharacter = rawMessage.substring(i, i + 1);

      if (curCharacter.equals("\u00A7")) {
        i++;
      } else {
        cleanMessage += curCharacter;
      }

    }

    return cleanMessage;

  }

  public static ArrayList<GUIStack> getGUIInventory(IInventory chestInventory) {

    ArrayList<GUIStack> GUIInventory = new ArrayList<GUIStack>();

    // Loop through the items in the Chest GUI:
    for (int i = 0; i < chestInventory.getSizeInventory(); i++) {

      // Ensure that the stack isn't empty:
      if (chestInventory.getStackInSlot(i) != null)
        GUIInventory.add(new GUIStack(chestInventory.getStackInSlot(i), i));
  
    }

    // Return the GUI inventory:
    return GUIInventory;

  }

  public static String getIdentifier(ItemStack itemStack) {

    // Get the namespaced identifier of the item ("minecraft:log", for example):
    String namespacedIdentifier = Item.itemRegistry.getNameForObject(itemStack.getItem()).toString();

    // Return the identifier without the namespace:
    return namespacedIdentifier.substring(namespacedIdentifier.indexOf(":") + 1);

  }

  public static ArrayList<String> getLore(ItemStack itemStack) {

    // Keep track of the lore to return in an ArrayList:
    ArrayList<String> lore = new ArrayList<String>();

    // Get the full NBT from the item stack:
    NBTTagCompound fullNBT = itemStack.getTagCompound();

    // Return if the item stack has no NBT data:
    if (fullNBT == null)
      return lore;

    // Get the display tag from the full NBT:
    NBTTagCompound displayNBT = fullNBT.getCompoundTag("display");

    // Return if the item stack has no display NBT data:
    if (displayNBT == null)
      return lore;
      
    // Get the lore tag from the display NBT:
    NBTTagList loreNBT = displayNBT.getTagList("Lore", 8);

    // Return if the item stack has no lore NBT data:
    if (loreNBT == null)
      return lore;
      
    // Otherwise, append the lore to the lore ArrayList:
    for (int i = 0; i < loreNBT.tagCount(); i++)
      lore.add(loreNBT.get(i).toString().substring(1, loreNBT.get(i).toString().length() - 1));

    // Return the lore ArrayList:
    return lore;

  }

  public static ArrayList<Enchantment> getEnchantments(ItemStack itemStack) {

    // Keep track of the enchantments to return in an ArrayList:
    ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();

    // Get the enchantment NBT from the item stack:
    NBTTagList enchantmentNBT = itemStack.getEnchantmentTagList();

    // Return if the item stack has no enchantment NBT data:
    if (enchantmentNBT == null)
      return enchantments;
      
    // Otherwise, append the enchantments to the lore ArrayList:
    for (int i = 0; i < enchantmentNBT.tagCount(); i++)
      enchantments.add(new Enchantment(Reference.enchantments.get(Integer.valueOf(enchantmentNBT.getCompoundTagAt(i).getShort("id"))), Integer.valueOf(enchantmentNBT.getCompoundTagAt(i).getShort("lvl"))));

    // Return the enchantment ArrayList:
    return enchantments;

  }

  public static String getRank(String cleanString) {

    ArrayList<String> ranks = new ArrayList<String>(Arrays.asList("[VIP]", "[VIP+]", "[MVP]", "[MVP+]", "[MVP++]", "[YOUTUBE]", "[YT]", "[MOJANG]", "[EVENTS]", "[MCP]", "[PIG]", "[PIG+]", "[PIG++]", "[PIG+++]", "[TOMMY]", "[GM]", "[ADMIN]", "[OWNER]", "[HELPER]", "[JR HELPER]", "[MOD]", "[BUILD TEAM]", "[BUILD TEAM+]", "[SPECIAL]", "[RETIRED]", "[BETA TESTER]", "[GOD]", "[ABOVE THE RULES]", "[MCProHosting]"));
    for (String rank : ranks) {

      if (cleanString.contains(rank + " "))
        return rank;

    }

    return "";

  }

  public static String removeRank(String cleanString) {

    String rank = getRank(cleanString);

    if (rank.equals(""))
      return cleanString;
    
    return cleanString.substring(cleanString.indexOf(rank + " ") + (rank.length() + 1));

  }

  public static String substringBetween(String string, String start, String end) {

    // Substring between the start and the end strings:
    return string.substring(string.indexOf(start) + start.length(), string.indexOf(end, string.indexOf(start) + start.length()));

  }

  public static Housing getHousing() {

    // Get the tab footer to get the housing from:
    String tabFooter = getTabFooter();

    // If not in a house, return now:
    if (!isInHouse())
      return new Housing();

    try {

      // Otherwise, grab the housing's owner, name, guests, and cookies from the tab header:
      String name = substringBetween(tabFooter, "\nYou are in ", ", by ");
      String owner = substringBetween(tabFooter, "\nYou are in " + name + ", by ", "\n\u00A7s\nGuests: ");
      String guests = substringBetween(tabFooter, "\nYou are in " + name + ", by " + owner + "\n\u00A7s\nGuests: ", " | Cookies: ");
      String cookies = substringBetween(tabFooter, "\nYou are in " + name + ", by " + owner + "\n\u00A7s\nGuests: " + guests + " | Cookies: ", "\n\u00A7s\nRanks, Boosters & MORE! STORE.HYPIXEL.NET");

      // Return the housing data:
      return new Housing(removeRank(owner), name, Integer.parseInt(String.join("", guests.split(","))), Integer.parseInt(String.join("", cookies.split(","))));

    } catch(Exception err) {

      return new Housing();

    }

  }

  public static String getTabHeader() {

    try {

      // Get the tab list:
      GuiPlayerTabOverlay tabList = mc.ingameGUI.getTabList();

      // Get the header from the tab list and convert it to a String:
      IChatComponent headerComponent = (IChatComponent)headerField.get(tabList);
      String header = headerComponent.getUnformattedText();

      // Return the header data:
      return header;

    } catch(Exception err) {

      return "";

    }

  }

  public static String getTabFooter() {

    try {

      // Get the tab list:
      GuiPlayerTabOverlay tabList = mc.ingameGUI.getTabList();

      // Get the footer from the tab list and convert it to a String:
      IChatComponent footerComponent = (IChatComponent)footerField.get(tabList);
      String footer = footerComponent.getUnformattedText();

      // Return the footer data:
      return footer;

    } catch(Exception err) {

      return "";

    }

  }

  public static ArrayList<String> getPlayerList() {

    try {

      // Create the player list for storing usernames:
      ArrayList<String> playerList = new ArrayList<String>();

      // Get the online player list and extract their usernames:
      // ArrayList<NetworkPlayerInfo> networkPlayerInfoList = new ArrayList<NetworkPlayerInfo>(mc.thePlayer.sendQueue.getPlayerInfoMap()); [TODO]
      ArrayList<NetworkPlayerInfo> networkPlayerInfoList = new ArrayList<NetworkPlayerInfo>(mc.getNetHandler().getPlayerInfoMap());

      for (NetworkPlayerInfo player : networkPlayerInfoList)
        if (!player.getGameProfile().getName().equals("Carpenter "))
          playerList.add(player.getGameProfile().getName());

      // Return the player list:
      return playerList;

    } catch(Exception err) {

      return new ArrayList<String>();

    }

  }

  public static String getSidebarHeader() {

    try {

      // Get the scoreboard:
      Scoreboard scoreboard = mc.theWorld.getScoreboard();

      // If none exists, return an empty string:
      if (scoreboard == null)
        return "";

      // Get the sidebar:
      ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);

      // If none exists, return an empty string:
      if (sidebar == null)
        return "";

      // Return the sidebar name:
      return sidebar.getDisplayName();

    } catch(Exception err) {

      return "";

    }
    
  }

  public static ArrayList<String> getSidebarLines() {

    try {

      // Setup the ArrayList for the sidebar lines:
      ArrayList<String> lines = new ArrayList<String>();

      // Get the scoreboard:
      Scoreboard scoreboard = mc.theWorld.getScoreboard();

      // If none exists, return an empty ArrayList:
      if (scoreboard == null)
        return lines;

      // Get the sidebar:
      ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);

      // If none exists, return an empty ArrayList:
      if (sidebar == null)
        return lines;

      // Get the scores in the sidebar:
      Collection<Score> scores = scoreboard.getSortedScores(sidebar);
      ArrayList<Score> list = Lists.newArrayList(scores.stream()
        .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
        .collect(Collectors.toList()));

      scores = (list.size() > 15) ? Lists.newArrayList(Iterables.skip(list, scores.size() - 15)) : list;

      for (Score score : scores) {
        ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
        lines.add(0, ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
      }

      // Return the sidebar lines:
      return lines;

    } catch(Exception err) {

      return new ArrayList<String>();

    }

  }
  
}
