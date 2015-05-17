package com.nutri.preparation.utils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 29.09.14
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public final class ParserUtils {
    private ParserUtils(){
    }
    private final static Map<String,String> MEASURES_MAP = new HashMap<String, String>();
    private final static int MAX_MEASURE_WORDS = 3;

   public static int getMaxMeasureWords() {
        return MAX_MEASURE_WORDS;
    }

    public static List<String> tokenizeStringToList(String row, String token) {

        List<String>  itemList = new ArrayList<String>();
        StringTokenizer itemTokenizer = new StringTokenizer(row, token);

        while (itemTokenizer.hasMoreTokens()) {
            String str = itemTokenizer.nextToken();
            itemList.add(str.trim());
        }
        return itemList;
    }
    public static boolean isMeasure(String string){
        initMeasuresMap();
        return MEASURES_MAP.containsKey(string);
    }

    public static String getMeasureValue(String string){
        initMeasuresMap();
        return MEASURES_MAP.get(string);
    }
    private static void initMeasuresMap(){
//        "г", "кг", "шт", "л", "мл", "ч. л.", "ч.л.", "ч л", "ст. л",
//                "ст.л", "ст л", "по вкусу", "чайных ложек", "чайная ложка", "чайные ложки", "чайной ложки",
//                "столовых ложек", "столовая ложка", "столовые ложки", "столовой ложки", "стакан", "стакана", "стаканов",
//                "штук", "штуки", "штуки"
        //??? пакет пакетик
        MEASURES_MAP.put("г","g");
        MEASURES_MAP.put("гp.","g");
        MEASURES_MAP.put("гp","g");
        MEASURES_MAP.put("грамм","g");
        MEASURES_MAP.put("кг","kg");
        MEASURES_MAP.put("шт","sht");
        MEASURES_MAP.put("шт.","sht");
        MEASURES_MAP.put("штук","sht");
        MEASURES_MAP.put("штуки","sht");
        MEASURES_MAP.put("штуки","sht");
        MEASURES_MAP.put("зубчиков","sht");
        MEASURES_MAP.put("зубчик","sht");
        MEASURES_MAP.put("зубок","sht");
        MEASURES_MAP.put("зуб.","sht");
        MEASURES_MAP.put("зуб","sht");
        MEASURES_MAP.put("зубков","sht");
        MEASURES_MAP.put("пуч.","sht");
        MEASURES_MAP.put("вилок","sht");
        MEASURES_MAP.put("вилка","sht");
        MEASURES_MAP.put("головка","sht");
        MEASURES_MAP.put("головки","sht");
        MEASURES_MAP.put("головок","sht");
        MEASURES_MAP.put("ломтика","sht");
        MEASURES_MAP.put("ломтик","sht");
        MEASURES_MAP.put("ломт","sht");
        MEASURES_MAP.put("ломт.","sht");
        MEASURES_MAP.put("кусочков","sht");
        MEASURES_MAP.put("кусков","sht");
        MEASURES_MAP.put("кусок","sht");
        MEASURES_MAP.put("головок","sht");
        MEASURES_MAP.put("л","l");
        MEASURES_MAP.put("л","l");
        MEASURES_MAP.put("литр","l");
        MEASURES_MAP.put("литра","l");
        MEASURES_MAP.put("литров","l");
        MEASURES_MAP.put("мл","ml");
        MEASURES_MAP.put("миллилитра","ml");
        MEASURES_MAP.put("миллилитров","ml");
        MEASURES_MAP.put("ч. л.","chl");
        MEASURES_MAP.put("ч. л","chl");
        MEASURES_MAP.put("ч.л","chl");
        MEASURES_MAP.put("ч.л.","chl");
        MEASURES_MAP.put("ч л","chl");
        MEASURES_MAP.put("чл","chl");
        MEASURES_MAP.put("чайных ложек","chl");
        MEASURES_MAP.put("чайная ложка","chl");
        MEASURES_MAP.put("чайные ложки","chl");
        MEASURES_MAP.put("чайной ложки","chl");
        MEASURES_MAP.put("ст. л","stl");
        MEASURES_MAP.put("ст. л.","stl");
        MEASURES_MAP.put("ст.л","stl");
        MEASURES_MAP.put("ст.л.","stl");
        MEASURES_MAP.put("ст л","stl");
        MEASURES_MAP.put("стл","stl");
        MEASURES_MAP.put("столовых ложек","stl");
        MEASURES_MAP.put("столовая ложка","stl");
        MEASURES_MAP.put("столовые ложки","stl");
        MEASURES_MAP.put("столовой ложки","stl");
        MEASURES_MAP.put("стакан","st");
        MEASURES_MAP.put("стакана","st");
        MEASURES_MAP.put("стаканов","st");
        MEASURES_MAP.put("ст","st");
        MEASURES_MAP.put("ст.","st");
        MEASURES_MAP.put("стак.","st");
        MEASURES_MAP.put("стак","st");
        MEASURES_MAP.put("по вкусу","any");
        MEASURES_MAP.put("щепотку","any");
        MEASURES_MAP.put("щепотка","any");
        MEASURES_MAP.put("щепотки","any");
        MEASURES_MAP.put("на кончике ножа","any");
    }
}
