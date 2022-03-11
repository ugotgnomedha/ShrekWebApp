package com.example;

import java.util.ArrayList;
import java.util.List;

public class Constants {
//    public static String url = "jdbc:postgresql://localhost/postgres";
//    public static String user = "postgres";
//    public static String password = "root";

    public static String url = "jdbc:postgresql://localhost/postgres";
    public static String user = "postgres";
    public static String password = "gagarin11";

    public static String mainDataBaseName = "jc_contact";
    public static String temporaryDataBaseName = "MyTemporary";
    public static String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";
    public static List<List<Integer>> presetAndDomenHistory = new ArrayList<>();



}
