// src/pages/order/SellerCustomerListPage.jsx
import { useEffect } from "react";
import { useCRUD } from "../../hook/useCRUD";
import {
  getCustomer,
  createCustomer,
  updateCustomer,
  deleteCustomer,
} from "../../api/CustomerAPI";
import ModalComponent from "../../components/ModalComponent";
import SellCustomerForm from "../../form/order/SellCustomerForm";
import ButtonComponent from "../../components/ButtonComponent";
import BackButtonComponent from "../../components/BackButtonComponent";
import { initForms } from "../../arrays/TableArrays";
import { sellCustomerArray } from "../../arrays/SellerCustomerArray";
import { useNavigate } from "react-router-dom";

const api = {
  getAll: getCustomer,
  create: createCustomer,
  // 🔧 useCRUD.handleUpdate가 formData 통째로 넘기니까 여기서 래핑
  update: (formData) => updateCustomer(formData.cusNo, formData),
  delete: (cusNo) => deleteCustomer(cusNo),
};

const SellerCustomerListPage = () => {
  const navigate = useNavigate();

  const {
    items,            // 목록
    setItems,
    formData,         // 폼 상태
    handleChange,     // input onChange
    handleCreate,     // 등록
    handleUpdate,     // ✅ 수정 버튼에 연결
    handleDelete,     // ✅ 삭제 버튼에 연결
    openCreate,       // 등록 모달 열기
    openEdit,         // 수정 모달 열기 (행 클릭)
    closeCreate,
    closeEdit,
    isCreateOpen,
    isEditOpen,
    selectedItem,
  } = useCRUD({
    initFormData: () => initForms.sellerCustomer,
    api,
    keyField: "cusNo",
  });

  useEffect(() => {
    (async () => {
      const data = await getCustomer();
      setItems(data);
    })();
  }, [setItems]);

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="<  이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>판매거래처 리스트</h2>

      {/* 테이블 */}
      <table>
        <thead>
          <tr>
            {sellCustomerArray.map(col => <th key={col.id}>{col.content}</th>)}
          </tr>
        </thead>
        <tbody>
          {items && items.length > 0 ? (
            items.map(row => (
              <tr key={row.cusNo} className="row" onClick={() => openEdit(row)}>
                {sellCustomerArray.map(col => (
                  <td
                    key={col.id}
                    style={col.clmn === "cusComp" ? { color: "blue", textDecoration: "underline", cursor: "pointer" } : {}}
                  >
                    {row[col.clmn]}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={sellCustomerArray.length} style={{ textAlign: "center" }}>
                데이터가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <br />
      <ButtonComponent onClick={openCreate} text="거래처 등록" cln="submit" />

      {/* 등록 모달 */}
      <ModalComponent
        isOpen={isCreateOpen}
        onClose={closeCreate}
        title="거래처 등록"
        onConfirm={handleCreate}
      >
        <SellCustomerForm formData={formData} onChange={handleChange} />
        <div className="btn-wrapper">
          <ButtonComponent text="등록" onClick={handleCreate} cln="submit" />
        </div>
      </ModalComponent>

      {/* 수정/삭제 모달: 여기서 '완료(수정)' 버튼 + '삭제' 버튼 */}
      <ModalComponent
        isOpen={isEditOpen}
        onClose={closeEdit}
        title="거래처 수정/삭제"
        onConfirm={handleUpdate}
      >
        {selectedItem && (
          <>
            <SellCustomerForm formData={formData} onChange={handleChange} />
            <div className="btn-wrapper">
              {/* 완료 버튼 = 수정 */}
              <ButtonComponent text="완료" onClick={handleUpdate} cln="fixbtn" />
              {/* 삭제 버튼 */}
              <ButtonComponent text="삭제" onClick={handleDelete} cln="delbtn" />
            </div>
          </>
        )}
      </ModalComponent>
    </div>
  );
};

export default SellerCustomerListPage;
