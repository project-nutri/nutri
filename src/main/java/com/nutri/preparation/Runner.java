package com.nutri.preparation;

import com.nutri.preparation.indexer.IndexerReciept;
import com.nutri.preparation.parser.page.SeleniumParserPovarenok;
import com.nutri.preparation.dto.DocumentDto;


import java.io.IOException;
import java.util.Date;

/**
 * Created by katerinaglushchenko on 1/15/15.
 */
public class Runner {
    public static void main(String[] args) {
        testPovarenok();
    }

    private static void testPovarenok() {
        long time = new Date().getTime();
        System.out.println("start app");
        SeleniumParserPovarenok urlParser = new SeleniumParserPovarenok("http://www.povarenok.ru/recipes/", 3);
        String indexDir = "/Users/katerinaglushchenko/indexPovarenok";

        try {
            IndexerReciept indexer = new IndexerReciept(indexDir);

            int startPage = 200;
            int endPage = 2030;
//689 - 894
            for (int i = startPage; i <= endPage; i++) {
                String href = "http://www.povarenok.ru/recipes/show/" + i + "/";
                DocumentDto documentDto = urlParser.parseDocumentPovarenok(href);
//                System.out.println(documentDto);
                if (documentDto != null)
                    indexer.index(documentDto);
            }

            indexer.close();
            System.out.println("end " + (new Date().getTime() - time));

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
