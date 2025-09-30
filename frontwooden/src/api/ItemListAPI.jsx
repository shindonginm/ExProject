import axios_api from "./axios";

const host = "/plan/itemlist";

// 전체 조회
export const getItemList = async () => {
  const res = await axios_api.get(host);
  return res.data;
};

// 등록
export const createItemList = async (formData) => {
  const payload = {
    ...formData,
    itemNo: formData.itemNo ? Number(formData.itemNo) : undefined,
    itemPrice: Number(formData.itemPrice),
  };
  const res = await axios_api.post(host, payload);
  return res.data;
};

// 수정
export const updateItemList = async (formData) => {
  const { itemNo } = formData;
  const payload = {
    ...formData,
    itemNo: Number(itemNo),
    itemPrice: Number(formData.itemPrice),
  };
  const res = await axios_api.put(`${host}/${itemNo}`, payload);
  return res.data;
};

// 삭제
export const deleteItemList = async (itemNo) => {
  const res = await axios_api.delete(`${host}/${itemNo}`);
  return res.data;
};
