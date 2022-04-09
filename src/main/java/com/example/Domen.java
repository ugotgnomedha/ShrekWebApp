package com.example;

public class Domen {

    private String domenName;
    private Integer domenCount;

    public Domen(String domenName, Integer domenCount) {
        this.domenName = domenName;
        this.domenCount = domenCount;
    }

    public String getDomenName() {
        return domenName;
    }

    public Integer getDomenCount() {
        return domenCount;
    }
}
