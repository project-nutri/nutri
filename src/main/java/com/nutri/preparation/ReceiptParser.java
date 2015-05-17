package com.nutri.preparation;

import com.nutri.preparation.utils.FileUtils;
import com.nutri.preparation.utils.ParserUtils;
import sun.net.www.ParseUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 29.09.14
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class ReceiptParser {
    public static void main(String[] args) {
        String filePath = "src/main/resources/tryRec.txt";
        StringBuilder fileString = FileUtils.readFileToString(filePath);
        parseReciept(fileString.toString());
    }

    public static List<String> parseReciept(String fileString) {
        List<String> itemList;
        String cleanRow = deleteBracesValues(fileString.trim()).trim();

        cleanRow = changeComaInNumbers(cleanRow);
        cleanRow = selectNumberInTwo(cleanRow);
        cleanRow = parseFraction(cleanRow);
        cleanRow = cleanAllStuff(cleanRow);
        itemList = ParserUtils.tokenizeStringToList(cleanRow, " ");
        replaceMeasures(itemList);
//        System.out.println(itemList);
        replaceProducts(itemList);
//        System.out.println(itemList);
        //Map<String, EntityClass> classified = new HashMap<String, EntityClass>();
        //By conviension 0 - name, 1 - number, 2 - measure
        List<String> result = Arrays.asList("","","");
        for (String item : itemList) {
            if (isDigit(item)) {
                //      classified.put(item,EntityClass.QUANTITY);
//                System.out.println("number - " + item);
                result.set(1, item);
            } else if (ParserUtils.isMeasure(item)) {
                //    classified.put(item,EntityClass.MEASURE);
//                System.out.println("measure - " + item);
                result.set(2, ParserUtils.getMeasureValue(item));
            } else {
                result.set(0, item);
//                System.out.println("product - " + item);
               // findInDB(item);
            }
        }
        //in current realization return only last set - so now can be used only for single line each time
        return result;
    }

    private static void replaceProducts(List<String> list) {
        String product = "";
        List<String> stringsToRemove = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            int counter = 0;
            String foundMeasure = "";
            for (int j = 0; j < list.size(); j++) {
                if(i + j < list.size()&& !isDigit(list.get(j+i)) && !ParserUtils.isMeasure(list.get(j+i))){
                    product += list.get(i + j);
                    foundMeasure = product;
                    counter++;
                }else{
                 break;
                }
                product += " ";
                //j++;
            }
            product = "";
            if (!foundMeasure.equals("")) {
                list.set(i, foundMeasure);
                if (counter > 1) {
                    for (int h = i + 1; h < (i + counter) && h < list.size(); h++) {
                        stringsToRemove.add(list.get(h));
                    }
                    i+=counter;
                }
            }
        }

        list.removeAll(stringsToRemove);
    }


    private static boolean isDigit(String item) {
        try {
            Float.valueOf(item);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static String parseFraction(String cleanRow){
        String pattern = "(\\d+/\\d+)";
        Matcher matcher = Pattern.compile(pattern).matcher(cleanRow);
        StringBuilder buffer = new StringBuilder();
        int lastPosition = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            buffer.append(cleanRow.substring(lastPosition, start));
            String numberStr = cleanRow.substring(start, end);
            int delimPos = numberStr.indexOf('/');
            float first = Float.valueOf(numberStr.substring(0, delimPos));
            float last = Float.valueOf(numberStr.substring(delimPos+1,numberStr.length()));
            float res = first/last;
            buffer.append(res);
            lastPosition = end;
        }

        if (buffer.length() != 0) {
            return buffer.append(cleanRow.substring(lastPosition)).toString();
        } else {
            return cleanRow;
        }
    }

    private static String selectNumberInTwo(String cleanRow) {
        String pattern = "(\\d+\\s?[-]\\s?\\d+)";
        Matcher matcher = Pattern.compile(pattern).matcher(cleanRow);
        StringBuilder buffer = new StringBuilder();
        int lastPosition = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            buffer.append(cleanRow.substring(lastPosition, start));
            String numberStr = cleanRow.substring(start, end);
            buffer.append(numberStr.substring(0, numberStr.indexOf('-')));
            lastPosition = end;
        }

        if (buffer.length() != 0) {
            return buffer.append(cleanRow.substring(lastPosition)).toString();
        } else {
            return cleanRow;
        }
    }

    private static void replaceMeasures(List<String> list) {
        String measure = "";
        List<String> stringsToRemove = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            int counter = 0;
            String foundMeasure = "";
            for (int j = 0; j < ParserUtils.getMaxMeasureWords(); j++) {
                measure += (i + j < list.size()) ? list.get(i + j) : "";
                if (ParserUtils.isMeasure(measure)) {
                    foundMeasure = measure;
                    counter=j+1;
                }
                measure += " ";
            }
            measure = "";
            if (!foundMeasure.equals("")) {
                list.set(i, foundMeasure);
                if (counter > 1) {
                    for (int h = i + 1; h < (i + counter) && h < list.size(); h++) {
                        stringsToRemove.add(list.get(h));
                    }
                }
            }
        }

        list.removeAll(stringsToRemove);
    }

    private static String cleanAllStuff(String cleanRow) {
//        String pattern = "[\\W]";
        String pattern = "[-–:—;,!@#$%^&*()_+{}\\[\\]|<>]";
        String updated = cleanRow.replaceAll(pattern, "");
        pattern = "[\\p{Space}]";
        updated = updated.replaceAll(pattern, " ");
//        System.out.println("cleanAllStuff: " + updated);
        return updated;  //To change body of created methods use File | Settings | File Templates.
    }

    private static String changeComaInNumbers(String cleanRow) {
        String pattern = "(\\d+[,]\\s?\\d+)";
        Matcher matcher = Pattern.compile(pattern).matcher(cleanRow);
        StringBuilder buffer = new StringBuilder();
        int lastPosition = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            buffer.append(cleanRow.substring(lastPosition, start));
            String numberStr = cleanRow.substring(start, end);
            buffer.append(numberStr.replace(',', '.'));
            lastPosition = end;
        }
        if (buffer.length() != 0) {
            return buffer.append(cleanRow.substring(lastPosition)).toString();
        } else {
            return cleanRow;
        }
    }

    private static String deleteBracesValues(String itemFromList) {
        String pattern = "[<(](.+?)[)>]";
        String updated = itemFromList.replaceAll(pattern, "");
//        System.out.println("deleteBracesValues: " + updated);
        return updated;  //To change body of created methods use File | Settings | File Templates.
    }
}
