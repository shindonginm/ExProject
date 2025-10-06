import { useEffect } from "react";
import { useCRUD } from "../../hook/useCRUD";
import { getPartStockList } from "../../api/partStockApi";
import { getCompletedPartOrders } from "../../api/PartOrderAPI";

import { initForms } from "../../arrays/TableArrays";
import { PartStockListArray } from "../../arrays/PartStockListArray";
import { partOrderArrays } from "../../arrays/partOrderArrays";

const PartStockListPage = () => {
  const {
    items: stocks,
    setItems: setStocks,
  } = useCRUD({
    initFormData: () => ({ ...initForms.partStock }),
  });

  // 완료 발주 목록은 별도 상태
  const {
    items: completed,
    setItems: setCompleted,
  } = useCRUD({
    initFormData: () => ({}),
  });

  // 최초 로드
  useEffect(() => {
    (async () => {
      try {
        const [s, c] = await Promise.all([
          getPartStockList(),
          getCompletedPartOrders(),
        ]);
        setStocks(Array.isArray(s) ? s : []);
        setCompleted(Array.isArray(c) ? c : []);
      } catch (e) {
        setStocks([]);
        setCompleted([]);
      }
    })();
  }, [setStocks, setCompleted]);

  const safeStocks = Array.isArray(stocks) ? stocks : [];
  const safeCompleted = Array.isArray(completed) ? completed : [];

  return (
    <div className="p-4 w-full bg-white">
      <h2 className="text-2xl font-bold mb-3">부품 재고 현황</h2>

      {/* 재고 테이블 */}
      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            {PartStockListArray.map((col) => (
              <th
                key={col.id}
                className={`border px-2 py-1 ${
                  col.align === "right" ? "text-right" : "text-left"
                }`}
                style={{ width: col.width }}
              >
                {col.content}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {safeStocks.length ? (
            safeStocks.map((row) => (
              <tr key={row.psNo}>
                <td className="border px-2 py-1">{row.psNo}</td>
                <td className="border px-2 py-1">{row.partName}</td>
                <td className="border px-2 py-1 text-right">{row.psQty}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td
                className="border px-2 py-6 text-center"
                colSpan={PartStockListArray.length}
              >
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <hr className="my-6 opacity-30" />

      <h3 className="text-xl font-semibold mb-2">입고완료 리스트</h3>
      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            {partOrderArrays.map((col) => (
              <th key={col.id} className="border px-2 py-1 text-left">
                {col.content}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {safeCompleted.length ? (
            safeCompleted.map((row) => (
              <tr key={row.poNo}>
                {partOrderArrays.map((col) => (
                  <td key={`${row.poNo}-${col.id}`} className="border px-2 py-1">
                    {row[col.clmn] ?? ""}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td
                className="border px-2 py-6 text-center"
                colSpan={partOrderArrays.length}
              >
                입고완료 내역이 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default PartStockListPage;
