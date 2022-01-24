import Database.DatabaseConfig;
import Listener.JdaListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static String botToken = ""; //Disney Bot Token 11/7/2020

    public static void main(String[] args) throws LoginException, SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        DatabaseConfig.getConnection();
        JDABuilder.createLight(botToken)
                .addEventListeners(new JdaListener())
                .setActivity(Activity.playing("Type dis.help!"))
                .build();
    }
}