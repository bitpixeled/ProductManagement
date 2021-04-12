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

package labs.pm.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * {@code Product} abstract class represents properties and behaviour
 * of product objects in the Product Management System
 * <br>
 * Each product has an id, name, and price
 * <br>
 * Each product can have discount, calculated based on
 * {@link #DISCOUNT_RATE discount rate}
 * @author Halil SARI
 * @version 1.0.1
 *  */
public abstract class Product implements Rateable<Product>, Serializable {

    /**
     * A constant that defines a {@link java.math.BigDecimal BigDecimal} value
     * of the discount rate
     * <br>
     * Discount rate is 10%
     * <br>
     */
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);
    private int id;
    private String name;
    private BigDecimal price;
    private final Rating rating;

    Product() {
        this(0, "No Name", BigDecimal.ZERO);
    }

    Product(int id, String name, BigDecimal price) {
        this(id, name, price, Rating.NOT_RATED);
    }
    Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public LocalDate getBestBefore() {
        return LocalDate.now();
    }

    @Override
    public Rating getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public BigDecimal getPrice() {
        return price;
    }

//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }

    /**
     * Calculates discount based on product price and
     * {@link #DISCOUNT_RATE discount rate}
     * @return a {@link java.math.BigDecimal BigDecimal}
     * value of the discount
     */
    public BigDecimal getDiscount(){
        return price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

//    public abstract Product applyRating (Rating newRating);

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", discount=" + getDiscount() +
                ", rating=" + rating.getStars() +
                " " + getBestBefore();
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
        if (o instanceof Product) {
            Product product = (Product) o;
            return this.id == product.id; // && name.equals(product.name);
        }
        return false;
    }



    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}