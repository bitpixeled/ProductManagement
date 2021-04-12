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

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {
    private Map<Product, List<Review>> products = new HashMap<>();
    private ResourceFormatter formatter;
    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());
    private ResourceBundle config = ResourceBundle.getBundle("config");
    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Path.of(config.getString("temp.folder"));

    private static Map<String, ResourceFormatter> formatters =
            Map.of("en-GB", new ResourceFormatter(Locale.UK),
                    "en-US", new ResourceFormatter(Locale.US),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
                    "zh-CN", new ResourceFormatter(Locale.CHINA)
            );

    public void changeLocale(String languageTag) {
        formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }

    public static Set<String> getSupportedLocale() {
        return formatters.keySet();
    }

    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }

    public ProductManager(String languageTag) {
        changeLocale(languageTag);
        loadAllData();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        try {
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void dumpData(){
        try {
            if (Files.notExists(tempFolder)){
                Files.createDirectory(tempFolder);
            }
            Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), Instant.now()));
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))){
                out.writeObject(products);
                products = new HashMap<>();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Error dumping data "
                    + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreData (){
        try {
            Path tempFile = Files.list(tempFolder)
                    .filter(path -> path.getFileName().toString().endsWith("tmp"))
                    .findFirst()
                    .orElseThrow();
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))){
                products = (HashMap)in.readObject();
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error restoring data " + e.getMessage(), e);
        }
    }

    private void loadAllData(){
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(this::loadProduct)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(product -> product, this::loadReviews));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading data "+e.getMessage(), e);
        }
    }

    private Product loadProduct (Path file){
        Product product = null;
        try {
            product = parseProduct(Files.lines(dataFolder.resolve(file), StandardCharsets.UTF_8).findFirst().orElseThrow());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading product "+e.getMessage());
        }
        return product;
    }

    private List<Review> loadReviews (Product product){
        List<Review> reviews = null;
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)){
            reviews = new ArrayList<>();
        } else {
            try {
                reviews = Files.lines(file, StandardCharsets.UTF_8)
                        .map(this::parseReview)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error loading reviews "+ e.getMessage());
            }
        }
        return reviews;
    }

    private Review parseReview (String text){
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(
                    Rateable.convert(Integer.parseInt((String)values[0])),
                    (String) values[1]
            );
        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing review "+text);
        }
        return review;
    }

    private Product parseProduct (String text){
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
            switch ((String) values[0]){
                case "D":
                    product = new Drink(id,name,price,rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
                    product = new Food(id,name,price,rating,bestBefore);
                    break;
            }
        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            logger.log(Level.WARNING, "Error parsing product "+text+e.getMessage());
        }
        return product;
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {

        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));
//        int sum = 0;
//        for (Review review : reviews){
//            sum += review.getRating().ordinal();
//        }
//        product = product.applyRating(Rateable.convert(Math.round((float) sum / reviews.size())));
        product = product.applyRating(
                Rateable.convert(
                        (int) Math.round(
                                reviews.stream()
                                        .mapToInt(r -> r.getRating().ordinal())
                                        .average()
                                        .orElse(0)
                        )));
        products.put(product, reviews);
        return product;
    }

    public Product findProduct(int id) throws ProductManagerException {
//        Product result = null;
//        for (Product product: products.keySet()){
//            if (product.getId() == id){
//                result = product;
//                break;
//            }
//        }
//        return result;
        return products.keySet()
                .stream()
                .filter((p) -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProductManagerException("Product with id "+id+ " not found"));//.orElseGet(() -> null);
    }

    public void printProductReport(int id) {
        try {
            printProductReport(findProduct(id));
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error printing product report "+e.getMessage());
        }
    }

    public void printProductReport(Product product) throws IOException {
        List<Review> reviews = products.get(product);
//        StringBuilder txt = new StringBuilder();
        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId()));
        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE), StandardCharsets.UTF_8))){
        out.append(formatter.formatProduct(product)).append(System.lineSeparator());
        if (reviews.isEmpty()) {
            out.append(formatter.getText("no.reviews")).append(System.lineSeparator());
        } else {
            out.append(reviews.stream()
                    .map(r -> formatter.formatReview(r) + System.lineSeparator())
                    .collect(Collectors.joining())
            );
        }}
//        Collections.sort(reviews);
//        for (Review review : reviews) {
//            txt.append(formatter.formatReview(review));
//            txt.append('\n');
//        }
//        if (reviews.isEmpty()) {
//            txt.append(formatter.getText("no.reviews"));
//            txt.append('\n');
//        }
//        System.out.println(txt);
    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter) {

//        List<Product> productList = new ArrayList<>(products.keySet());
//        productList.sort(sorter);
        StringBuilder txt = new StringBuilder();
        products.keySet()
                .stream()
                .sorted(sorter)
                .filter(filter)
                .forEach((p) -> txt.append(formatter.formatProduct(p) + '\n')
                );
//        for (Product product: productList){
//            txt.append(formatter.formatProduct(product));
//            txt.append('\n');
//        }
        System.out.println(txt);
    }

    public Map<String, String> getDiscounts() {
        return products.keySet()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                product -> product.getRating().getStars(),
                                Collectors.collectingAndThen(
                                        Collectors.summingDouble(
                                                product -> product.getDiscount().doubleValue()),
                                        discount -> formatter.moneyFormat.format(discount))));
    }

    private static class ResourceFormatter {
        private Locale locale;
        private ResourceBundle resourceBundle;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            resourceBundle = ResourceBundle.getBundle("resources", locale);
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            return MessageFormat.format(resourceBundle.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()));
        }

        private String formatReview(Review review) {
            return MessageFormat.format(resourceBundle.getString("review"),
                    review.getRating().getStars(),
                    review.getComments());
        }

        private String getText(String key) {
            return resourceBundle.getString(key);
        }
    }
}
