package com.example.solox3_dit2b21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // Function to format date string
    public static String formatDateString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Return original string in case of error
        }
    }



}

