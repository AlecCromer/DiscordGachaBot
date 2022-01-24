package MessageProcess;

public class Utils {

    public static String bold(String input) {
        return "**" + input + "**";
    }

    //This is used to replace some of the weird characters in the database from the character parser
    public static String replacer(String unformatted) {
        unformatted = unformatted.replace("&#039;", "'");
        unformatted = unformatted.replace("<br />", "\n");
        unformatted = unformatted.replace("<br>", "\n");
        unformatted = unformatted.replace("<br","\n");
        unformatted = unformatted.replace("&quot;","\"");
        unformatted = unformatted.replace(" - MyShowList.net", "");
        unformatted = unformatted.replace("_", " ");
        unformatted = unformatted.replace("&amp;quot;", "\"");
        unformatted = unformatted.replace("%27", "'");
        return unformatted;
    }
}
