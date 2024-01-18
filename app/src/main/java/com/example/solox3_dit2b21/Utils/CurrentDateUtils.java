package com.example.solox3_dit2b21.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentDateUtils {
    public static String getCurrentDateTime() {
        // Get the current date and time
        Date currentDate = new Date();

        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        // Format the date as a string
        return sdf.format(currentDate);
    }
}
