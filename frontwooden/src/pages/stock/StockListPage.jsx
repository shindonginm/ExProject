import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getItemStockList } from "../../api/ItemStockAPI.jsx";
import { getCompletedPlanList } from "../../api/PlanListAPI.jsx";
import { ItemStockColumns } from "../../arrays/ItemStockArrays.jsx";
import { PlanListArrays as CompletedPlanArrays } from "../../arrays/PlanArrays.jsx";
import BackButtonComponent from "../../components/BackButtonComponent.jsx";
import ButtonComponent from "../../components/ButtonComponent.jsx";
import SearchComponent from "../../components/SearchComponent.jsx";


const StockListPage = () => {
  const navigate = useNavigate();
  const [rows, setRows] = useState([]);
  const [completed, setCompleted] = useState([]);
  const [loading, setLoading] = useState(false);

  // 재고 검색
  const [q, setQ] = useState("");
  const [term, setTerm] = useState("");

  // 생산완료 검색
  const [cpQ, setCpQ] = useState("");
  const [cpTerm, setCpTerm] = useState("");

  // 새로 고침 : 재고 + 생산완료리스트
  const reloadAll = async () => {
    setLoading(true);
    try {
      const [stocks, plans] = await Promise.all([
        getItemStockList(),
        getCompletedPlanList(),
      ]);
      setRows(Array.isArray(stocks) ? stocks : []);
      setCompleted(Array.isArray(plans) ? plans : []);
    } catch (e) {
      console.error(e);
      const {response} = e || {};
      const {data} = response || {};
      alert(data?.message || data?.error || "재고 목록을 가져오지 못했습니다.");
      setRows([]);
      setCompleted([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { reloadAll(); }, []);

  // 생산완료 안전 배열 + 숫자 정리
  const safeCompleted = useMemo (
    () => (Array.isArray(completed) ? completed : []).map(p => ({
      ...p,
      planQty: Number(p.planQty || 0),
    })),
    [completed]
  )

  // 재고 검색 필터링
  const filtered = useMemo(() => {
    const t = term.trim().toLowerCase();
    if (!t) return rows;
    return rows.filter(({itemName = ""}) => itemName.toLowerCase().includes(t));
  }, [rows, term]);

  // 생산완료 검색 필터링
  const filteredCompleted = useMemo(() => {
    const t = (cpTerm || "").trim().toLowerCase();
    if (!t) return safeCompleted;
    return safeCompleted.filter(p => {
      const name = (p.itemName || "").toLowerCase();
      const code = (p.itemCode || "").toLowerCase();
      const no = String(p.planNo || "");
      return name.includes(t) || code.includes(t) || no.includes(t);
    });
  }, [safeCompleted, cpTerm]);

  // 합계 (현재/누적)
  const {isQty, totalIn, totalOut} = useMemo(() => {
    return filtered.reduce(
      (acc, {isQty = 0, totalIn = 0, totalOut = 0}) => ({
        isQty: acc.isQty += Number(isQty),
        totalIn: acc.totalIn += Number(totalIn),
        totalOut: acc.totalOut += Number(totalOut),
      }),
      { isQty: 0, totalIn: 0, totalOut: 0 }
    );
  }, [filtered]);

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="< 이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>현재/누적 재고</h2>

      {/* 상단 툴바 */}
      <div style={{ display: "flex", gap: 12, alignItems: "center", margin: "8px 0" }}>
        <SearchComponent
          value={q}
          onChange={setQ}
          onDebounced={setTerm}
          delay={300}
          placeholder="검색"
          className="border rounded px-3 py-2"
        />
        <ButtonComponent onClick={reloadAll} text={loading ? "새로고침..." : "새로고침"} cln="submit" />
        <div style={{ marginLeft: "auto", fontWeight: 600 }}>
          현재재고 합: {isQty.toLocaleString()} &nbsp;/&nbsp;
          누적입고: {totalIn.toLocaleString()} &nbsp;/&nbsp;
          누적출고: {totalOut.toLocaleString()}
        </div>
      </div>

      {/* 재고 표 */}
      <table>
        <thead>
          <tr>
            {ItemStockColumns.map(({ id, label }) => (
              <th key={id}>{label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {filtered.length ? (
            filtered.map(({ isNo, ...rest }) => (
              <tr key={isNo} className="row">
                {ItemStockColumns.map(({ id, key, align = "left" }) => {
                  const val = rest[key];
                  return (
                    <td key={`${isNo}-${id}`} style={{ textAlign: align }}>
                      {typeof val === "number" ? Number(val).toLocaleString() : (val ?? "")}
                    </td>
                  );
                })}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={ItemStockColumns.length} style={{ textAlign: "center" }}>
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <hr className="my-6 opacity-30" />

      {/* 생산완료 리스트 */}
      <h3 className="text-xl font-semibold mb-2">생산완료 리스트</h3>
      <div style={{ display: "flex", gap: 12, alignItems: "center", margin: "8px 0" }}>
        <SearchComponent
          value={cpQ}
          onChange={setCpQ}
          onDebounced={setCpTerm}
          delay={300}
          placeholder="완료목록 검색 (품목명/코드/계획번호)"
          className="border rounded px-3 py-2"
        />
      </div>

      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            {CompletedPlanArrays.map(({ clmn, content }) => (
              <th key={clmn} className="border px-2 py-1">{content}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {filteredCompleted.length ? (
            filteredCompleted.map((row) => (
              <tr key={row.planNo}>
                {CompletedPlanArrays.map(({ clmn }) => {
                  const v = row?.[clmn];
                  return (
                    <td key={`${row.planNo}-${clmn}`} className="border px-2 py-1">
                      {typeof v === "number" ? Number(v).toLocaleString() : (v ?? "")}
                    </td>
                  );
                })}
              </tr>
            ))
          ) : (
            <tr>
              <td className="border px-2 py-6 text-center" colSpan={CompletedPlanArrays.length}>
                생산완료 내역이 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default StockListPage;
