package Logging;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Logging {


    public static void logEvent(MessageReceivedEvent event) {
        System.out.println("Message received by: " +

                event.getAuthor().getName() +
                "#" +
                event.getAuthor().getDiscriminator() +
                " : " +
                event.getMessage().getContentDisplay());
    }
}
