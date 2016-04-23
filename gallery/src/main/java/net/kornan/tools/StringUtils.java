package net.kornan.tools;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author: kornan
 * @date: 2016-03-09 20:01
 */
public class StringUtils {

    public static String format(Date date, String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyyMMddHHmmss";
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}
