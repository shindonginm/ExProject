import axios_api from "./axios";

// 전체 아이템 재고 리스트 (ItemStock)
export const getItemStockList = async () => {
  const res = await axios_api.get("/stock/itemstocks");
  return res.data;
};

// 생산 완료된 리스트 (Plan 중 생산완료 상태)
export const getCompletedPlans = async () => {
  const res = await axios_api.get("/plan/planlist/completed");
  return res.data;
};
