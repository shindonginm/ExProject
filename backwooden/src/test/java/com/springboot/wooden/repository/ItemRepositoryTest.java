// src/test/java/com/springboot/wooden/repository/ItemRepositoryTest.java
package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Item;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@Log4j2
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testInsert() {
        for (int i = 0; i < 10; i++) {
            Item item = Item.builder()
                    .itemName("아이템" + i)
                    .itemCode("item"+ i)
                    .itemSpec("규격-" + i)
                    .itemPrice(1000 * (i + 1))
                    .build();
            itemRepository.save(item);
        }
    }

//    @Test
//    public void testRead() {
//        Long id = 3L; // 존재하는 PK로 변경
//        Optional<Item> result = itemRepository.findById(id);
//        Item item = result.orElseThrow();
//        log.info("READ => {}", item);
//    }
//
//    @Test
//    public void testModify() {
//        Long id = 3L; // 존재하는 PK로 변경
//        Item item = itemRepository.findById(id).orElseThrow();
//
//        item.changeItemName("아이템(수정)");
//        item.changeItemCode("I9999");
//        item.changeItemSpec("규격-수정");
//        item.changeItemPrice(9999);
//
//        itemRepository.save(item);
//        log.info("MODIFY => {}", item);
//    }
//
//    @Test
//    public void testDelete() {
//        Long id = 3L; // 존재하는 PK로 변경
//        itemRepository.deleteById(id);
//        log.info("DELETE => id={}", id);
//    }
}
