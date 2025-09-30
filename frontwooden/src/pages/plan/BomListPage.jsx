import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCRUD } from "../../hook/useCRUD.jsx";

import ModalComponent from "../../components/ModalComponent";
import ButtonComponent from "../../components/ButtonComponent.jsx";
import BackButtonComponent from "../../components/BackButtonComponent.jsx";

import BomForm from "../../form/bom/BomForm.jsx";

import { getBomList, createBom, updateBom, deleteBom } from "../../api/BomAPI";
import { getItemList } from "../../api/ItemListAPI";
import { getPartList } from "../../api/PartListAPI";


const COLUMNS = [
  { id: "bomId", label: "BOM ID", key: "bomId" },
  { id: "itemName", label: "완제품", key: "itemName" },
  { id: "partName", label: "부품", key: "partName" },
  { id: "qtyPerItem", label: "수량", key: "qtyPerItem" },
];

const initBomForm = () => ({
  bomId: null,
  itemNo: "",
  partNo: "",
  qtyPerItem: 1,
});

// useCRUD 계약에 맞게 api 세팅
// - getAll(): 리스트 반환
// - create(payload): 등록
// - update(formData): 수정 (formData.bomId 사용)
// - delete(id): 삭제
const api = {
  getAll: getBomList,
  create: createBom,
  update: updateBom,           // formData 전체가 들어옵니다.
  delete: deleteBom,           // 숫자 id만 들어옵니다.
};

const BomListPage = () => {
  const navigate = useNavigate();

  const {
    items, setItems,
    formData, setFormData,
    selectedItem,
    handleChange,
    handleCreate, handleUpdate, handleDelete,
    openCreate, openEdit,
    closeCreate, closeEdit,
    isCreateOpen, isEditOpen,
  } = useCRUD({
    initFormData: () => initBomForm(),
    api,
    keyField: "bomId",
  });

  // 목록 로딩
  useEffect(() => {
    (async () => {
      const list = await getBomList();
      setItems(list);
    })();
  }, [setItems]);

  // 드롭다운 옵션
  const [itemOptions, setItemOptions] = useState([]);
  const [partOptions, setPartOptions] = useState([]);

  useEffect(() => {
    (async () => {
      const items = await getItemList();
      setItemOptions(items.map(it => ({ value: Number(it.itemNo), label: it.itemName })));
      const parts = await getPartList();
      setPartOptions(parts.map(pt => ({ value: Number(pt.partNo), label: pt.partName })));
    })();
  }, []);

  // 편집 모달 열릴 때 초기값 세팅 (이름 → 번호 역매핑)
  useEffect(() => {
    if (!isEditOpen || !selectedItem) return;
    const itemNo = itemOptions.find(o => o.label === selectedItem.itemName)?.value ?? "";
    const partNo = partOptions.find(o => o.label === selectedItem.partName)?.value ?? "";
    setFormData(prev => ({
      ...prev,
      bomId: selectedItem.bomId,
      itemNo,
      partNo,
      qtyPerItem: selectedItem.qtyPerItem ?? 1,
    }));
  }, [isEditOpen, selectedItem, itemOptions, partOptions, setFormData]);

  // 기본 submit/버블 방지
  const stop = (e) => { e?.preventDefault?.(); e?.stopPropagation?.(); };

  const refetch = async () => setItems(await getBomList());

  const doCreate = async (e) => { stop(e); await handleCreate(); await refetch(); closeCreate(); };
  const doUpdate = async (e) => { stop(e); await handleUpdate(); await refetch(); closeEdit(); };
  const doDelete = async (e) => { stop(e); await handleDelete(); await refetch(); closeEdit(); };

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="< 이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>BOM 목록</h2>

      <table>
        <thead>
          <tr>{COLUMNS.map(c => <th key={c.id}>{c.label}</th>)}</tr>
        </thead>
        <tbody>
          {items?.length ? items.map(row => (
            <tr key={row.bomId} onClick={() => openEdit(row)} className="row">
              {COLUMNS.map(c => <td key={c.id}>{row[c.key]}</td>)}
            </tr>
          )) : (
            <tr><td colSpan={COLUMNS.length} style={{textAlign:"center"}}>데이터가 없습니다.</td></tr>
          )}
        </tbody>
      </table>

      <br />
      {/* 공용 버튼이므로 onClick만 사용 */}
      <ButtonComponent onClick={openCreate} text="BOM 등록" cln="submit" />

      {/* 등록 */}
      <ModalComponent
        isOpen={isCreateOpen}
        onClose={closeCreate}
        title="BOM 등록"
        onConfirm={doCreate}
      >
        <form onSubmit={stop}>
          <BomForm
            formData={formData}
            onChange={handleChange}
            itemOptions={itemOptions}
            partOptions={partOptions}
          />
          <div className="btn-wrapper">
            <ButtonComponent text="등록" onClick={doCreate} cln="submit" />
          </div>
        </form>
      </ModalComponent>

      {/* 수정/삭제 */}
      <ModalComponent
        isOpen={isEditOpen}
        onClose={closeEdit}
        title="BOM 수정/삭제"
        onConfirm={doUpdate}
      >
        {selectedItem && (
          <form onSubmit={stop}>
            <BomForm
              formData={formData}
              onChange={handleChange}
              itemOptions={itemOptions}
              partOptions={partOptions}
            />
            <div className="btn-wrapper">
              <ButtonComponent text="수정" onClick={doUpdate} cln="fixbtn" />
              <ButtonComponent text="삭제" onClick={doDelete} cln="delbtn" />
            </div>
          </form>
        )}
      </ModalComponent>
    </div>
  );
};

export default BomListPage;
