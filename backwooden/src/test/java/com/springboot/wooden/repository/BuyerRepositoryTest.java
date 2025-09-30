package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Buyer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@Log4j2
public class BuyerRepositoryTest {

    @Autowired
    private BuyerRepository buyerRepository;

    @Test
    public void testInsert() {
        for (int i = 0; i < 10; i++) {
            Buyer buyer = Buyer.builder()
                    .buyerComp("구매거래처" + i)
                    .buyerName("구매처 담당자" + i)
                    .buyerEmail("aaaa" + i + "naver.com")
                    .buyerPhone("01044447777")
                    .buyerAddr("경기도" + i)
                    .build();
            buyerRepository.save(buyer);
        }
    }

//    @Test
//    public void testRead() {
//        Long id = 3L;
//        Optional<Buyer> result = buyerRepository.findById(id);
//        Buyer buyer = result.orElseThrow();
//        log.info("READ => {}", buyer);
//    }

//    @Test
//    public void testModify() {
//        Long id = 3L;
//        Buyer buyer = buyerRepository.findById(id).orElseThrow();
//
//
//        buyer.changeBuyerComp("구매거래처(수정)");
//        buyer.changeBuyerName("담당자(수정)");
//        buyer.changeBuyerEmail("modified@naver.com");
//        buyer.changeBuyerPhone("01012345678");
//        buyer.changeBuyerAddr("서울특별시 수정구");
//
//        buyerRepository.save(buyer);
//        log.info("MODIFY => {}", buyer);
//    }

//    @Test
//    public void testDelete() {
//        Long id = 3L;
//        buyerRepository.deleteById(id);
//        log.info("DELETE => id={}", id);
//    }
}
