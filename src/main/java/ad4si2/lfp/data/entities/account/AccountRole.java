package ad4si2.lfp.data.entities.account;

public enum AccountRole {

    ADMIN("Администратор"), PLAYER("Игрок");

    private String title;

    AccountRole(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
