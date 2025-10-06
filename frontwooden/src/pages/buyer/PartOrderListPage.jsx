import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCRUD } from "../../hook/useCRUD.jsx";

import ModalComponent from "../../components/ModalComponent";
import ButtonComponent from "../../components/ButtonComponent.jsx";
import BackButtonComponent from "../../components/BackButtonComponent.jsx";

import PartOrderForm from "../../form/buyer/PartOrderForm.jsx";
import { initForms } from "../../arrays/TableArrays.jsx";
import { partOrderArrays } from "../../arrays/partOrderArrays.jsx";

// API
import {
  getPartOrders,
  createPartOrder,
  updatePartOrder,
  deletePartOrder,
} from "../../api/PartOrderAPI";
import { getBuyerCustomer } from "../../api/BuyerListAPI";
import { getPartList } from "../../api/PartListAPI";

const api = {
  getAll: getPartOrders,
  create: (fd) =>
    createPartOrder({
      poNo: fd.poNo ? Number(fd.poNo) : undefined,
      buyerNo: Number(fd.buyerNo),
      partNo: Number(fd.partNo),
      poQty: Number(fd.poQty),
      poPrice: Number(fd.poPrice),
      poState: fd.poState,
      poDate: fd.poDate || null,
      buyerAddr: fd.buyerAddr ?? "",
    }),
  update: (fd) =>
    updatePartOrder(Number(fd.poNo), {
      buyerNo: Number(fd.buyerNo),
      partNo: Number(fd.partNo),
      poQty: Number(fd.poQty),
      poPrice: Number(fd.poPrice),
      poState: fd.poState,
      poDate: fd.poDate || null,
      buyerAddr: fd.buyerAddr ?? "",
    }),
  delete: (id) => deletePartOrder(Number(id)),
};

