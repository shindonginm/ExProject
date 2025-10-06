import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCRUD } from "../../hook/useCRUD.jsx";

import ModalComponent from "../../components/ModalComponent";
import ButtonComponent from "../../components/ButtonComponent.jsx";
import BackButtonComponent from "../../components/BackButtonComponent.jsx";

import BomForm from "../../form/plan/BomForm.jsx";

import { getItemList } from "../../api/ItemListAPI";
import { getPartList } from "../../api/PartListAPI";
import { getBomList, createBom, updateBom, deleteBom } from "../../api/BomAPI";
import { BomArrays } from "../../arrays/BomListArrays.jsx";

/** 폼 초기값 */
const initBomForm = () => ({
  bomId: null,
  itemNo: "",
  partNo: "",
  qtyPerItem: 1,
});

/** useCRUD 계약용 API 묶음 */
const bomApi = {
  getAll: getBomList,
  create: (fd) =>
    createBom({
      itemNo: Number(fd.itemNo),
      partNo: Number(fd.partNo),
      qtyPerItem: Number(fd.qtyPerItem ?? 1),
    }),
  update: updateBom, // formData에 bomId 포함
  delete: (id) => deleteBom(Number(id)),
};

const BomListPage = () => {
  const navigate = useNavigate();

  const {
    items,
    setItems,
    formData,
    setFormData,
    selectedItem,
    handleChange,
    handleCreate,
    handleUpdate,
    handleDelete,
    openCreate,
    openEdit,
    closeCreate,
    closeEdit,
    isCreateOpen,
    isEditOpen,
  } = useCRUD({
    initFormData: () => initBomForm(),
    api: bomApi,
    keyField: "bomId",
  });

  /** 최초 목록 로딩 */
  useEffect(() => {
    (async () => {
      try {
        const list = await bomApi.getAll();
        setItems(Array.isArray(list) ? list : []);
      } catch (e) {
        console.error("getBomList failed", e);
        setItems([]);
      }
    })();
  }, [setItems]);

  /** 드롭다운 옵션 */
  const [itemOptions, setItemOptions] = useState([]);
  const [partOptions, setPartOptions] = useState([]);

  useEffect(() => {
    (async () => {
      try {
        const items = await getItemList();
        setItemOptions(
          (items ?? []).map((it) => ({
            value: Number(it.itemNo),
            label: it.itemName,
          }))
        );
        const parts = await getPartList();
        setPartOptions(
          (parts ?? []).map((pt) => ({
            value: Number(pt.partNo),
            label: pt.partName,
          }))
        );
      } catch (e) {
        console.error("load options failed", e);
        setItemOptions([]);
        setPartOptions([]);
      }
    })();
  }, []);

  /** 편집 모달 열릴 때 선택행 → 폼으로 역매핑 */
  useEffect(() => {
    if (!isEditOpen || !selectedItem) return;
    const itemNo =
      itemOptions.find((o) => o.label === selectedItem.itemName)?.value ?? "";
    const partNo =
      partOptions.find((o) => o.label === selectedItem.partName)?.value ?? "";
    setFormData((prev) => ({
      ...prev,
      bomId: selectedItem.bomId,
      itemNo,
      partNo,
      qtyPerItem: Number(selectedItem.qtyPerItem ?? 1),
    }));
  }, [isEditOpen, selectedItem, itemOptions, partOptions, setFormData]);

  /** 공용 유틸 */
  const stop = (e) => {
    e?.preventDefault?.();
    e?.stopPropagation?.();
  };
  const refetch = async () => {
    const list = await bomApi.getAll();
    setItems(Array.isArray(list) ? list : []);
  };

  /** 등록 */
  const doCreate = async (e) => {
    stop(e);
    try {
      const ok = await handleCreate();
      if (ok) {
        await refetch();
        closeCreate();
      }
    } catch (err) {
      console.error(err);
      alert(err?.response?.data?.message ?? "등록 중 오류가 발생했습니다.");
    }
  };

  /** 수정 */
  const doUpdate = async (e) => {
    stop(e);
    if (!formData?.bomId) {
      alert("선택된 BOM ID가 없습니다. 행을 클릭해 수정하세요.");
      return;
    }
    try {
      const ok = await handleUpdate();
      if (ok) {
        await refetch();
        closeEdit();
      }
    } catch (err) {
      console.error(err);
      alert(err?.response?.data?.message ?? "수정 중 오류가 발생했습니다.");
    }
  };

  /** 삭제 */
  const doDelete = async (e) => {
    stop(e);
    try {
      await handleDelete();
      await refetch();
      closeEdit();
    } catch (err) {
      console.error(err);
      alert(err?.response?.data?.message ?? "삭제 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="< 이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>BOM 목록</h2>

      <table>
        <thead>
          <tr>
            {BomArrays.map((c) => (
              <th key={c.id}>{c.content}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items?.length ? (
            items.map((row) => (
              <tr key={row.bomId} onClick={() => openEdit(row)} className="row">
                {BomArrays.map((c) => (
                  <td
                    key={c.id}
                    style={{ textAlign: c.align || "left" }}
                  >
                    {row[c.clmn]}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={BomArrays.length}
                style={{ textAlign: "center" }}
              >
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <br />

      <ButtonComponent onClick={openCreate} text="BOM 등록" cln="submit" />

      {/* 등록 모달 */}
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

      {/* 수정/삭제 모달 */}
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
