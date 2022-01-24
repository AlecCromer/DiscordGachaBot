package MessageProcess;

public class CharacterImage {

    Integer id;

    Integer characterId;

    String imageUrl;

    Integer imageNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(Integer imageNumber) {
        this.imageNumber = imageNumber;
    }

    public CharacterImage(Integer id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public CharacterImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
