package com.nutri.preparation.parser.page;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.nutri.preparation.dto.DocumentDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by katerinaglushchenko on 1/15/15.
 */
public class SeleniumParserPovarenok {

    List<String> visitedUrl;
    final String startPage;
    final int maxNumberOfPages;

    public SeleniumParserPovarenok(String startPage, int maxNumberOfPages) {
        this.startPage = startPage;
        this.maxNumberOfPages = maxNumberOfPages;
        this.visitedUrl = new ArrayList<String>();
    }
//http://www.povarenok.ru/r//<div class="recipe-time-peaces">
//        <div class="gray">
//        <strong>Время приготовления:</strong> <time datetime="PT60M" itemprop="totalTime">60 минут</time>
//        </div>
//        <div class="gray">
//        <strong>Количество порций:</strong> 4
//                </div>
//        </div>

//    <div id="print_body" itemscope itemtype="http://data-vocabulary.org/Recipe">
//
//    <h1><a href="http://www.povarenok.ru/recipes/show/107378/" style="text-decoration: none; cursor: default;">Самые сочные мясные котлетки</a></h1>

    //    <div class="paginator" id="paginator_0" style="width:100%"><table width="100%"><tbody><tr><td width="7.142857142857143%"><span><a href="/recipes/~49/">49</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~50/">50</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~51/">51</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~52/">52</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~53/">53</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~54/">54</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~55/">55</a></span></td><td width="7.142857142857143%"><span><strong>56</strong></span></td><td width="7.142857142857143%"><span><a href="/recipes/~57/">57</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~58/">58</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~59/">59</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~60/">60</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~61/">61</a></span></td><td width="7.142857142857143%"><span><a href="/recipes/~62/">62</a></span></td></tr><tr><td colspan="14"><div class="scroll_bar"><div class="scroll_trough"></div><div class="scroll_thumb" style="width: 8px; left: 4.87481346377052px;"><div class="scroll_knob"></div></div><div class="current_page_mark" style="width: 3px; left: 3.97172939810977px;"></div></div></td></tr></tbody></table></div>
    public DocumentDto parseDocumentPovarenok(String url) {
        long timeBench = new Date().getTime();
        WebDriver driver1 = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
        driver1.get(url);
        DocumentDto result = new DocumentDto();

        String ingredient = "recipe-ing";
        String instruction = "instructions";
//        String tagList = "tag-list";
        WebElement allElems;
        try {
            allElems = driver1.findElement(By.id("print_body"));
            String name = allElems.findElement(By.tagName("h1")).getText();
            System.out.println(name);
            result.setName(name);

            List<WebElement> elementList = driver1.findElement(By.className(ingredient)).findElements(By.tagName("li"));
            String ingredientList = "";
            for (WebElement element1 : elementList) {
                ingredientList += (getTextFromElement(element1)) + "; ";
            }
            result.setIngredients(ingredientList);
            List<WebElement> categoryList = allElems.findElement(By.className("recipe-infoline")).findElements(By.tagName("a"));
            String category;
            if (!categoryList.isEmpty()) {
                category = categoryList.get(0).getText();
            } else {
                throw new IllegalArgumentException("category not found");
            }
            WebElement element = driver1.findElement(By.className("recipe-text"));
            result.setInstruction(element.getText());

            result.setCategory(category);
            String time = allElems.findElement(By.tagName("time")).getText();
            WebElement peaces = allElems.findElement(By.className("recipe-time-peaces"));
            String portions;
            if (peaces.getText().contains("Количество порций: ")) {
                String[] items = peaces.getText().split("Количество порций: ");
                portions = items[1];
            } else {
                throw new IllegalArgumentException("setPortions not found");
            }
            String img = allElems.findElement(By.className("recipe-img")).findElement(By.tagName("img")).getAttribute("src");
            if(!img.equals("http://www.povarenok.ru/images/recipes/1.gif"))
                result.setImg(img);
            else
                result.setImg("");

            result.setTime(time);
            result.setPortions(portions);
            result.setUrl(url);
            driver1.close();
//            System.out.println("!@#$ time "+(new Date().getTime() - timeBench));
            return result;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println(url + " is not valid page");
            return null;
        }
    }

    private String getTextFromElement(WebElement element) {
        return element != null ? element.getText() : null;
    }

    public List<DocumentDto> getPagesSimple() {
        int startPage = 100;
        int endPage = 110;
        List<DocumentDto> dtoList = new ArrayList<DocumentDto>();

        for (int i = startPage; i <= endPage; i++) {
            String href = "http://www.povarenok.ru/recipes/show/" + i + "/";
            DocumentDto documentDto = parseDocumentPovarenok(href);
            System.out.println(documentDto);
            if(documentDto!=null)
             dtoList.add(documentDto);
        }
        return dtoList;
    }

    public List<DocumentDto> getPages() throws IOException {
        int counter = 0;
        List<DocumentDto> dtoList = new ArrayList<DocumentDto>();
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
//FIREFOX_24        HtmlUnitDriver driver = new HtmlUnitDriver(true);

//        WebDriver driver = new FirefoxDriver();
//        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
//        driver.setJavascriptEnabled(true);
//
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(startPage);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        http://www.povarenok.ru/recipes/show/107378/
//       WebElement we = driver.findElement(By.className("icon-heart"));//.findElementByClassName("icon-heart");
//        System.out.println("!@#"+we);
        List<WebElement> elements = driver.findElements(By.tagName("a"));

        for (WebElement link : elements) {
            String href = link.getAttribute("href");
//            System.out.println(href);
//            recipes/show/107378/
            if (href != null && href.contains("/recipes/") && href.contains("show") && !visitedUrl.contains(href) && counter <= maxNumberOfPages) {
//                if(href.matches("(/dir/recipes/salads/)(\\d+)(.*)")&&!visitedUrl.contains(href)&&counter<=maxNumberOfPages){
                System.out.println(counter + ": " + href);
                DocumentDto documentDto = parseDocumentPovarenok(href);
                System.out.println(documentDto);
                dtoList.add(documentDto);
                counter++;
                visitedUrl.add(href);
            }
        }
//        <a href="/recipes/nizkokaloriynye-blinchiki" target="_blank">Низкокалорийные блинчики</a>
        driver.close();
        return dtoList;
    }
}
