package MessageProcess;

import java.util.List;


public class User {

    public User(String id, String userName, List<Character> characters, Integer characterDust, Integer favoriteCharacterId) {
        this.id = id;
        this.userName = userName;
        this.characters = characters;
        this.characterDust = characterDust;
        this.favoriteCharacterId = favoriteCharacterId;

    }

    private String id;

    private String userName;

    private List<Character> characters;

    private Integer characterDust;

    private Integer favoriteCharacterId;

    public Integer getFavoriteCharacterId() {
        return favoriteCharacterId;
    }

    public void setFavoriteCharacterId(Integer favoriteCharacterId) {
        this.favoriteCharacterId = favoriteCharacterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public Integer getCharacterDust() {
        return characterDust;
    }

    public void setCharacterDust(Integer characterDust) {
        this.characterDust = characterDust;
    }
}
