package MessageProcess;

public class UserCharacter extends Character{

    Integer localId;

    CharacterImage characterImage;

    Integer affection;

    String type;


    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }


    public Integer getAffection() {
        return affection;
    }

    public void setAffection(Integer affection) {
        this.affection = affection;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CharacterImage getCharacterImage() {
        return characterImage;
    }

    public void setCharacterImage(CharacterImage characterImage) {
        this.characterImage = characterImage;
    }

    public UserCharacter(Integer id, String name, String description, String show, Integer localId, CharacterImage characterImage, Integer affection, String type) {
        super(id, name, description, show);
        this.localId = localId;
        this.characterImage = characterImage;
        this.affection = affection;
        this.type = type;
    }

    public UserCharacter(Integer id, String name, String description, String show, CharacterImage characterImage) {
        super(id, name, description, show);
        this.characterImage = characterImage;
    }

    public UserCharacter(){

    }

}
