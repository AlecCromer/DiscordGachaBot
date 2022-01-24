package Logging;

import Database.DatabaseCommand;
import MessageProcess.Character;
import MessageProcess.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DetermineFunction {


    private String[] scarArray = {"https://media.giphy.com/media/a4sJykNINf0f6/giphy.gif",
            "https://media.giphy.com/media/eD44Pqc11LkKk/giphy.gif",
            "https://media.giphy.com/media/baCkq7AvREVqw/giphy.gif",
            "https://media.giphy.com/media/3E4kGRRuQBWZW/giphy.gif",
            "https://media1.tenor.com/images/8238436cbb655818c18b0d35ad7a4306/tenor.gif",
            "https://media.giphy.com/media/PiXKktwXOdx2E/giphy.gif",
            "https://media.giphy.com/media/ZocgK699fb1u0/giphy.gif",
            "https://media.giphy.com/media/Ne9M3Om2H3Dva/giphy.gif",
            "https://media.giphy.com/media/V7lDsommeBghG/giphy.gif",
            "https://media.giphy.com/media/vHAf6A1kGD3Ak/giphy.gif",
            "https://media.giphy.com/media/D8pdAjF5KaEzS/giphy.gif",
            "https://media.giphy.com/media/D8pdAjF5KaEzS/giphy.gif",
            "https://media.giphy.com/media/Mb1eSc13QooCc/giphy.gif"
    };

    private String[] gifArray = {"https://media.giphy.com/media/xjadUUMhIqq7m/giphy.gif",//Nick
            "https://media.giphy.com/media/Mnw7GYKVJjX20/giphy.gif",//pumba
            "https://media.giphy.com/media/wNDa1OZtvl6Fi/giphy.gif",//Remy
            "https://media.giphy.com/media/ZmZ5hwzX1dgQw/giphy.gif"//baymax
    };
    private String outPut;

    private String imageOutPut = null;

    private ProcessMessage processedMessage;

    private User user;

    private DatabaseCommand databaseCommand;

    private String query;

    private int maxOut = 20;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    private String textChannelId;

    public String getTextChannelId() {
        return textChannelId;
    }

    public void setTextChannelId(String textChannelId) {
        this.textChannelId = textChannelId;
    }

    public String getOutPut() {
        return outPut;
    }

    public void setOutPut(String outPut) {
        this.outPut = outPut;
    }

    public String getImageOutPut() {
        return imageOutPut;
    }

    public void setImageOutPut(String imageOutPut) {
        this.imageOutPut = imageOutPut;
    }

    public ProcessMessage getProcessedMessage() {
        return processedMessage;
    }


    public DetermineFunction(ProcessMessage processedMessage, User user, DatabaseCommand databaseCommand, String textChannelId) {
        this.processedMessage = processedMessage;
        this.user = user;
        this.databaseCommand = databaseCommand;
        this.textChannelId = textChannelId;
        determine();

    }

    //Used for random claims
    public DetermineFunction(DatabaseCommand databaseCommand, String textChannelId) {
        this.databaseCommand = databaseCommand;
        this.textChannelId = textChannelId;
        generateClaim();
    }

    private void claim(String nameGuess) {
        UserCharacter userCharacter = databaseCommand.getClaimCharacter(nameGuess, getTextChannelId());
        if (userCharacter.getId() != null) {
            userCharacter.setAffection(0);
            userCharacter.setType(Type.getRandomType().toString());
            databaseCommand.addCharacterToUser(user, userCharacter);
            if (databaseCommand.removeClaimChannelCharacter(getTextChannelId())) {
                setOutPut("Congratulations " + user.getUserName() + "! " + userCharacter.getName() + " has been claimed!");
                setImageOutPut(userCharacter.getCharacterImage().getImageUrl());
            }
        } else {
            setOutPut("Apologies, but that is not the correct name.");
        }
    }

    private void generateClaim() {
        StringBuilder out = new StringBuilder();
        UserCharacter userCharacter = databaseCommand.getRandomCharacter();
        databaseCommand.setClaimChannelCharacter(getTextChannelId(), userCharacter.getId(), userCharacter.getCharacterImage().getId());
        setImageOutPut(userCharacter.getCharacterImage().getImageUrl());
        out.append(Utils.bold("A random character has appeared!"));
        out.append("\n\n");
        out.append(generateHiddenCharacterName(userCharacter.getName()));
        setOutPut(out.toString());
    }

    private String generateHiddenCharacterName(String name) {
        StringBuilder hiddenName = new StringBuilder();
        String[] nameArray = Utils.replacer(name).split(" ");
        for (int i = 0; i < nameArray.length; i++) {
            hiddenName.append(nameArray[i].charAt(0)); //Grab first letter of name
            for (int j = 0; j < nameArray[i].length() - 1; j++) {
                hiddenName.append("\\*");//for each remaining character, add stars
            }
            hiddenName.append(" ");
        }
        hiddenName.substring(0, hiddenName.length() - 1);
        return hiddenName.toString();
    }

    private void replace(String[] movie) {
        String out = "";
        for (String value : movie) {
            if (!value.equalsIgnoreCase("replace")) {
                out = out + value + " ";
            }
        }
        out = out.substring(0, out.length() - 1);
        int count = databaseCommand.replace(out);
        if (count > 0) {
            setOutPut(count + " characters have been set to " + out);
        } else {
            setOutPut("No descriptions have been found with that movie/IP.");
        }
    }

    private void gif() {
        List<String> gifs = new ArrayList<>();
        gifs.addAll(Arrays.asList(gifArray));
        Random rand = new Random();
        setImageOutPut(gifs.get(rand.nextInt(gifs.size())));
    }

    private void scar() {
        List<String> gifs = new ArrayList<>();
        gifs.addAll(Arrays.asList(scarArray));
        Random rand = new Random();
        setImageOutPut(gifs.get(rand.nextInt(gifs.size())));
    }

    private void determine() {
        //TODO: CREATE ENUMS!!!!
        String[] message = getProcessedMessage().getMessage().split(" ");
        String function = message[0];

        if ("GIF".equalsIgnoreCase(function)) {
            gif();
        } else if ("SCAR".equalsIgnoreCase(function)) {
            scar();
        } else if ("REPLACE".equalsIgnoreCase(function) && user.getId().equalsIgnoreCase("183763003760443392")) {
            replace(message);
        } else if ("STOP".equalsIgnoreCase(function)) {
            setOutPut("Alec has decided to shut me down for the night. See you soon!");
        } else if ("CLAIM".equalsIgnoreCase(function)) {
            try {
                claim(getProcessedMessage().getMessage().substring(6));
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect");
            }
        } else if ("FAVORITE".equalsIgnoreCase(function)) {
            try {
                favorite(Integer.valueOf(message[1]));
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect");
            }
        } else if ("SC".equalsIgnoreCase(function)) {
            try {
                sc(message);
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect. Try entering an show name");
            }
        } else if ("INFO".equalsIgnoreCase(function) || "NAME".equalsIgnoreCase(function)) {
            try {
                info(message);
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect. Try entering a character name");
            }
        } else if ("PROFILE".equalsIgnoreCase(function)) {
            profile();
        } else if ("GROLL".equalsIgnoreCase(function)) {
            groll();
        } else if ("DELETE".equalsIgnoreCase(function)) {
            try {
                delete(Integer.valueOf(message[1]));
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect");
            }
        } else if ("VIEW".equalsIgnoreCase(function)) {
            try {
                view(Integer.valueOf(message[1]));
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect");
            }
        } else if ("CHARACTER".equalsIgnoreCase(function) || "ID".equalsIgnoreCase(function)) {
            try {
                character(Integer.valueOf(message[1]));
            } catch (Exception e) {
                setOutPut("The parameter you have entered is incorrect");
            }
        } else if ("LIST".equalsIgnoreCase(function)) {
            list();
        } else if ("HELP".equalsIgnoreCase(function)) {
            help();
        } else if ("DAILYGACHA".equalsIgnoreCase(function)) {
            dailygacha();
        } else if ("SPEAK".equalsIgnoreCase(function)) {
            speak();
        } else if ("DAILY".equalsIgnoreCase(function)) {
            daily();
        } else if ("HIGHEST".equalsIgnoreCase(function)) {
            highest();
        } else {
            setOutPut("Just between us, you might want to work on that spelling of yours.");
            setImageOutPut("https://ohmy.disney.com/wp-content/uploads/2017/03/scar-surrounded-by-idiots.gif");
        }
    }

    private void speak() {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get("src/speak.txt")), "UTF-8");
        } catch (IOException e) {
            System.out.println(e);
        }
        setOutPut(content);

    }

    private void list() {
        StringBuilder out = new StringBuilder();
        QueryBuilder queryBuilder = new QueryBuilder();
        List<Character> characters = databaseCommand.getUserCharacterList(queryBuilder, user.getId(), maxOut, 0);
        if (characters.size() == maxOut) {
            String query = "SELECT w.id, uw.local_id, w.name FROM disneydatabase.user_character uw JOIN disneydatabase.character w ON uw.character_id = w.id WHERE uw.user_id = " + user.getId();
            setQuery(query);
        }
        characters.forEach(character -> out.append(character.getId() + ": " + character.getName() + " \n"));
        setOutPut("Here is a list of all of your characters\n" + out.toString());
    }

    private void view(Integer localId) {

        UserCharacter character = databaseCommand.getCharacterByUserAndLocalId(user, localId);
        StringBuilder out = new StringBuilder();

        if (character.getLocalId() != null) {
            out.append(character.getId() + " - " + Utils.bold(character.getName()));
            out.append("\n");
            out.append("show: " + character.getShow());
            out.append("\n");
            out.append("local Id: " + character.getLocalId());
            out.append("\n");
            out.append("affection: " + character.getAffection());
            out.append("\n");
            out.append("type: " + character.getType());
            out.append("\n");
            setImageOutPut(character.getCharacterImage().getImageUrl());
            setOutPut(out.toString());
        } else {
            setOutPut("That is not a valid character number");
        }

    }

    //TODO: MAKE AN OUTPUT CLASS!!!!!!!!
    private void groll() {
        String function = "DAILY";
        Integer grollCost = 10;
        //Give user a character, subtract from charactercount
        if (grollCost <= user.getCharacterDust()) {
            UserCharacter userCharacter = databaseCommand.getRandomCharacter();
            userCharacter.setAffection(0);
            userCharacter.setType(Type.getRandomType().toString());
            if (databaseCommand.addCharacterToUser(user, userCharacter)) {
                databaseCommand.addFunctionToTimeTable(function, user);
                databaseCommand.changeCharacterDustByUser(user, user.getCharacterDust() - grollCost);
                setOutPut("Roll result: " + Utils.bold(userCharacter.getName()) + " (" + userCharacter.getType() + ")" + "\nDisney IP: " + userCharacter.getShow() + "\n\n" + userCharacter.getDescription());
                setImageOutPut(userCharacter.getCharacterImage().getImageUrl());
            } else {
                setOutPut("FUCKIN YIKES FAM, SYSTEM BROKE, CONTACT ALEC");
            }
        } else {
            setOutPut("Sorry, you do not have enough weeb points for a groll. You only have " + user.getCharacterDust() + " weeb points remaining.");

        }
    }

    //TODO: IMPLEMENT DAILYGACHA
    private void dailygacha() {
        String function = "DAILYGACHA";
        //Give user a free character
        UserCharacter userCharacter = databaseCommand.getRandomCharacter();
        userCharacter.setAffection(0);
        userCharacter.setType(Type.getRandomType().toString());
        if (!databaseCommand.checkIfFunctionIsAvailable(function, user)) {
            setOutPut("Sorry, you have already done your dailygacha, come back at 8PM EST");
        } else {
            if (databaseCommand.addCharacterToUser(user, userCharacter)) {
                databaseCommand.addFunctionToTimeTable(function, user);
                setOutPut("Roll result: **" + userCharacter.getName() + "** (" + userCharacter.getType() + ")" + "\n Disney IP: " + userCharacter.getShow() + "\n\n" + userCharacter.getDescription());
                setImageOutPut(userCharacter.getCharacterImage().getImageUrl());
            } else {
                setOutPut("FUCKIN YIKES FAM, SYSTEM BROKE, CONTACT ALEC");
            }
        }
    }

    private enum Type {
        Alpha,
        Omega,
        Beta;

        public static Type getRandomType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    private void daily() {
        String function = "DAILY";
        Integer characterDustAmount = generateDailyCharacterAmount();
        //Give user a free character
        if (!databaseCommand.checkIfFunctionIsAvailable(function, user)) {
            setOutPut("Sorry, you have already done your daily, come back at 8PM EST");
        } else {
            if (databaseCommand.changeCharacterDustByUser(user, user.getCharacterDust() + characterDustAmount)) {
                databaseCommand.addFunctionToTimeTable(function, user);
                Integer newAmount = user.getCharacterDust() + characterDustAmount;
                setOutPut("Welcome back to Pride Rock. You have been awarded " + characterDustAmount + " weeb points! (Whatever that is.) Your new total is " + newAmount);
            } else {
                setOutPut("FUCKIN YIKES FAM, SYSTEM BROKE, CONTACT ALEC");
            }
        }
    }

    //TODO: CHECK IF WAIFU EXISTS
    private void favorite(Integer favoriteCharacterId) {
        if (!databaseCommand.checkIfCharacterExists(favoriteCharacterId)) {
            setOutPut("That character does not exist");
        } else if (databaseCommand.changeFavoriteCharacter(favoriteCharacterId, user)) {
            setOutPut("Your new favorite character has been updated");
        } else {
            setOutPut("ERROR: ALERT ALEC");
        }
    }

    private void profile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("**" + user.getUserName() + "**");
        stringBuilder.append("\n");
        stringBuilder.append("Total Owned: " + databaseCommand.getNumberOfCharactersFromUser(user));
        stringBuilder.append("\n");
        stringBuilder.append("Weeb points: " + user.getCharacterDust());
        stringBuilder.append("\n");
        UserCharacter character = databaseCommand.getCharacterById(user.getFavoriteCharacterId());
        stringBuilder.append("Favorite Character: " + character.getName());
        setOutPut(stringBuilder.toString());
        if (character.getCharacterImage() != null) {
            setImageOutPut(character.getCharacterImage().getImageUrl());
        }
    }

    public Integer generateDailyCharacterAmount() {
        Random random = new Random();
        int max = 20;
        int min = 10;

        return random.nextInt(max) + min;
    }

    private void highest() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("We are currently sifting through ALL of myshowlist. The highest characterId is " + databaseCommand.checkHighestCharacterId());
        setOutPut(stringBuilder.toString());
    }

    private void help() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Here are a list of commands:");
        stringBuilder.append("\n\n");
        stringBuilder.append("Everything must be prefaced with with dis.");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.groll uses your weebs points to get a new character.");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.profile will show you your profile information.");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.favorite [GLOBAL WAIFU ID] will add a character to your profile. EG: 'd.favorite 1' That will save Spike as your favorite");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.dailygacha will get you a new character everyday. The daily timer resets at 8PM EST.");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.view [LOCAL WAIFU ID] will view your character. EG: 'd.view 1' will view your first character");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.speak is for my master to use. It may not be very helpful.");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.delete [LOCAL WAIFU ID] will remove that character from your list. Use this at your own risk");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.sc [ANIME NAME] will return every character in that show");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.character/id [GLOBAL WAIFU ID] will return information about a given character ID");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.info [CHARACTER NAME] will return a character of a given name. \"dis.name like [CHARACTER NAME]\" will return a guess. The beginning will need to be correct, but anything after the given name will be guessed upon.");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.claim [CHARACTER NAME] will claim a random character that has appeared in the channel");
        stringBuilder.append("\n\n");
        stringBuilder.append("dis.help... hey wait a minute!");
        setOutPut(stringBuilder.toString());
    }

    private void delete(Integer localId) {
        Integer additionalCharacterDust = user.getCharacterDust() + 10;

        if (databaseCommand.deleteCharacterByUserAndLocalId(user, localId, additionalCharacterDust)) {
            setOutPut("I will call upon the Hyenas to take care of that fiend. They will no longer be an issue in the Pride Lands. I have restored 10 additional character dust upon your name. Use Wisely.");
        } else {
            setOutPut("I had Zazu fly out and inspect the Pride Lands where he failed to locate that individual. Either that bird is slacking or they are long gone.");
        }
    }

    private void sc(String[] function) {
        StringBuilder show = new StringBuilder();
        for (int i = 1; i < function.length; i++) {
            show.append(function[i]);
            show.append(" ");
        }
        List<Character> characters = databaseCommand.getCharactersByShow(show.substring(0, show.length() - 1));
        if (characters.size() == maxOut) {
            String query = "SELECT w.name, w.show, w.id FROM disneydatabase.character w WHERE w.show LIKE '%" + show.substring(0, show.length() - 1) + "%' ORDER BY w.id";
            setQuery(query);
        }
        if (characters.isEmpty()) {
            setOutPut("Sorry, but no characters were found for that show");
        } else {
            //Create map with the show name as the key, and a list of the characters as the value
            Map<String, List<Character>> characterFromShowMap = new HashMap<>();
            characters.forEach(character -> {
                characterFromShowMap.computeIfAbsent(character.getShow(), k -> new ArrayList<>()).add(character);
            });
            StringBuilder out = new StringBuilder();
            out.append("Here are the characters for that show: \n");
            //for each show, loop through each character
            characterFromShowMap.forEach((showName, characterList) -> {
                out.append("**" + showName + "**");
                out.append("\n");
                characterList.forEach(character -> out.append(character.getId() + " - " + character.getName() + "\n"));
                out.append("\n\n");
            });
            setOutPut(out.toString());
        }
    }

    private void character(Integer globalId) {
        if (!databaseCommand.checkIfCharacterExists(globalId)) {
            setOutPut("That is not a valid global character ID, or that Character currently has no photos");
        } else {
            UserCharacter character = databaseCommand.getCharacterById(globalId);
            StringBuilder out = new StringBuilder();

            if (character.getCharacterImage().getImageUrl() != null) {
                out.append(character.getId() + " - " + "**" + character.getName() + "**");
                out.append("\n\n");
                out.append("Disney IP: " + character.getShow());
                out.append("\n\n");
                out.append(character.getDescription());
                out.append("\n\n");
                setImageOutPut(character.getCharacterImage().getImageUrl());
                setOutPut(out.toString());
            } else {
                setOutPut("Apologies, but that Character currently has no images.\n Alec has discovered an issue with the grabber.\n Many characters are missing images (roughly 8000). Once the 200,000 character is added, Alec will go back and requery those poor souls.");
            }
        }
    }

    //TODO: LIST MULTIPLE WAIFUS EG: d.info john 3
    private void info(String[] characterName) {
        boolean like = false;
        if (characterName[1].equalsIgnoreCase("LIKE")) {
            like = true;
            characterName = Arrays.copyOfRange(characterName, 1, characterName.length);
        }
        StringBuilder name = new StringBuilder();
        for (int i = 1; i < characterName.length; i++) {
            name.append(characterName[i]);
            name.append(" ");
        }
        UserCharacter character = null;
        if (like) {
            character = databaseCommand.getCharacterByLikeName(name.substring(0, name.length() - 1));
        } else {
            character = databaseCommand.getCharacterByName(name.substring(0, name.length() - 1));
        }
        StringBuilder out = new StringBuilder();

        if (character.getId() != null) {
            out.append(character.getId() + " - " + Utils.bold(character.getName()));
            out.append("\n\n");
            out.append("Disney IP: " + character.getShow());
            out.append("\n\n");
            out.append(character.getDescription());
            out.append("\n\n");
            setImageOutPut(character.getCharacterImage().getImageUrl());
            setOutPut(out.toString());
        } else {
            setOutPut("That is not a valid global character Name, this name must be very specific. It is possible for multiple characters to have the same name");
        }
    }
}