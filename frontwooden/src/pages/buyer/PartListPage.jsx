// src/pages/buyer/PartListPage.jsx
import { useEffect } from "react";
import {
  getPartList,
  createPartList,
  updatePartList,
  deletePartList,
} from "../../api/PartListAPI";
import { useCRUD } from "../../hook/useCRUD";
import ModalComponent from "../../components/ModalComponent";
import PartListForm from "../../form/buyer/PartListForm";
import ButtonComponent from "../../components/ButtonComponent";
import BackButtonComponent from "../../components/BackButtonComponent";
import { initForms } from "../../arrays/TableArrays";
import { useNavigate } from "react-router-dom";
import { PartListArray } from "../../arrays/PartListArrays";
import axios_api from "../../api/axios"; // ✅ axios_api 사용 (baseURL 일관)
                                     // (직접 axios+BASE_URL 혼용 지양)

const api = {
  getAll: getPartList,
  create: createPartList,
  update: updatePartList,   // ✅ formData 하나만 받도록 맞춤
  delete: deletePartList,
};

const PartListPage = () => {
  const navigate = useNavigate();

  const {
    items: partList,
    setItems: setPartList,
    formData,
    setFormData,
    handleChange,
    handleCreate,
    handleUpdate,
    handleDelete,
    openEdit,
    openCreate,
    isCreateOpen,
    isEditOpen,
    closeCreate,
    closeEdit,
    selectedItem,
    customer,
    setCustomer,
    setCustomerId,
  } = useCRUD({
    initFormData: () => initForms.part,
    api,
    keyField: "partNo",
  });

  // 구매처(셀렉트 옵션) + 목록 로딩
  useEffect(() => {
    (async () => {
      // ✅ 구매처: /api/buyer/buyercustomer
      const res = await axios_api.get("/api/buyer/buyercustomer");
      const options = res.data.map((t) => ({
        label: t.buyerComp,  // 구매처명
        value: t.buyerNo,    // buyerNo (FK)
      }));
      setCustomer(options);

      // 목록
      const data = await getPartList();
      setPartList(data);
    })();
  }, [setCustomer, setPartList]);

  // 수정모달 열릴 때 buyerComp(이름) → buyerNo 역매핑
  useEffect(() => {
    if (!isEditOpen || !selectedItem || !customer?.length) return;
    const buyerNo = customer.find((c) => c.label === selectedItem.buyerComp)?.value ?? "";
    setFormData((prev) => ({
      ...prev,
      partNo: selectedItem.partNo,
      partName: selectedItem.partName,
      partCode: selectedItem.partCode,
      partSpec: selectedItem.partSpec,
      partPrice: selectedItem.partPrice,
      buyerComp: buyerNo, // ✅ select 값으로 세팅 (buyerNo)
    }));
  }, [isEditOpen, selectedItem, customer, setFormData]);

  // 기본 submit/버블 방지용
  const stop = (e) => {
    e?.preventDefault?.();
    e?.stopPropagation?.();
  };

  const refetch = async () => setPartList(await getPartList());

  const doCreate = async (e) => { stop(e); await handleCreate(); await refetch(); closeCreate(); };
  const doUpdate = async (e) => { stop(e); await handleUpdate(); await refetch(); closeEdit(); };
  const doDelete = async (e) => { stop(e); await handleDelete(); await refetch(); closeEdit(); };

  return (
    <div className="page-wrapper">
      <BackButtonComponent
        text="< 이전페이지"
        onClick={() => navigate(-1)}
      />
      <h2 style={{ textAlign: "center" }}>부품리스트</h2>

      {/* 테이블 */}
      <table>
        <thead>
          <tr>
            {PartListArray.map((col) => (
              <th key={col.id}>{col.content}</th>
            ))}
          </tr>
        </thead>

        <tbody>
          {partList?.length ? (
            partList.map((part) => (
              <tr key={part.partNo} className="row">
                <td>{part.partNo}</td>

                {/* 부품명 클릭 시 수정 모달 */}
                <td
                  style={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}
                  onClick={() => openEdit(part)}
                >
                  {part.partName}
                </td>

                <td>{part.buyerComp}</td>
                <td>{part.partCode}</td>
                <td>{part.partSpec}</td>
                <td>{part.partPrice}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={PartListArray.length} style={{ textAlign: "center" }}>
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <br />
      {/* ✅ 라벨 정정: "주문 등록" → "부품 등록" */}
      <ButtonComponent onClick={openCreate} text={"부품 등록"} cln="submit" />

      {/* 등록 모달 */}
      <ModalComponent
        isOpen={isCreateOpen}
        onClose={closeCreate}
        title="부품 등록"
        onConfirm={doCreate}
      >
        <form onSubmit={stop}>
          <PartListForm
            formData={formData}
            onChange={handleChange}
            customer={customer}
            setCustomerId={setCustomerId}
          >
            <ButtonComponent text={"등록"} onClick={doCreate} cln="submit" />
          </PartListForm>
        </form>
      </ModalComponent>

      {/* 수정/삭제 모달 */}
      <ModalComponent
        isOpen={isEditOpen}
        onClose={closeEdit}
        title="부품 수정/삭제"
        onConfirm={doUpdate}
      >
        {selectedItem && (
          <form onSubmit={stop}>
            <PartListForm
              formData={formData}
              onChange={handleChange}
              customer={customer}
              setCustomerId={setCustomerId}
            >
              <div className="btn-wrapper">
                <ButtonComponent text="수정" onClick={doUpdate} cln="fixbtn" />
                <ButtonComponent text="삭제" onClick={doDelete} cln="delbtn" />
              </div>
            </PartListForm>
          </form>
        )}
      </ModalComponent>
    </div>
  );
};

export default PartListPage;
