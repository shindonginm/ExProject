import { useEffect } from "react";
import {
  getOrderList,
  createOrderList,
  updateOrderList,
  deleteOrderList,
} from "../../api/OrderListAPI";
import { useCRUD } from "../../hook/useCRUD";
import ModalComponent from "../../components/ModalComponent";
import OrderListForm from "../../form/order/OrderListForm";
import ButtonComponent from "../../components/ButtonComponent";
import BackButtonComponent from "../../components/BackButtonComponent";
import { initForms } from "../../arrays/TableArrays";
import { useNavigate } from "react-router-dom";
import { OrderListArr } from "../../arrays/OrderListArrays";
import axios from "axios";
import { BASE_URL } from "../../api/config";

const api = {
  getAll: getOrderList,
  create: createOrderList,
  update: (fd) => {
    const payload = {
      customerId: Number(fd.customerId),
      itemId: Number(fd.itemId),
      orderQty: Number(fd.orderQty),
      orderPrice: Number(fd.orderPrice),
      orderState: fd.orderState,
      orderDeliState: fd.orderDeliState,
      orderDate: fd.orderDate,        // YYYY-MM-DD
      deliveryDate: fd.deliveryDate,  // YYYY-MM-DD
      cusAddr: fd.cusAddr,
    };
    return updateOrderList(fd.orderNo, payload);
  },
  delete: (orderNo) => deleteOrderList(orderNo),
};

const OrderListPage = () => {
  const navigate = useNavigate();

  const {
    items: orderList,
    setItems: setOrderList,
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
    itemList,
    setItemList,
    setCustomerId,
    setItemListId,
  } = useCRUD({
    initFormData: () => initForms.orderList,
    api,
    keyField: "orderNo",
  });

  // 이름(label) 과 ID(value) 매핑 도우미
  const labelToId = (list, label) => {
    const f = list.find(x => x.label === label);
    return f ? f.value : "";
  };

  const handleRowClick = (row) => {
    openEdit(row); // 기존 데이터 세팅
    // 이름만 있던 필드를 ID 로 채워줌
    const cid = labelToId(customer, row.cusComp);
    const iid = labelToId(itemList, row.itemName);

    setFormData(prev => ({
      ...prev,
      customerId: cid,
      itemId: iid,

      // 숫자형 필드는 안전하게 숫자로
      orderQty: Number(row.orderQty || 0),
      orderPrice: Number(row.orderPrice || 0),
    }));
  };


  useEffect(() => {
    // 고객 목록 전체 조회
    axios.get(`${BASE_URL}/order/sellercustomer`).then((res) => {
      const options = res.data.map((temp) => ({
        label: temp.cusComp, // 판매거래처명
        value: temp.cusNo, // PK
      }));
      console.log(options)
      setCustomer(options);
    })

    // 상품 목록 전체 조회
    axios.get(`${BASE_URL}/plan/itemlist`).then((res) => {
      const options = res.data.map((temp) => ({
        label: temp.itemName, // 상품명
        value: temp.itemNo, // PK
        price: temp.itemPrice // 단가
      }));
      setItemList(options);
    })

    const fetchData = async () => {
      const data = await getOrderList();
      setOrderList(data);
    };
    fetchData();
  }, [setCustomer, setItemList]);

  return (
    <div className="page-wrapper">
      <BackButtonComponent
        text="< &nbsp;이전페이지"
        onClick={() => navigate(-1)}
      />
      <h2 style={{ textAlign: "center" }}>상품주문서</h2>

      {/* 테이블 */}
      <table>
        <thead>
          <tr>
            {OrderListArr.map((col) => (
              <th key={col.id}>{col.content}</th>
            ))}
          </tr>
        </thead>

        <tbody>
          {orderList && orderList.length > 0 ? (
            orderList.map((order) => (
              <tr key={order.orderNo} className="row">
                <td>{order.orderNo}</td>
                <td>{order.orderDate}</td>
                {/* 판매처명 클릭 시에만 수정 모달 열기 */}
                <td
                  style={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}
                  onClick={() => openEdit(order)}>
                    {order.cusComp}
                </td>
                <td>{order.itemName}</td>
                <td>{order.orderQty}</td>
                <td>{order.orderPrice.toLocaleString()} 원</td>
                <td>{order.orderState}</td>
                <td>{order.orderDeliState}</td>
                <td>{order.deliveryDate}</td>
                <td>{order.cusAddr}</td>
                <td>{(order.orderQty * order.orderPrice).toLocaleString()} 원</td>
              </tr>
              )) 
              ) : (

              <tr>
                <td colSpan={OrderListArr.length} style={{ textAlign: "center" }}>
                  데이터가 없습니다.
                </td>
              </tr>
              )}
        </tbody>
      </table>

      {/* 등록 버튼 */}
      <br />
      <ButtonComponent onClick={openCreate} text={"주문 등록"} cln="submit" />

      {/* 등록 모달 */}
      <ModalComponent
        isOpen={isCreateOpen}
        onClose={closeCreate}
        title="상품주문서 등록"
        onConfirm={handleCreate}
      >
        <OrderListForm
          formData={formData}
          onChange={handleChange}
          customer={customer}
          itemList={itemList}
          setCustomerId={setCustomerId}
          setItemListId={setItemListId}
        >
          <ButtonComponent text={"등록"} onClick={handleCreate} cln="submit" />
        </OrderListForm>
      </ModalComponent>

      {/* 수정/삭제 모달 */}
      <ModalComponent
        isOpen={isEditOpen}
        onClose={closeEdit}
        title="상품주문서 수정/삭제"
        onConfirm={handleUpdate}
      >
        {selectedItem && (
          <OrderListForm
            formData={formData}
            onChange={handleChange}
            customer={customer}
            itemList={itemList}
            setCustomerId={setCustomerId}
            setItemListId={setItemListId}
          >
            <div className="btn-wrapper">
              <ButtonComponent text="수정" onClick={handleUpdate} cln="fixbtn" />
              <ButtonComponent text="삭제" onClick={handleDelete} cln="delbtn" />
            </div>
          </OrderListForm>
        )}
      </ModalComponent>
    </div>
  );
};

export default OrderListPage;
