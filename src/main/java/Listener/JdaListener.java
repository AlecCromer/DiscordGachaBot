package Listener;

import Database.DatabaseCommand;
import Logging.DetermineFunction;
import MessageProcess.ProcessMessage;
import MessageProcess.User;
import MessageProcess.Utils;
import MessageProcess.Character;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Random;

import static Logging.Logging.logEvent;

public class JdaListener extends ListenerAdapter {

    private static String botId = "734083477145649162";
    private static final String initializerString = "dis.";


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        DatabaseCommand databaseCommand = new DatabaseCommand();
        Long messageId = event.getMessageIdLong();
        if (!event.getUser().getId().equals(botId)) {

            if (event.getReaction().getReactionEmote().getName().equals("➡")) {
                databaseCommand.changeMessageSplitPageNumber("increase", messageId);
                editReply(event, databaseCommand, databaseCommand.getQueryFromSplitPageNumber(messageId));

            }
            if (event.getReaction().getReactionEmote().getName().equals("⬅")) {
                databaseCommand.changeMessageSplitPageNumber("decrease", messageId);
                editReply(event, databaseCommand, databaseCommand.getQueryFromSplitPageNumber(messageId));
            }
        }
    }

    @Override // If a user leaves discord and re-joins, give them their rank and such.
    public void onGuildMemberJoin(GuildMemberJoinEvent evt) {
        DatabaseCommand databaseCommand = new DatabaseCommand();

        if (!databaseCommand.userExists(evt.getUser().getId())) {
            addUser(evt.getMember());
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent groupJoinEvent) {
        //TODO: SAVE USERS
        List<Member> discordUserList = groupJoinEvent.getGuild().getMembers();

        discordUserList.forEach(this::addUser);
    }


    public void editReply(MessageReactionAddEvent event, DatabaseCommand databaseCommand, String[] query) {
        String queryCommand = query[0];
        int offset = Integer.valueOf(query[1]) * 20;
        String finalQuery = queryCommand + " LIMIT 20 OFFSET " + String.valueOf(offset);
        List<Character> characters = databaseCommand.getCharactersFromQuery(finalQuery);
        StringBuilder out = new StringBuilder();
        if (characters.size() == 0) {
            databaseCommand.changeMessageSplitPageNumber("decrease", event.getMessageIdLong());
        }
        characters.forEach(character -> out.append(character.getId() + ": " + character.getName() + " \n"));
        MessageChannel channel = event.getChannel();
        if (out.toString().length() > 0) {
            channel.editMessageById(event.getMessageIdLong(), generateEmbedMessage(out.toString(), null).build()).queue();
        }
    }

    public EmbedBuilder generateEmbedMessage(String output, String imageOutPut) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (output != null) {
            embedBuilder.setDescription(Utils.replacer(output));
        }
        if (imageOutPut != null) {
            embedBuilder.setImage(imageOutPut);
        }
        return embedBuilder;
    }


    public void replyBack(MessageReceivedEvent event, String output, String imageOutPut, String messageQuery) {
        DatabaseCommand databaseCommand = new DatabaseCommand();
        if (messageQuery != null) {
            event.getChannel().sendMessage(generateEmbedMessage(output, imageOutPut).build()).queue((message -> {
                long messageId = message.getIdLong();
                databaseCommand.addMessageSplit(messageId, messageQuery);
                event.getTextChannel().addReactionById(messageId, "⬅").queue();
                event.getTextChannel().addReactionById(messageId, "➡").queue();
            }));
        } else {
            event.getChannel().sendMessage(generateEmbedMessage(output, imageOutPut).build()).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        logEvent(event);

        String OriginalMessage = event.getMessage().getContentRaw();
        DatabaseCommand databaseCommand = new DatabaseCommand();
        if (!botId.equals(event.getAuthor().getId()) && OriginalMessage.length() > 4) {


            if (!databaseCommand.userExists(event.getAuthor().getId())) {
                addUser(event.getMember());
            }
            User user = databaseCommand.getUser(event.getAuthor().getId());
            if (OriginalMessage.substring(0, initializerString.length()).equalsIgnoreCase(initializerString)) {

                String ModifiedMessage = OriginalMessage.substring(initializerString.length());
                ProcessMessage processMessage = new ProcessMessage(user, ModifiedMessage);
                DetermineFunction determineFunction = new DetermineFunction(processMessage, user, databaseCommand, event.getTextChannel().getId());
                if (determineFunction.getQuery() != null) {
                    replyBack(event, determineFunction.getOutPut(), determineFunction.getImageOutPut(), determineFunction.getQuery());
                } else {
                    replyBack(event, determineFunction.getOutPut(), determineFunction.getImageOutPut(), null);
                }

            }
            generateRandomCharacter(databaseCommand, event);
        }

    }


    private void generateRandomCharacter(DatabaseCommand databaseCommand, MessageReceivedEvent event) {
        //5% chance of doing a claimable character
        if (new Random().nextInt(100) > 98) {
            DetermineFunction determineFunction = new DetermineFunction(databaseCommand, event.getTextChannel().getId());
            replyBack(event, determineFunction.getOutPut(), determineFunction.getImageOutPut(), null);
        }
    }

    private boolean addUser(Member u) {
        User user = new User(u.getId(), u.getEffectiveName(), null, 20, null);
        DatabaseCommand databaseCommand = new DatabaseCommand();

        if (null != databaseCommand.addUserToDatabase(user)) {
            System.out.println("User added : " + u.getId() + " : " + (u.getEffectiveName()));
            return true;
        } else {
            return false;
        }
    }
}
