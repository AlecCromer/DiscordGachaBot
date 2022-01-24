package Database;

import MessageProcess.Character;
import MessageProcess.*;
import com.mysql.cj.jdbc.ClientPreparedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCommand {


    public Integer addUserToDatabase(User user) {
        int affectedRow = 0;
        String query = "insert IGNORE into user" + "(id, user_name, character_dust)"
                + "values(?,?,?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(user.getId()));
            sqlStatement.setString(2, user.getUserName());
            sqlStatement.setInt(3, user.getCharacterDust());

            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow;

    }

    public boolean userExists(String id) {
        String query = "SELECT * from user where id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(id));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return false;
    }

    public User getUser(String id) {
        User user = new User(null, null, null, null, null);
        String query = "SELECT * from user where id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(id));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                user.setId(rs.getString(1));
                user.setUserName(rs.getString(2));
                user.setCharacterDust(rs.getInt(3));
                user.setFavoriteCharacterId(rs.getInt(4));
            }
            //user.setCharacters(getUserCharacterList(id));
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return user;
    }

    public List<Character> getUserCharacterList(QueryBuilder queryBuilder, String id, int maxOut, int offset) {
        List<Character> characterList = new ArrayList<>();

        String query = "SELECT w.id, uw.local_id, w.name FROM disneydatabase.user_character uw JOIN disneydatabase.character w ON uw.character_id = w.id WHERE uw.user_id = ? LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(id));
            sqlStatement.setInt(2, maxOut);
            sqlStatement.setInt(3, offset);

            queryBuilder.setQuery(((ClientPreparedStatement) sqlStatement).asSql());
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                Character character = new Character();
                character.setId(rs.getInt(2));
                character.setName(rs.getString(3));
                characterList.add(character);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return characterList;
    }

    public UserCharacter getRandomCharacter() {
        Integer id = null;
        String name = null;
        String description = null;
        String show = null;
        Integer image_id = null;
        String image_url = null;
        String query = "SELECT DISTINCT w.id, w.name, w.description, w.show, wi.id AS image_id, wi.image_url FROM character w INNER JOIN character_images wi ON w.id = wi.character_id ORDER BY RAND() LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                description = rs.getString(3);
                show = rs.getString(4);
                image_id = rs.getInt(5);
                image_url = rs.getString(6);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return new UserCharacter(id, name, description, show, new CharacterImage(image_id, image_url));
    }

    //Use to insert the next highest value for user
    public Integer getHighestLocalIdByUserId(String userId) {
        Integer highestValue = 0;
        String query = "SELECT uw.local_id FROM disneydatabase.user_character uw WHERE uw.user_id = ? ORDER BY local_id  DESC LIMIT 0, 1 ";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(userId));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                highestValue = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return highestValue;
    }

    public UserCharacter getCharacterById(Integer id) {
        String name = null;
        String description = null;
        String show = null;
        String characterImageUrl = null;
        String query = "SELECT w.id, w.name, w.description, w.show, wi.image_url FROM disneydatabase.character w, disneydatabase.character_images wi WHERE w.id = ? AND w.id = wi.character_id LIMIT 1;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(id));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                name = rs.getString(2);
                description = rs.getString(3);
                show = rs.getString(4);
                characterImageUrl = rs.getString(5);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        CharacterImage characterImage = new CharacterImage(characterImageUrl);
        return new UserCharacter(id, name, description, show, characterImage);
    }

    public boolean addCharacterToUser(User user, UserCharacter character) {
        int affectedRow = 0;
        String query = "insert IGNORE into user_character" + "(user_id, character_id, local_id, image_id, affection, type)"
                + "values(?, ?, ?, ?, ?, ?)";
        Integer newLocalId = getHighestLocalIdByUserId(user.getId()) + 1;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(user.getId()));
            sqlStatement.setString(2, String.valueOf(character.getId()));
            sqlStatement.setString(3, String.valueOf(newLocalId));
            sqlStatement.setString(4, String.valueOf(character.getCharacterImage().getId()));
            sqlStatement.setString(5, String.valueOf(character.getAffection()));
            sqlStatement.setString(6, String.valueOf(character.getType()));


            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow > 0;
    }

    public UserCharacter getCharacterByUserAndLocalId(User user, Integer id) {
        UserCharacter userCharacter = new UserCharacter();
        String query = "SELECT w.id, w.name, w.show, uw.local_id, uw.affection, uw.type, wi.image_url, w.description \n" +
                "\tFROM disneydatabase.user_character uw\n" +
                "\t\tJOIN disneydatabase.character w\n" +
                "\t\t\tON uw.character_id = w.id\n" +
                "\t\tJOIN disneydatabase.character_images wi\n" +
                "\t\t\tON wi.character_id = w.id AND wi.id = uw.image_id\n" +
                "\t\tJOIN disneydatabase.user u\n" +
                "\t\t\tON uw.user_id = u.id\n" +
                "\t\t\n" +
                "    WHERE uw.user_id = ? AND uw.local_id = ?;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(user.getId()));
            sqlStatement.setString(2, String.valueOf(id));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                userCharacter.setId(rs.getInt(1));
                userCharacter.setName(rs.getString(2));
                userCharacter.setShow(rs.getString(3));
                userCharacter.setLocalId(rs.getInt(4));
                userCharacter.setAffection(rs.getInt(5));
                userCharacter.setType(rs.getString(6));
                userCharacter.setCharacterImage(new CharacterImage(rs.getString(7)));
                userCharacter.setDescription(rs.getString(8));
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return userCharacter;
    }


    public boolean checkIfFunctionIsAvailable(String function, User user) {
        String query = "SELECT * from time_table where function = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(function));
            sqlStatement.setString(2, String.valueOf(user.getId()));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return true;
    }

    public boolean addFunctionToTimeTable(String function, User user) {
        int affectedRow = 0;
        String query = "insert into time_table" + "(user_id, function, last_run)"
                + "values(?, ?, null)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(user.getId()));
            sqlStatement.setString(2, String.valueOf(function));

            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow > 0;
    }

    public boolean changeCharacterDustByUser(User user, Integer newCharacterDust) {
        String query = "UPDATE user SET character_dust = ? WHERE id = ?";
        Integer affectedRow = 0;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(newCharacterDust));
            sqlStatement.setString(2, String.valueOf(user.getId()));

            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow > 0;
    }

    public boolean changeFavoriteCharacter(Integer favoriteCharacterId, User user) {
        String query = "UPDATE user SET favorite_character_id = ? WHERE id = ?";
        Integer affectedRow = 0;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(favoriteCharacterId));
            sqlStatement.setString(2, String.valueOf(user.getId()));

            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow > 0;
    }

    public boolean checkIfCharacterExists(Integer favoriteCharacterId) {
        String query = "SELECT * from character where id = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(favoriteCharacterId));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return false;
    }

    public Integer checkHighestCharacterId() {
        String query = "SELECT MAX(character.id) FROM disneydatabase.character;";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return 0;
    }

    public boolean deleteCharacterByUserAndLocalId(User user, Integer id, Integer additionalCharacterDust) {
        changeCharacterDustByUser(user, additionalCharacterDust);

        String query = "DELETE FROM user_character where user_id = ? AND local_id = ? LIMIT 1;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(user.getId()));
            sqlStatement.setString(2, String.valueOf(id));

            // get the number of return rows
            int result = sqlStatement.executeUpdate();
            if (result > 0) {
                return updateCharacterListAfterDelete(user, id);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return false;
    }

    public boolean updateCharacterListAfterDelete(User user, Integer id) {

        String query = "UPDATE user_character uw SET uw.local_id = uw.local_id-1 WHERE uw.local_id > ? AND uw.user_id = ?;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(id));
            sqlStatement.setString(2, String.valueOf(user.getId()));

            // get the number of return rows
            int result = sqlStatement.executeUpdate();
            if (result > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return true;
    }

    public List<Character> getCharactersByShow(String show) {
        List<Character> characters = new ArrayList<>();
        String query = "SELECT w.name, w.show, w.id FROM disneydatabase.character w WHERE w.show LIKE ? ORDER BY w.id LIMIT 20 OFFSET 0";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, "%" + show + "%");

            // get the number of return rows
            ResultSet result = sqlStatement.executeQuery();
            while (result.next()) {
                Character character = new Character();
                character.setName(result.getString(1));
                character.setShow(result.getString(2));
                character.setId(result.getInt(3));
                characters.add(character);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return characters;

    }

    public UserCharacter getCharacterByName(String characterName) {
        Integer id = null;
        String name = null;
        String description = null;
        String show = null;
        String characterImageUrl = null;
        String query = "SELECT w.id, w.name, w.description, w.show, wi.image_url FROM disneydatabase.character w, disneydatabase.character_images wi WHERE w.name = ? AND w.id = wi.character_id LIMIT 1;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, characterName);

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                description = rs.getString(3);
                show = rs.getString(4);
                characterImageUrl = rs.getString(5);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        CharacterImage characterImage = new CharacterImage(characterImageUrl);
        return new UserCharacter(id, name, description, show, characterImage);
    }

    public UserCharacter getCharacterByLikeName(String characterName) {
        Integer id = null;
        String name = null;
        String description = null;
        String show = null;
        String characterImageUrl = null;
        String query = "SELECT w.id, w.name, w.description, w.show, wi.image_url FROM disneydatabase.character w, disneydatabase.character_images wi WHERE w.name like ? AND w.id = wi.character_id LIMIT 1;";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, "%" + characterName + "%");

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                description = rs.getString(3);
                show = rs.getString(4);
                characterImageUrl = rs.getString(5);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        CharacterImage characterImage = new CharacterImage(characterImageUrl);
        return new UserCharacter(id, name, description, show, characterImage);
    }

    public boolean setClaimChannelCharacter(String channelId, Integer characterId, Integer imageId) {
        int affectedRow = 0;
        String query = "REPLACE into channel_character" + "(channel_id, character_id, image_id)"
                + "values(?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, channelId);
            sqlStatement.setInt(2, characterId);
            sqlStatement.setInt(3, imageId);

            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow > 0;
    }

    public UserCharacter getClaimCharacter(String nameGuess, String channelId) {
        Integer id = null;
        String name = null;
        String description = null;
        String show = null;
        Integer image_id = null;
        String image_url = null;
        String query = "SELECT DISTINCT w.id, w.name, w.description, w.show, wi.id AS image_id, wi.image_url FROM character w \n" +
                "INNER JOIN character_images wi ON w.id = wi.character_id \n" +
                "INNER JOIN channel_character cw ON w.id = cw.character_id && cw.image_id = wi.id\n" +
                "WHERE w.name = ? AND channel_id = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

            sqlStatement.setString(1, nameGuess);
            sqlStatement.setString(2, channelId);

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                description = rs.getString(3);
                show = rs.getString(4);
                image_id = rs.getInt(5);
                image_url = rs.getString(6);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return new UserCharacter(id, name, description, show, new CharacterImage(image_id, image_url));
    }

    public boolean removeClaimChannelCharacter(String channelId) {
        String query = "DELETE FROM channel_character where channel_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, channelId);

            // get the number of return rows
            int result = sqlStatement.executeUpdate();
            if (result > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return false;
    }

    public Integer getNumberOfCharactersFromUser(User user) {
        Integer count = 0;
        String query = "SELECT COUNT(*) FROM disneydatabase.user_character WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

            sqlStatement.setString(1, user.getId());

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return count;
    }

    public boolean addMessageSplit(Long messageId, String messageQuery) {
        int affectedRow = 0;
        String query = "insert into message_split" + "(message_id, query, pageNum)"
                + "values(?, ?, 0)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(messageId));
            sqlStatement.setString(2, String.valueOf(messageQuery));

            // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);

        }
        return affectedRow > 0;
    }

    public boolean changeMessageSplitPageNumber(String direction, Long messageId) {
        String query = "";
        if (direction.equalsIgnoreCase("INCREASE")) {
            query = "UPDATE message_split SET pageNum = pageNum + 1 WHERE message_id = ?";

        } else if (direction.equalsIgnoreCase("DECREASE")) {
            query = "UPDATE message_split SET pageNum = pageNum - 1 WHERE message_id = ? AND pageNum >= 1";
        }
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, String.valueOf(messageId));

            // get the number of return rows
            int result = sqlStatement.executeUpdate();
            if (result > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return true;
    }

    //String[0] = query
    //String[1] = page
    public String[] getQueryFromSplitPageNumber(Long messageId) {
        String outputquery[] = new String[2];
        String query = "SELECT query, pageNum FROM disneydatabase.message_split WHERE message_id = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

            sqlStatement.setString(1, String.valueOf(messageId));

            // get the number of return rows
            ResultSet rs = sqlStatement.executeQuery();
            while (rs.next()) {
                outputquery[0] = rs.getString(1);
                outputquery[1] = rs.getString(2);
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return outputquery;
    }

    public List<Character> getCharactersFromQuery(String query) {
        {
            List<Character> characterList = new ArrayList<>();

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

                ResultSet rs = sqlStatement.executeQuery();
                while (rs.next()) {
                    Character character = new Character();
                    if (0 != rs.getInt("id")) {
                        character.setId(rs.getInt("id"));
                    }
                    if (0 != rs.getInt("local_id")) {
                        character.setId(rs.getInt("local_id"));
                    }
                    character.setName(rs.getString("name"));
                    characterList.add(character);
                }
            } catch (Exception e) {
                System.out.println("Status: operation failed due to " + e);
            }
            return characterList;
        }
    }

    public int replace(String movie) {
        String query = "UPDATE character SET show = ? WHERE description like ? AND show = 'Disney'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            sqlStatement.setString(1, movie);
            sqlStatement.setString(2, "%" + movie + "%");

            // get the number of return rows
            int result = sqlStatement.executeUpdate();
            if (result > 0) {
                return result;
            }
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return 0;
    }
}
