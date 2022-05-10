package com.example;

import java.util.ArrayList;
import java.util.List;

public class Constants {
//    public static String url = "jdbc:postgresql://localhost/postgres";
//    public static String user = "postgres";
//    public static String password = "root";

    public static String url = "jdbc:postgresql://ec2-54-247-96-153.eu-west-1.compute.amazonaws.com/d2a08q02s3aa8h?user=drchqhkktdpmjl&password=0c370cd21c839d31d2455acccff0960767e9d4bf321448a58e5a47888a15181d&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    public static String user = "drchqhkktdpmjl";
    public static String password = "0c370cd21c839d31d2455acccff0960767e9d4bf321448a58e5a47888a15181d";

    public static String mainDataBaseName = "jc_contact";
    public static String temporaryDataBaseName = "MyTemporary";
    public static String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";
    public static List<List<Integer>> presetAndDomenHistory = new ArrayList<>();


}
