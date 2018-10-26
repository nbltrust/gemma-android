package com.cybex.componentservice.bean;

public class TokenBean {

    private String name;

    private String iconUrl="http://img3.imgtn.bdimg.com/it/u=3853852840,331334549&fm=26&gp=0.jpg";

    public TokenBean(String name, String iconUrl) {
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public TokenBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
