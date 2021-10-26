package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {
    public static final String url = "jdbc:postgresql://localhost/postgres";
    public static final String user = "postgres";
    public static final String password = "root";
    public static final String spec_sym = "$aesc6$";
    public static final String mainDataBaseName = "jc_contact";
    public static final String temporaryDataBaseName = "MyTemporary";
    public static final String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";
    public static List<List<Integer>> presetAndDomenHistory = new ArrayList<>();

}
