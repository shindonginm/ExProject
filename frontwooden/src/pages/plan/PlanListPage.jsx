import { useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useCRUD } from "../../hook/useCRUD.jsx";

import ModalComponent from "../../components/ModalComponent";
import ButtonComponent from "../../components/ButtonComponent.jsx";
import BackButtonComponent from "../../components/BackButtonComponent.jsx";

import PlanListForm from "../../form/plan/PlanListForm.jsx";
import { PlanListArrays, initPlanForm } from "../../arrays/PlanArrays";
import { getPlanList, createPlan, updatePlan, deletePlan } from "../../api/PlanListAPI";
import { getItemList } from "../../api/ItemListAPI";

const api = {
  getAll: getPlanList,
  create: createPlan,
  update: updatePlan,
  delete: deletePlan,
};

const PlanListPage = () => {
  const navigate = useNavigate();

  const {
    items: plans,
    setItems: setPlans,
    formData,
    setFormData,       // ✅ 덮어쓰기 방지를 위해 필요
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
    itemList,
    setItemList,
  } = useCRUD({
    initFormData: () => initPlanForm(),
    api,
    keyField: "planNo",
  });

  // 목록 로딩
  useEffect(() => {
    (async () => {
      const data = await getPlanList();
      setPlans(data);
    })();
  }, [setPlans]);

  // 상품 옵션 로딩
  useEffect(() => {
    (async () => {
      const items = await getItemList();
      const options = items.map((it) => ({
        value: Number(it.itemNo),
        label: it.itemName,
      }));
      setItemList(options);
    })();
  }, [setItemList]);

  // 편집 모달이 열릴 때에만 초기값 세팅 (❗️덮어쓰기 방지)
  useEffect(() => {
    if (!isEditOpen || !selectedItem) return;

    // selectedItem에서 폼으로 필요한 필드만 추출
    setFormData((prev) => ({
      ...prev,
      planNo: selectedItem.planNo ?? prev.planNo,
      planName: selectedItem.planName ?? prev.planName,
      planQty: selectedItem.planQty ?? prev.planQty,
      planState: ["생산중", "완료"].includes(selectedItem.planState)
        ? selectedItem.planState
        : "생산중",
      // itemName → itemNo 역매핑
      itemNo:
        prev.itemNo ||
        (itemList.find((o) => o.label === selectedItem.itemName)?.value ?? ""),
    }));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isEditOpen, selectedItem, itemList, setFormData]);

  // ── 공통: 기본 제출/버블 막기 ──
  const stop = (e) => {
    if (e?.preventDefault) e.preventDefault();
    if (e?.stopPropagation) e.stopPropagation();
  };

  const refetchPlans = async () => {
    const fresh = await getPlanList();
    setPlans(fresh);
  };

  const doCreate = async (e) => {
    stop(e);
    await handleCreate();
    await refetchPlans();
    closeCreate();
  };

  const doUpdate = async (e) => {
    stop(e);
    await handleUpdate();
    await refetchPlans();
    closeEdit();
  };

  const doDelete = async (e) => {
    stop(e);
    await handleDelete();
    await refetchPlans();
    closeEdit();
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
          {plans && plans.length > 0 ? (
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

      {/* 등록 버튼 */}
      <br />
      {/* ❗ ButtonComponent는 공용이라 못 바꾼다고 하셨으니 type은 건드리지 않고 onClick만 사용 */}
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
            itemList={itemList}
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
              itemList={itemList}
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
