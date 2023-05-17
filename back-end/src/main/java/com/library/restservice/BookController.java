package com.library.restservice;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.example.incBookStockRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import entities.Book;
import entities.Borrow;
import entities.Card;
import queries.ApiResult;
import queries.BookQueryConditions;
import system.LibraryManagementSystem;
import system.LibraryManagementSystemImpl;
import utils.ConnectConfig;
import utils.DatabaseConnector;

@RestController
//@RequestMapping("/api")
public class BookController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/book")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    private static final Logger log = Logger.getLogger(BookController.class.getName());
    LibraryManagementSystem st;

    @Component
    @Order(value = 1)
    public class StartRunnerOne implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            try {
                // parse connection config from "resources/application.yaml"
                ConnectConfig conf = new ConnectConfig();
                log.info("Success to parse connect config. " + conf.toString());
                // connect to database
                DatabaseConnector connector = new DatabaseConnector(conf);
                boolean connStatus = connector.connect();
                if (!connStatus) {
                    log.severe("Failed to connect database.");
                    System.exit(1);
                }

                st = new LibraryManagementSystemImpl(connector);


                // release database connection handler
//                if (connector.release()) {
//                    log.info("Success to release connection.");
//                } else {
//                    log.warning("Failed to release connection.");
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    


    @PostMapping("/storeBook")
    public ApiResult storeBook(@RequestBody Book book) {
        return st.storeBook(book);
    }

    @PostMapping("/incBookStock")
    public ApiResult incBookStock(@RequestBody List<Integer> query) {
        return st.incBookStock(query.get(0),query.get(1));
    }

    @PostMapping("/batchStoreBook")
    public ApiResult batchStoreBook(@RequestBody List<Book> books) {
        return st.storeBook(books);
    }

    @PostMapping("/removeBook")
    public ApiResult removeBook(@RequestBody int query) {
        return st.removeBook(query);
    }

    @PostMapping("/modifyBookInfo")
    public ApiResult modifyBookInfo(@RequestBody Book book){
        return st.modifyBookInfo(book);
    }

    @PostMapping("/queryBook")
    public ApiResult queryBook(@RequestBody BookQueryConditions conditions) {
        return st.queryBook(conditions);
    }

    @PostMapping("/borrowBook")
    public ApiResult borrowBook(@RequestBody Borrow borrow) {
        return st.borrowBook(borrow);
    }

    @PostMapping("/returnBook")
    public ApiResult returnBook(@RequestBody Borrow borrow) {
        return st.returnBook(borrow);
    }

    @PostMapping("/showBorrowHistory")
    public ApiResult showBorrowHistory(@RequestBody int cardId) {
        return st.showBorrowHistory(cardId);
    }

    @PostMapping("/registerCard")
    public ApiResult registerCard(@RequestBody Card card) {
        return st.registerCard(card);
    }

    @PostMapping("/removeCard")
    public ApiResult removeCard(@RequestBody int cardId) {
        return st.removeCard(cardId);
    }

    @GetMapping("/showCards")
    public ApiResult showCards() {
        return st.showCards();
    }
    

}
