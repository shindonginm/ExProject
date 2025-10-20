package com.springboot.wooden.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SalesRepository {
    private final JdbcTemplate jdbc;

    /** 월별 판매수익: 납품완료 + 승인완료만 */
    public List<Map<String, Object>> findMonthlyRevenueDelivered() {
        String sql = """
            SELECT DATE_FORMAT(o.order_date, '%Y-%m') AS ym,
                   SUM(COALESCE(o.order_qty,0) * COALESCE(i.item_price,0)) AS total
            FROM order_tbl o
            JOIN item_tbl  i ON i.item_no = o.item_no
            WHERE COALESCE(o.order_deli_state,'') IN ('납품완료','DELIVERED','Y','DONE')
              AND COALESCE(o.order_state,'')      IN ('승인완료','APPROVED')
            GROUP BY DATE_FORMAT(o.order_date, '%Y-%m')
            ORDER BY ym
        """;
        return jdbc.queryForList(sql);
    }

    /** 특정 월 Top10: 납품완료 + 승인완료만 */
    public List<Map<String, Object>> findItemQtyTop10DeliveredByMonth(String ym) {
        String sql = """
            SELECT i.item_no   AS itemNo,
                   i.item_name AS itemName,
                   SUM(COALESCE(o.order_qty,0)) AS qty
            FROM order_tbl o
            JOIN item_tbl i ON o.item_no = i.item_no
            WHERE COALESCE(o.order_deli_state,'') IN ('납품완료','DELIVERED','Y','DONE')
              AND COALESCE(o.order_state,'')      IN ('승인완료','APPROVED')
              AND DATE_FORMAT(o.order_date, '%Y-%m') = ?
            GROUP BY i.item_no, i.item_name
            ORDER BY qty DESC
            LIMIT 10
        """;
        return jdbc.queryForList(sql, ym);
    }

    /** 모든 월×상품 수량: 납품완료 + 승인완료만 */
    public List<Map<String, Object>> findItemQtyMonthlyAllDelivered() {
        String sql = """
            SELECT DATE_FORMAT(o.order_date, '%Y-%m') AS ym,
                   i.item_no   AS itemNo,
                   i.item_name AS itemName,
                   SUM(COALESCE(o.order_qty,0)) AS totalQty
            FROM order_tbl o
            JOIN item_tbl i ON o.item_no = i.item_no
            WHERE COALESCE(o.order_deli_state,'') IN ('납품완료','DELIVERED','Y','DONE')
              AND COALESCE(o.order_state,'')      IN ('승인완료','APPROVED')
            GROUP BY DATE_FORMAT(o.order_date, '%Y-%m'), i.item_no, i.item_name
            ORDER BY ym, totalQty DESC
        """;
        return jdbc.queryForList(sql);
    }
}
