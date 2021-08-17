package be.kuleuven.elcontador10.background.model;

public class EmojiCategory {
    String icon;
    String title;
    String id;

    public EmojiCategory() {
    }

    public EmojiCategory(String icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