const PartOrderListPage = () => {
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
    initFormData: () => initForms.partOrder,
    api,
    keyField: "poNo",
  });

  // 옵션 로딩
  const [buyerOptions, setBuyerOptions] = useState([]); // {value: buyerNo, label: buyerComp, extra: buyerAddr}
  const [partOptions, setPartOptions] = useState([]); // {value: partNo, label: partName, extra: partPrice}

  useEffect(() => {
    (async () => {
      try {
        const buyers = await getBuyerCustomer(); // [{buyerNo,buyerComp,buyerAddr,...}]
        setBuyerOptions(
          (buyers ?? []).map((b) => ({
            value: Number(b.buyerNo),
            label: b.buyerComp,
            extra: b.buyerAddr,
          }))
        );
        const parts = await getPartList(); // [{partNo,partName,partPrice,...}]
        setPartOptions(
          (parts ?? []).map((p) => ({
            value: Number(p.partNo),
            label: p.partName,
            extra: Number(p.partPrice ?? 0),
          }))
        );
      } catch (e) {
        console.error(e);
        setBuyerOptions([]);
        setPartOptions([]);
      }
    })();
  }, []);

  // 빠른 Lookup Map
  const buyerMap = useMemo(() => {
    const m = new Map();
    buyerOptions.forEach((b) => m.set(b.value, b));
    return m;
  }, [buyerOptions]);

  const partMap = useMemo(() => {
    const m = new Map();
    partOptions.forEach((p) => m.set(p.value, p));
    return m;
  }, [partOptions]);

  // 선택 핸들러: 구매처
  const onSelectBuyer = (buyerNo) => {
    const opt = buyerMap.get(buyerNo);
    setFormData((prev) => ({
      ...prev,
      buyerNo: buyerNo || "",
      buyerAddr: opt?.extra ?? "",
    }));
  };

  // 선택 핸들러: 부품
  const onSelectPart = (partNo) => {
    const opt = partMap.get(partNo);
    setFormData((prev) => ({
      ...prev,
      partNo: partNo || "",
      poPrice: opt?.extra ?? "", // 자동 입력
    }));
  };

  // 최초 목록 로딩
  useEffect(() => {
    (async () => {
      try {
        const list = await api.getAll();
        setItems(Array.isArray(list) ? list : []);
      } catch (e) {
        console.error(e);
        setItems([]);
      }
    })();
  }, [setItems]);

  const refetch = async () => {
    const list = await api.getAll();
    setItems(Array.isArray(list) ? list : []);
  };

  // 모달 열릴 때, 편집 모드면 기존 데이터 반영 (response는 buyerComp/partName이라 역매핑 필요할 수 있음)
  useEffect(() => {
    if (!isEditOpen || !selectedItem) return;

    // buyerComp -> buyerNo 역매핑
    const buyerNo =
      buyerOptions.find((b) => b.label === selectedItem.buyerComp)?.value ?? "";

    // partName -> partNo 역매핑
    const partNo =
      partOptions.find((p) => p.label === selectedItem.partName)?.value ?? "";

    setFormData((prev) => ({
      ...prev,
      poNo: selectedItem.poNo,
      buyerNo,
      buyerAddr: selectedItem.buyerAddr ?? buyerMap.get(buyerNo)?.extra ?? "",
      partNo,
      poPrice:
        selectedItem.poPrice ??
        partMap.get(partNo)?.extra ??
        "",
      poQty: selectedItem.poQty ?? "",
      poState: selectedItem.poState ?? "입고대기",
      poDate: selectedItem.poDate ?? "",
    }));
  }, [
    isEditOpen,
    selectedItem,
    setFormData,
    buyerOptions,
    partOptions,
    buyerMap,
    partMap,
  ]);

  // 기본 submit 막기
  const stop = (e) => {
    e?.preventDefault?.();
    e?.stopPropagation?.();
  };

  const doCreate = async (e) => {
    stop(e);
    const ok = await handleCreate();
    if (ok) {
      await refetch();
      closeCreate();
      alert("등록 완료!");
    } else {
      alert("등록 실패");
    }
  };

  const doUpdate = async (e) => {
    stop(e);
    const ok = await handleUpdate();
    if (ok) {
      await refetch();
      closeEdit();
      alert("수정 완료!");
    } else {
      alert("수정 실패");
    }
  };

  const doDelete = async (e) => {
    stop(e);
    const ok = await handleDelete();
    if (ok !== false) {
      await refetch();
      closeEdit();
      alert("삭제 완료!");
    } else {
      alert("삭제 실패");
    }
  };

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="< 이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>부품 발주</h2>

      <table>
        <thead>
          <tr>
            {partOrderArrays.map((c) => (
              <th key={c.id}>{c.content}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {Array.isArray(items) && items.length > 0 ? (
            items.map((row) => (
              <tr key={row.poNo} className="row" onClick={() => openEdit(row)}>
                {partOrderArrays.map((c) => (
                  <td key={`${row.poNo}-${c.id}`}>
                    {row[c.clmn] ?? ""}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={partOrderArrays.length} style={{ textAlign: "center" }}>
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <br />
      <ButtonComponent onClick={openCreate} text="발주 등록" cln="submit" />

      {/* 등록 모달 */}
      <ModalComponent
        isOpen={isCreateOpen}
        onClose={closeCreate}
        title="발주 등록"
        onConfirm={doCreate}
      >
        <form onSubmit={stop}>
          <PartOrderForm
            formData={formData}
            onChange={handleChange}
            buyerOptions={buyerOptions}
            partOptions={partOptions}
            onSelectBuyer={onSelectBuyer}
            onSelectPart={onSelectPart}
          />
          <div className="btn-wrapper" style={{ marginTop: 12 }}>
            <ButtonComponent text="등록" onClick={doCreate} cln="submit" />
          </div>
        </form>
      </ModalComponent>

      {/* 수정/삭제 모달 */}
      <ModalComponent
        isOpen={isEditOpen}
        onClose={closeEdit}
        title="발주 수정/삭제"
        onConfirm={doUpdate}
      >
        {selectedItem && (
          <form onSubmit={stop}>
            <PartOrderForm
              formData={formData}
              onChange={handleChange}
              buyerOptions={buyerOptions}
              partOptions={partOptions}
              onSelectBuyer={onSelectBuyer}
              onSelectPart={onSelectPart}
            />
            <div className="btn-wrapper" style={{ marginTop: 12 }}>
              <ButtonComponent text="수정" onClick={doUpdate} cln="fixbtn" />
              <ButtonComponent text="삭제" onClick={doDelete} cln="delbtn" />
            </div>
          </form>
        )}
      </ModalComponent>
    </div>
  );
};

export default PartOrderListPage;
