import axios_api from "./axios";
import { BASE_URL } from "./config";

const host = `${BASE_URL}/plan/itemlist`;

// 전체 조회
export const getItemList = async () => {
  const res = await axios_api.get(host);
  return res.data;
};

// 등록
export const createItemList = async (formData) => {
  const res = await axios_api.post(host, formData);
  return res.data;
};

// 수정
export const updateItemList = async (itemNo, formData) => {
  const res = await axios_api.put(`${host}/${itemNo}`, formData);
  return res.data;
};

// 삭제
export const deleteItemList = async (itemNo) => {
  const res = await axios_api.delete(`${host}/${itemNo}`);
  return res.data;
};
