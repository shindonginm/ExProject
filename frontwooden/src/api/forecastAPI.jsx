import axios_api from "./axios";

// 예측 + 실적 시리즈 (FastAPI 포함)
export const getForecastSeries = async (itemNo, h = 12) => {
  const res = await axios_api.get("/forecast/series", {
    params: { itemNo, h },
  });
  return res.data;
};

// 과거 주간 실적만 조회
export const getWeeklyHistory = async (itemNo, weeks = 52) => {
  const res = await axios_api.get("/history/weekly", {
    params: { itemNo, weeks },
  });
  return res.data;
};
