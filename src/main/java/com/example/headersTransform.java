package com.example;

public class headersTransform {
    public static String TranslateNameToDB(String header) {
        header = header.replace(" ", "_");
        header = header.replace("(", "");
        header = header.replace(")", "");
        header = header.replace(".", "");
        header = header.replace("/", "_");
        header = header.replace("-", "_");
        header = header.replace("&", "_and_");
        header = header.replace("%", "percent");
        header = header.replace(",", "");
        header = header.replace("1", "one_");
        header = header.replace("2", "two_");
        header = header.replace("3", "three_");
        header = header.replace("4", "four_");
        header = header.replace("5", "five_");
        header = header.replace("6", "six_");
        header = header.replace("7", "seven_");
        header = header.replace("8", "eight_");
        header = header.replace("9", "nine_");
        return header;
    }
}

