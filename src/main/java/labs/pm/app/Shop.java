/*
 * Copyright © 2021  Halils.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package labs.pm.app;

import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;

/**
 * {@code Shop} class represents an application that manages Products
 * @author Halil SARI
 * @version 1.0.0
 */
public class Shop {

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ProductManager pm = new ProductManager("en-GB");

        pm.createProduct(101,"Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.createProduct(102,"Coffee", BigDecimal.valueOf(2.99), Rating.NOT_RATED);
        pm.createProduct(103,"Whiskey", BigDecimal.valueOf(9.99), Rating.NOT_RATED);
        pm.createProduct(104,"Coke", BigDecimal.valueOf(1.99), Rating.NOT_RATED);

        pm.reviewProduct(101, Rating.FOUR_STAR, "Was okay");
        pm.reviewProduct(101, Rating.FOUR_STAR, "Not Bad");
        pm.reviewProduct(101, Rating.THREE_STAR, "Didn't like it");
        pm.reviewProduct(102, Rating.FOUR_STAR, "Smells good");
        pm.reviewProduct(103, Rating.FIVE_STAR, "Great");
        pm.reviewProduct(104, Rating.FOUR_STAR, "It's Good");

        pm.printProductReport(101);
        pm.printProductReport(102);
        pm.printProductReport(103);
        pm.printProductReport(104);

//        pm.parseProduct("D, 101, Tea, 1.99, 3, 2021-04-10");
//        pm.parseProduct("D, 102, Coffee, 1.99, 3, 2021-04-11");
//        pm.parseProduct("D, 103, Whiskey, 9.99, 3, 2021-04-11");
//        pm.parseProduct("D, 104, Coke, 1.99, 3, 2021-04-11");

//        pm.parseReview("101, 4, Nice Cup of Tea");
//        pm.parseReview("101, 3, Not that good");
//        pm.parseReview("102, 5, Great");
//        pm.parseReview("103, 5, Was okay");
//        pm.parseReview("104, 5, Liked it");


//        pm.parseProduct("F, 103, Cake, 3.99, 0, 2021-04-19");
//        pm.printProductReport(103);

//        pm.createProduct(102,"Coffee",BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        pm.reviewProduct(102, Rating.FOUR_STAR, "Nice cup of coffee");
//        pm.reviewProduct(102, Rating.FIVE_STAR, "Love it");
//        pm.reviewProduct(102, Rating.FIVE_STAR, "Great");
////        pm.printProductReport(102);
//
////        pm.changeLocale("en-GB");
//        Product p3 = pm.createProduct(103,"Cake",BigDecimal.valueOf(3.99), Rating.NOT_RATED);
//        p3 = pm.reviewProduct(p3, Rating.FOUR_STAR, "Nice!");
//        p3 = pm.reviewProduct(p3, Rating.THREE_STAR, "Average");
//        p3 = pm.reviewProduct(p3, Rating.FIVE_STAR, "Tasty");
////        pm.printProductReport(p3);
//
//        Product p4 = pm.createProduct(104,"Cookie",BigDecimal.valueOf(2.99), Rating.NOT_RATED);
//        p4 = pm.reviewProduct(p4, Rating.FOUR_STAR, "Thanks");
//        p4 = pm.reviewProduct(p4, Rating.FOUR_STAR, "Well made");
//        p4 = pm.reviewProduct(p4, Rating.FIVE_STAR, "Want more!");
//        pm.printProductReport(p4);
//
//        pm.printProducts(p -> p.getPrice().floatValue() < 2, (p1,p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
//
//        pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating+"\t"+discount));
//
//        Comparator<Product> ratingSorter = (p1,p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
//        Comparator<Product> priceSorter = (p1,p2) -> p2.getPrice().compareTo(p1.getPrice());
//        pm.printProducts(ratingSorter);
//        pm.printProducts(priceSorter);
//        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());

    }
}