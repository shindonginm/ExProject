import axios_api from "./axios";

export const getBomList = () =>
  axios_api.get("/api/plan/bom").then((r) => r.data);
export const getBomOne = (bomId) =>
  axios_api.get(`/api/plan/bom/${bomId}`).then((r) => r.data);
export const createBom = (payload) =>
  axios_api.post("/api/plan/bom", payload).then((r) => r.data);
export const updateBom = (bomId, payload) =>
  axios_api.put(`/api/plan/bom/${bomId}`, payload).then((r) => r.data);
export const deleteBom = (bomId) =>
  axios_api.delete(`/api/plan/bom/${bomId}`).then((r) => r.data);
