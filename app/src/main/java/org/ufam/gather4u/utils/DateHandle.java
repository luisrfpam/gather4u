package org.ufam.gather4u.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHandle {

    private static String TAG = DateHandle.class.getSimpleName();

    public static String prettyDate(String dateStr){

        String timestamp = "";

        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS", Locale.getDefault());
//            sdf1.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().toString()));
//            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date theDate = sdf1.parse(dateStr);
            Timestamp ts = new Timestamp(theDate.getTime());
            timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(ts);
            timestamp = timestamp.replace(" "," às ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public String today(){
        Date date = new Date(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return year+"-"+month+"-"+day+"";
    }

    public static String getDate() {
        Date utilDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        cal.set(Calendar.MILLISECOND, 0);
        return new Timestamp(utilDate.getTime()).toString();
    }

    public static String getDateBR() {

        return getDateBR( getDate() );
    }

    public static String getDateES(String value){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date utilDate = sdf.parse(value);
            Calendar cal = Calendar.getInstance();
            cal.setTime(utilDate);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            return format1.format(cal.getTime());
        }
        catch (Exception ex){
        }
        return "";

    }

    public static String getDateBR(String value){
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Date utilDate = format1.parse(value);
            Calendar cal = Calendar.getInstance();
            cal.setTime(utilDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(cal.getTime());
        }
        catch (Exception ex){
        }
        return "";

    }

    public static boolean checkTimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkDate(String date, String enddate) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = sdf.parse(date);
            Date date2 = sdf.parse(enddate);

            if(date1.before(date2)) {
                return true;
            } else if(date1.equals(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

//    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
//        put("^\\d{8}$", "yyyyMMdd");
//        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
//        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
//        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
//        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
//        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
//        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
//        put("^\\d{12}$", "yyyyMMddHHmm");
//        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
//        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
//        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
//        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
//        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
//        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
//        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
//        put("^\\d{14}$", "yyyyMMddHHmmss");
//        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
//        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
//        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
//        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
//        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
//        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
//        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
//    }};



//    /**
//     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
//     * format is unknown. You can simply extend DateUtil with more formats if needed.
//     * @param dateString The date string to determine the SimpleDateFormat pattern for.
//     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
//     * @see SimpleDateFormat
//     */
//    public static String determineDateFormat(String dateString) {
//        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
//            if (dateString.toLowerCase().matches(regexp)) {
//                return DATE_FORMAT_REGEXPS.get(regexp);
//            }
//        }
//        return "indeterminado"; // Unknown format.
//    }


//    private static final String[] formats = {
//            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ",
//            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
//            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
//            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
//            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
//            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss",
//            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd", };


//    public static void parse(String d) {
//        if (d != null) {
//            for (String parse : formats) {
//                SimpleDateFormat sdf = new SimpleDateFormat(parse);
//                try {
//                    sdf.parse(d);
//                   MyLog.w(TAG,"Printing the value of: "+parse);
////                    System.out.println("Printing the value of " + parse);
//                } catch (ParseException e) {
//
//                }
//            }
//        }
//    }

//    public boolean checkStoreIsOpen(Store store){
//        MyLog.w(TAG,"checkStoreIsOpen");
//        boolean isOpen;
//        if(store.is24()){
//            isOpen = true;
//            MyLog.w(TAG,"open is 24");
//        }else{
//            SimpleDateFormat formatador = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            try {
//                Date now = formatador.parse(formatador.format(new Date()));
//                Date dMin;
//                Date dMax;
//                try{
//                    dMin = formatador.parse(store.getOpenHour());
//                    dMax = formatador.parse(store.getCloseHour());
//                }catch (Exception e){
//                    e.printStackTrace();
//                    dMin = formatador.parse("7:00");
//                    dMax = formatador.parse("18:00");
//                }
//
////                MyLog.e(TAG,"now date: "+now.toString());
////                MyLog.e(TAG,"min date: "+dMin.toString());
////                MyLog.e(TAG,"max date: "+dMax.toString());
////                MyLog.e(TAG,"ini: "+store.getOpenHour());
////                MyLog.e(TAG,"fim: "+store.getCloseHour());
////                MyLog.e(TAG,"min: "+now.after(dMin));
////                MyLog.e(TAG,"max: "+now.before(dMax));
//
//                if(dMin.before(dMax)){
//                    MyLog.w(TAG,"dMin.before(dMax)");
//                    if(now.after(dMin) && now.before(dMax)){
//                        MyLog.w(TAG,"open");
//                        isOpen = true;
//                    }else{
//                        MyLog.w(TAG,"close");
//                        isOpen = false;
//                    }
//                }else{
//                    MyLog.w(TAG,"dMax.before(dMin)");
//                    if(now.before(dMax) || now.after(dMin)){
//                        MyLog.w(TAG,"open");
//                        isOpen = true;
//                    }else{
//                        MyLog.w(TAG,"close");
//                        isOpen = false;
//                    }
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//                isOpen = true;
//            }
//        }
//        return isOpen;
//    }

//    public int getDayOfWeek(Date date){
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        return cal.get(Calendar.DAY_OF_WEEK);
//    }

//    public String timeAdjust(String sourceDateString){
//        return this.timeAdjust(sourceDateString,null);
//    }
//
//    public String timeAdjust(String sourceDateString, String formatModel){
//        String resultDateString = null;
//        try {
//            // TODO: 07/12/16 remover esse trycatch qndo o servidor estiver operando em long
//            //desconto de minutos para hora em long
//            Date date = new Date(Long.parseLong(sourceDateString));
//            long local = new Date().getTime();
//            long msg = date.getTime();
//            long result = local - msg;
//            if(result < 0){
//                msg = msg + result;
//            }else{
//                msg = msg - result;
//            }
//            Date finalDate = new Date(msg);
//
//            if(formatModel!=null){
//                resultDateString = new SimpleDateFormat(formatModel, Locale.getDefault()).format(finalDate);
//            }else{
//                resultDateString = String.valueOf(finalDate.getTime());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            //converte a hora zuada do servidor numa hora correta
//            //cai nesse catch quando a hora vem no formato antigo e da erro no try
//            try{
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                format.setTimeZone(TimeZone.getTimeZone("GMT"));
//
//                Date date = format.parse(sourceDateString);
//                long local = new Date().getTime();
//                long msg = date.getTime();
//                long result = local - msg;
//                if(result < 0){
//                    msg = msg + result;
//                }else{
//                    msg = msg - result;
//                }
//                Date finalDate = new Date(msg);
//
//                if(formatModel!=null){
//                    resultDateString = new SimpleDateFormat(formatModel, Locale.getDefault()).format(finalDate);
//                }else{
//                    resultDateString = String.valueOf(finalDate.getTime());
//                }
////                resultDateString = new SimpleDateFormat(formatModel, Locale.getDefault()).format(finalDate);
////                resultDateString = String.valueOf(finalDate.getTime());
//            }catch (ParseException pe){
//                pe.printStackTrace();
//            }
//        }
//
//        return resultDateString;
//    }
}
