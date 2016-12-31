package ad4si2.lfp.web.menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    @Nonnull
    private String name;

    @Nonnull
    private String url;

    @Nullable
    private String icon;

    @Nonnull
    private List<MenuItem> subMenu = new ArrayList<>();

    public MenuItem(@Nonnull final String name, @Nonnull final String url, @Nullable final String icon) {
        this.name = name;
        this.url = url;
        this.icon = icon;
    }

    public MenuItem(@Nonnull final String name, @Nonnull final String url, @Nullable final String icon, @Nonnull final List<MenuItem> subMenu) {
        this.name = name;
        this.url = url;
        this.icon = icon;
        this.subMenu = subMenu;
    }

    @Override
    public String toString() {
        return "MenuItem {" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", icon='" + icon + '\'' +
                ", subMenu=" + subMenu +
                '}';
    }
}
