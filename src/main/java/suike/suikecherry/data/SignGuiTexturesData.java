package suike.suikecherry.data;

public class SignGuiTexturesData{
    private final String type;
    private final String textures;

    public SignGuiTexturesData(String type, String textures) {
        this.type = type;
        this.textures = textures;
    }

    public String getType() {
        return this.type;
    }

    public String getTextures() {
        return this.textures;
    }

    public String toString() {
        return String.format("方块类型{type='%s', gui名='%s'}", this.type, this.textures);
    }
}