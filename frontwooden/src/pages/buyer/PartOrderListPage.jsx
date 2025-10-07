// src/pages/buyer/PartOrderListPage.jsx
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
import { getPartByBuyer } from "../../api/PartListAPI"; // ✅ 추가 (구매처→부품 자동 세팅용)

const api = {
  getAll: getPartOrders,
  create: (fd) =>
    createPartOrder({
      poNo: fd.poNo ? Number(fd.poNo) : undefined,
      buyerNo: Number(fd.buyerNo),
      partNo: Number(fd.partNo),          // ✅ 자동 세팅된 값 전송
      poQty: Number(fd.poQty),
      poPrice: Number(fd.poPrice),        // ✅ 자동 세팅된 값 전송
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

  // ✅ 구매처 옵션만 유지 (부품 옵션/드롭다운 제거)
  const [buyerOptions, setBuyerOptions] = useState([]); // {value: buyerNo, label: buyerComp, addr: buyerAddr}

  useEffect(() => {
    (async () => {
      try {
        const buyers = await getBuyerCustomer(); // [{buyerNo,buyerComp,buyerAddr,...}]
        setBuyerOptions(
          (buyers ?? []).map((b) => ({
            value: Number(b.buyerNo),
            label: b.buyerComp,
            addr: b.buyerAddr,
          }))
        );
      } catch (e) {
        console.error(e);
        setBuyerOptions([]);
      }
    })();
  }, []);

  // 빠른 Lookup Map (구매처)
  const buyerMap = useMemo(() => {
    const m = new Map();
    buyerOptions.forEach((b) => m.set(b.value, b));
    return m;
  }, [buyerOptions]);

  // ✅ 구매처 선택 → 부품/단가/주소 자동 세팅
  const onSelectBuyer = async (buyerNo) => {
    const opt = buyerMap.get(buyerNo);

    // 우선 구매처 정보 반영
    setFormData((prev) => ({
      ...prev,
      buyerNo: buyerNo || "",
      buyerAddr: opt?.addr ?? "",
    }));

    // 구매처와 1:1인 부품 자동 조회
    try {
      if (!buyerNo) {
        setFormData((prev) => ({
          ...prev,
          partNo: "",
          partName: "",
          poPrice: "",
        }));
        return;
      }

      const part = await getPartByBuyer(buyerNo); // { partNo, partName, partPrice, ... }
      setFormData((prev) => ({
        ...prev,
        partNo: Number(part.partNo),
        partName: part.partName,             // 폼에서 readOnly로 표기
        poPrice: Number(part.partPrice ?? 0) // 단가 자동
      }));
    } catch (e) {
      console.error("getPartByBuyer failed", e);
      // 적절히 초기화
      setFormData((prev) => ({
        ...prev,
        partNo: "",
        partName: "",
        poPrice: "",
      }));
      alert("해당 구매처에 연결된 부품을 찾을 수 없습니다.");
    }
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

  // 편집 모달 열릴 때 기존 데이터 → 폼 반영 (buyerComp/partName 등 표시 컬럼을 역매핑)
  useEffect(() => {
    if (!isEditOpen || !selectedItem) return;

    // buyerComp -> buyerNo 역매핑
    const buyerNo =
      buyerOptions.find((b) => b.label === selectedItem.buyerComp)?.value ?? "";

    setFormData((prev) => ({
      ...prev,
      poNo: selectedItem.poNo,
      buyerNo,
      buyerAddr: selectedItem.buyerAddr ?? buyerMap.get(buyerNo)?.addr ?? "",
      partNo: "",                   // 아래 onSelectBuyer에서 자동 세팅될 예정
      partName: selectedItem.partName ?? "",
      poPrice: selectedItem.poPrice ?? "",
      poQty: selectedItem.poQty ?? "",
      poState: selectedItem.poState ?? "입고대기",
      poDate: selectedItem.poDate ?? "",
    }));

    // ✅ 편집 모달에서도 구매처 기반 부품/단가 동기화
    if (buyerNo) {
      onSelectBuyer(buyerNo);
    }
  }, [isEditOpen, selectedItem, setFormData, buyerOptions, buyerMap]); // onSelectBuyer는 안정적으로 호출

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
                  <td key={`${row.poNo}-${c.id}`}>{row[c.clmn] ?? ""}</td>
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
            onSelectBuyer={onSelectBuyer}   // ✅ 부품 자동 세팅은 여기서
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
              onSelectBuyer={onSelectBuyer} // ✅ 편집에서도 유지
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
