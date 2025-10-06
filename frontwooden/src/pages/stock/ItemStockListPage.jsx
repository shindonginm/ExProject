import { useEffect } from "react";
import { useCRUD } from "../../hook/useCRUD";
import { initForms } from "../../arrays/TableArrays";
import { ItemStockListArray, CompletedPlanArrays } from "../../arrays/ItemStockListArray";
import { getItemStockList, getCompletedPlans } from "../../api/ItemStockApi";

const ItemStockListPage = () => {
  const { items: stocks, setItems: setStocks } = useCRUD({
    initFormData: () => ({ ...initForms.itemStock }),
  });
  const { items: completed, setItems: setCompleted } = useCRUD({
    initFormData: () => ({}),
  });

  useEffect(() => {
    (async () => {
      try {
        const [s, c] = await Promise.all([getItemStockList(), getCompletedPlans()]);
        setStocks(Array.isArray(s) ? s : []);
        setCompleted(Array.isArray(c) ? c : []);
      } catch (e) {
        console.error("[ItemStock] fetchAll failed", e?.response?.status, e?.response?.data);
        setStocks([]); setCompleted([]);
      }
    })();
  }, [setStocks, setCompleted]);

  const safeStocks = Array.isArray(stocks) ? stocks : [];
  const safeCompleted = Array.isArray(completed) ? completed : [];

  return (
    <div className="p-4 w-full bg-white">
      <h2 className="text-2xl font-bold mb-3">완제품 재고 현황</h2>

      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            {ItemStockListArray.map(col => (
              <th key={col.clmn} className="border px-2 py-1">{col.content}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {safeStocks.length ? safeStocks.map(row => (
            <tr key={row.isNo}>
              {ItemStockListArray.map(col => (
                <td key={`${row.isNo}-${col.clmn}`} className="border px-2 py-1">
                  {row[col.clmn] ?? ""}
                </td>
              ))}
            </tr>
          )) : (
            <tr>
              <td className="border px-2 py-6 text-center" colSpan={ItemStockListArray.length}>
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <hr className="my-6 opacity-30" />

      <h3 className="text-xl font-semibold mb-2">생산완료 리스트</h3>
      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            {CompletedPlanArrays.map(col => (
              <th key={col.clmn} className="border px-2 py-1">{col.content}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {safeCompleted.length ? safeCompleted.map(row => (
            <tr key={row.planNo}>
              {CompletedPlanArrays.map(col => (
                <td key={`${row.planNo}-${col.clmn}`} className="border px-2 py-1">
                  {row[col.clmn] ?? ""}
                </td>
              ))}
            </tr>
          )) : (
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

export default ItemStockListPage;
