package com.pcstore.model;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Model_Menu {
    String icon;
    String name;
    MenuType type;

    public static enum MenuType{
        TITLE, MENU, EMPTY
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public Model_Menu(String icon, String name, MenuType type) {
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public Model_Menu() {
    }

    public Icon toIcon(){
        return new ImageIcon(getClass().getResource("/com/pcstore/resources/" + icon + ".png"));
    }

    
    


}
