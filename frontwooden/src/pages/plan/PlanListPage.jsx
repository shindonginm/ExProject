import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCRUD } from "../../hook/useCRUD.jsx";

import ModalComponent from "../../components/ModalComponent";
import ButtonComponent from "../../components/ButtonComponent.jsx";
import BackButtonComponent from "../../components/BackButtonComponent.jsx";

import PlanListForm from "../../form/plan/PlanListForm.jsx";
import { PlanListArrays } from "../../arrays/PlanArrays"; 
import { initForms } from "../../arrays/TableArrays.jsx";

import { getPlanList, createPlan, updatePlan, deletePlan } from "../../api/PlanListAPI";
import { getItemList } from "../../api/ItemListAPI";

const api = {
  getAll: getPlanList,
  create: createPlan,  // 내부에서 formData 그대로 보낼 경우 itemNo, planState 등 필드 일치 필요
  update: updatePlan,
  delete: deletePlan,
};

const PlanListPage = () => {
  const navigate = useNavigate();

  const {
    items: plans,
    setItems: setPlans,
    formData,
    setFormData,
    handleChange,
    handleCreate,
    handleUpdate,
    handleDelete,
    openCreate,
    openEdit,
    isCreateOpen,
    isEditOpen,
    closeCreate,
    closeEdit,
    selectedItem,
  } = useCRUD({
    initFormData: () => ({ ...initForms.plan }),
    api,
    keyField: "planNo",
  });

  // 상품 드롭다운 옵션
  const [itemOptions, setItemOptions] = useState([]);

  // 목록 로딩
  useEffect(() => {
    (async () => {
      try {
        const data = await getPlanList();
        setPlans(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error("getPlanList failed:", e);
        setPlans([]);
      }
    })();
  }, [setPlans]);

  // 상품 옵션 로딩
  useEffect(() => {
    (async () => {
      try {
        const items = await getItemList();
        const options = (items ?? []).map(it => ({
          value: Number(it.itemNo),
          label: it.itemName,
        }));
        setItemOptions(options);
      } catch (e) {
        console.error("getItemList failed:", e);
        setItemOptions([]);
      }
    })();
  }, []);

  // 편집 모달 열릴 때에만 폼 초기값 세팅 (덮어쓰기 방지)
  useEffect(() => {
    if (!isEditOpen || !selectedItem) return;

    setFormData(prev => ({
      ...prev,
      planNo: selectedItem.planNo ?? prev.planNo,
      planQty: Number(selectedItem.planQty ?? prev.planQty ?? 1),
      planState: ["진행중", "생산완료"].includes(selectedItem.planState)
        ? selectedItem.planState
        : "진행중",
      planStartDate: selectedItem.planStartDate ?? prev.planStartDate,
      planEndDate: selectedItem.planEndDate ?? prev.planEndDate,
      // itemName → itemNo 역매핑
      itemNo:
        prev.itemNo ||
        (itemOptions.find(o => o.label === selectedItem.itemName)?.value ?? ""),
      // 표시용
      itemName: selectedItem.itemName ?? prev.itemName,
    }));
  }, [isEditOpen, selectedItem, itemOptions, setFormData]);

  // 기본 submit/버블 방지
  const stop = (e) => {
    e?.preventDefault?.();
    e?.stopPropagation?.();
  };

  // 재조회
  const refetch = async () => {
    try {
      const fresh = await getPlanList();
      setPlans(Array.isArray(fresh) ? fresh : []);
    } catch (e) {
      console.error("refetch failed:", e);
      setPlans([]);
    }
  };

  // 등록/수정/삭제
  const doCreate = async (e) => {
    stop(e);
    const ok = await handleCreate();
    if (ok) {
      await refetch();
      closeCreate();
    }
  };

  const doUpdate = async (e) => {
    stop(e);
    const ok = await handleUpdate();
    if (ok) {
      await refetch();
      closeEdit();
    }
  };

  const doDelete = async (e) => {
    stop(e);
    const ok = await handleDelete();
    if (ok) {
      await refetch();
      closeEdit();
    }
  };

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="< 이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>생산 리스트</h2>

      {/* 테이블 */}
      <table>
        <thead>
          <tr>
            {PlanListArrays.map((col) => (
              <th key={col.id}>{col.content}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {Array.isArray(plans) && plans.length > 0 ? (
            plans.map((row) => (
              <tr key={row.planNo} className="row" onClick={() => openEdit(row)}>
                {PlanListArrays.map((col) => (
                  <td key={col.id}>{row[col.clmn]}</td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={PlanListArrays.length} style={{ textAlign: "center" }}>
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <br />
      <ButtonComponent onClick={openCreate} text={"생산 등록"} cln="submit" />

      {/* 등록 모달 */}
      <ModalComponent
        isOpen={isCreateOpen}
        onClose={closeCreate}
        title="생산 등록"
        onConfirm={doCreate}
      >
        <form onSubmit={stop}>
          <PlanListForm
            formData={formData}
            onChange={handleChange}
            itemList={itemOptions} 
          />
          <div className="btn-wrapper">
            <ButtonComponent text={"등록"} onClick={doCreate} cln="submit" />
          </div>
        </form>
      </ModalComponent>

      {/* 수정/삭제 모달 */}
      <ModalComponent
        isOpen={isEditOpen}
        onClose={closeEdit}
        title="생산 수정/삭제"
        onConfirm={doUpdate}
      >
        {selectedItem && (
          <form onSubmit={stop}>
            <PlanListForm
              formData={formData}
              onChange={handleChange}
              itemList={itemOptions} 
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

export default PlanListPage;
