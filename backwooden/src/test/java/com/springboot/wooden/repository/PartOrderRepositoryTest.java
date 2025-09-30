package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Buyer;
import com.springboot.wooden.domain.Part;
import com.springboot.wooden.domain.PartOrder;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
class PartOrderRepositoryTest {

    @Autowired private PartOrderRepository partOrderRepository;
    @Autowired private BuyerRepository buyerRepository;
    @Autowired private PartRepository partRepository;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("INSERT: 기존 Buyer/Part id로 부품발주 생성")
    @Transactional
    @Commit
    void testInsert() {

        Long buyerId = 1L;
        Long partId  = 1L;

        Buyer buyer = buyerRepository.findById(buyerId).orElseThrow();
        Part part   = partRepository.findById(partId).orElseThrow();

        PartOrder po = PartOrder.builder()
                .buyer(buyer)                       // Buyer 엔티티
                .part(part)                         // Part 엔티티
                .poQty(100)
                .poPrice(5000)
                .poState("미완료")
                .poDate(LocalDate.now())
                .buyerAddr(buyer.getBuyerAddr())    // Buyer에서 주소를 복사해옴
                .build();

        partOrderRepository.save(po);
        log.info("INSERT => {}", po);
    }

    @Test
    @DisplayName("READ: 단건 조회 + LAZY 필드 접근 (트랜잭션으로 해결)")
    @Transactional
    void testRead() {
        // ✔ 조회할 발주 PK 입력
        Long poId = 1L;

        PartOrder po = partOrderRepository.findById(poId).orElseThrow();

        log.info("READ => {}", po);

        // LAZY 초기화 문제 방지: @Transactional로 세션 유지
        log.info("READ.buyer => {}", po.getBuyer().getBuyerComp()); // 필드명 맞춰서
        log.info("READ.part  => {}", po.getPart().getPartName());   // 필드명 맞춰서
    }

    @Test
    @DisplayName("UPDATE: 수량/입고일 등 부분 수정")
    @Transactional
    @Commit
    void testModify() {
        // ✔ 수정할 발주 PK 입력
        Long poId = 1L;

        PartOrder po = partOrderRepository.findById(poId).orElseThrow();

        // 엔티티에 change 메서드가 있다면 그걸 사용, 없다면 setter/필드 접근으로
        po.changePoQty(150);
        po.changePoDate(LocalDate.now().plusDays(3));

        // @Transactional 이면 dirty checking 으로 flush됨 (save() 생략 가능)
        // partOrderRepository.save(po);

        log.info("MODIFY => {}", po);
    }

    // ============ UPDATE (연관수정) ============
    @Test
    @DisplayName("UPDATE: Buyer/Part 교체(연관 수정)")
    @Transactional
    @Commit
    void testRelink() {
        // ✔ 수정할 발주 PK + 교체할 FK 입력
        Long poId        = 1L;
        Long newBuyerId  = 2L;
        Long newPartId   = 2L;

        PartOrder po = partOrderRepository.findById(poId).orElseThrow();
        Buyer newBuyer   = buyerRepository.findById(newBuyerId).orElseThrow();
        Part newPart     = partRepository.findById(newPartId).orElseThrow();

        po.changeBuyer(newBuyer);
        po.changePart(newPart);

        // 재조회로 검증하고 싶다면 flush/clear 후 확인
        em.flush(); em.clear();
        PartOrder reloaded = partOrderRepository.findById(poId).orElseThrow();
        log.info("RELINK => buyer={}, part={}",
                reloaded.getBuyer().getBuyerComp(),
                reloaded.getPart().getPartName());
    }

    // ============ DELETE ============
    @Test
    @DisplayName("DELETE: PK로 삭제")
    @Transactional
    @Commit
    void testDelete() {
        // ✔ 삭제할 발주 PK 입력
        Long poId = 1L;
        partOrderRepository.deleteById(poId);

        Optional<PartOrder> after = partOrderRepository.findById(poId);
        log.info("DELETE => id={}, exists? {}", poId, after.isPresent());
    }
}
