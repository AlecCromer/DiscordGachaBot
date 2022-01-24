package MessageProcess;

public class Character {


    Integer id;

    String name;

    String description;

    String show;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Character (){

    }

    public Character(Integer id, String name, String description, String show) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.show = show;
    }

    @Deprecated
    public Character (Integer characterId, String characterName){

        this.id = characterId;
        this.name = characterName;
    }
}
