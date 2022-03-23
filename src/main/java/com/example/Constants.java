package com.example;

import java.util.ArrayList;
import java.util.List;

public class Constants {
//    public static String url = "jdbc:postgresql://localhost/postgres";
//    public static String user = "postgres";
//    public static String password = "root";

    public static String url = "jdbc:postgresql://ec2-34-253-29-48.eu-west-1.compute.amazonaws.com/dfumj4fkvmbc4r?user=fiykczfrcmpymp&password=2a2a2012e8ededb59604977d4bf427dd80c9984549dbb78f6585b5232890da10&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    public static String user = "fiykczfrcmpymp";
    public static String password = "2a2a2012e8ededb59604977d4bf427dd80c9984549dbb78f6585b5232890da10";

    public static String mainDataBaseName = "jc_contact";
    public static String temporaryDataBaseName = "MyTemporary";
    public static String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";
    public static List<List<Integer>> presetAndDomenHistory = new ArrayList<>();



}
