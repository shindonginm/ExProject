import { useEffect, useState } from "react";
import { getCompletedOrders } from "../../api/OrderListAPI";
import BackButtonComponent from "../../components/BackButtonComponent";
import { useNavigate } from "react-router-dom";

const COLS = [

    { id: 1, key: "orderNo",     label: "주문번호" },
    { id: 2, key: "orderDate",   label: "주문일자" },
    { id: 3, key: "cusComp",     label: "판매처명" },
    { id: 4, key: "itemName",    label: "상품명" },
    { id: 5, key: "orderQty",    label: "수량" },
    { id: 6, key: "orderPrice",  label: "단가" },
    { id: 7, key: "totalPrice",  label: "총 금액" },
    { id: 8, key: "deliveryDate",label: "납품일자" },
    { id: 9, key: "cusAddr",     label: "주소" },
];

export default function OrderApprovePage() {
    const nav = useNavigate();
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);

    const load = async () => {
        setLoading(true);
    const data = await getCompletedOrders();
    const normalized = (data || []).map(o => {
        const qty = Number(o.orderQty ?? 0);
        const price = Number(o.orderPrice ?? 0);
        const total = Number(o.totalPrice ?? 0) || qty * price;
        return { ...o, orderQty: qty, orderPrice: price, totalPrice: total };
    });
    setRows(normalized);
    setLoading(false);
    };

    useEffect(() => { load(); }, []);

    return (
        <div className="page-wrapper">
            <BackButtonComponent text="<  이전페이지" onClick={() => nav(-1)} />
        <h2 style={{ textAlign: "center" }}>주문완료현황</h2>

        {loading ? (
            <div style={{ textAlign: "center", padding: 20 }}>불러오는 중</div>
        ) : (
        <table>
            <thead>
                <tr>{COLS.map(c => <th key={c.id}>{c.label}</th>)}</tr>
            </thead>
            <tbody>
                {rows.length ? rows.map(r => (
                <tr key={r.orderNo}>
                    {COLS.map(c => {
                    const val = r[c.key];
                    if (c.key === "totalPrice" || c.key === "orderQty" || c.key === "orderPrice") {
                        return <td key={c.id}>{Number(val ?? 0).toLocaleString()}</td>;
                    }
                    return <td key={c.id}>{val}</td>;
                    })}
                </tr>
                )) : (
                <tr><td colSpan={COLS.length} style={{ textAlign: "center" }}>완료된 주문이 없습니다.</td></tr>
                )}
            </tbody>
        </table>
        )}

        <div style={{ textAlign: "right", marginTop: 12 }}>
            <button onClick={load}>새로고침</button>
        </div>
        </div>
    );
}