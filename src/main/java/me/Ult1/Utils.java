package me.Ult1;

import java.util.Arrays;

public class Utils {
    public static String limit(String str, int size){
        if(str.length() == size) return str;
        if(str.length() >  size)
            return str.substring(0, size - 3) + "...";

        StringBuilder a = new StringBuilder(str);
        int missing = size - a.length();

        char[] spaces = new char[missing];
        Arrays.fill(spaces, ' ');
        a.append(spaces);

        return a.toString();

    }

}
