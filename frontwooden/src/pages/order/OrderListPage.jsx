import axios from "axios";
import { useEffect } from "react";
import { getOrderList, createOrderList, updateOrderList, deleteOrderList } from "../../api/OrderListAPI";
import { useCRUD } from "../../hook/useCRUD";
import { initForms } from "../../arrays/TableArrays";
import { useNavigate } from "react-router-dom";
import { OrderListArr } from "../../arrays/OrderListArrays";
import { BASE_URL } from "../../api/config";
import { getCustomer } from "../../api/CustomerAPI";
import { getItemList } from "../../api/ItemListAPI";
import { updateOrderStatus } from "../../api/OrderListAPI";
import ModalComponent from "../../components/ModalComponent";
import OrderListForm from "../../form/order/OrderListForm";
import ButtonComponent from "../../components/ButtonComponent";
import BackButtonComponent from "../../components/BackButtonComponent";
import InlineSelectCell from "../../components/InlineSelectCell";

const OrderListPage = () => {
  const navigate = useNavigate();

// api.update는 DTO 스펙에 맞춰 body 정리해서 전송
const api = {
  getAll: async () => {
    const data = await getOrderList();
    return data.map(normalizeOrder).filter(o => !isCompleted(o));
  },
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

const {
  items: orderList,
  setItems: setOrderList,
  formData, setFormData,

  handleChange, handleCreate, handleUpdate, handleDelete,
  openEdit, openCreate, closeCreate, closeEdit,
  isCreateOpen, isEditOpen, selectedItem,

  // 드롭다운 데이터
  customer, itemList, setCustomer, setItemList,
  setCustomerId, setItemListId,
} = useCRUD({
  initFormData: () => initForms.orderList,
  api,
  keyField: "orderNo",
});

const isCompleted = o =>
  o?.orderState === "승인완료" && o?.orderDeliState === "납품완료";

const normalizeOrder = (o) => {
  const qty = Number(o.orderQty ?? 0);
  const price = Number(o.orderPrice ?? 0);
  const serverTotal = Number(o.totalPrice ?? 0);
  
  return {
    ...o,
    orderQty: qty,
    orderPrice: price,
    totalPrice: serverTotal > 0 ? serverTotal : qty * price,
  };
};

// 드롭다운 소스 로드
useEffect(() => {
  (async () => {
    const [orders, customers, items] = await Promise.all([
      getOrderList(),
      getCustomer(),
      getItemList(),
    ]);

    const normalized = orders.map(o => {
      const qty = Number(o.orderQty ?? 0);
      const price = Number(o.orderPrice ?? 0);
      const serverTotal = Number(o.totalPrice ?? 0);
      return {
        ...o,
        orderQty: qty,
        orderPrice: price,
        totalPrice: serverTotal > 0 ? serverTotal : qty * price, // 총 금액 표시 핵심
      };
    });

    setOrderList(normalized.filter(o => !isCompleted(o)));  // 완료 리스트는 제외
    setCustomer(customers.map(c => ({ value: c.cusNo, label: c.cusComp })));
    setItemList(items.map(i => ({ value: i.itemNo, label: i.itemName, price: i.itemPrice })));
  })();
}, [setOrderList, setCustomer, setItemList]);


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
      // 총 금액 표시
      totalPrice: Number(row.orderQty || 0) * Number(row.orderPrice || 0),
    }));
  };

  const reloadOrders = async () => {
  const orders = await getOrderList();
  const normalized = orders.map(o => {
    const qty = Number(o.orderQty ?? 0);
    const price = Number(o.orderPrice ?? 0);
    const serverTotal = Number(o.totalPrice ?? 0);
    return { ...o, orderQty: qty, orderPrice: price, totalPrice: serverTotal > 0 ? serverTotal : qty * price };
  });
  setOrderList(normalized.filter(o => !isCompleted(o)));
};

  // 등록 완료 후 새로고침
  const handleCreateAndRefresh = async () => {
    const ok = await handleCreate();
    if (ok) {
      await reloadOrders();
      closeCreate();
    }
  };
  
  // 수정 완료 후 새로고침
  const handleUpdateAndRefresh = async () => {
    const ok = await handleUpdate(); // useCRUD가 true/false 반환
    if (ok) {
      await reloadOrders();
      closeEdit();
    }
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
    };
    fetchData();
  }, [setCustomer, setItemList]);

  // state 값 onPatch 호출
  const ORDER_STATE_OPTS = ["승인대기", "승인완료"];
  const DELI_STATE_OPTS = ["납품대기", "납품완료"];

  // 주문상태 변경 및 이동
  const onPatchOrderState = async (id, next) => {

    // 업데이트
    setOrderList(prev => prev.map(r => r.orderNo === id ? { ...r, orderState: next } : r));
    const updated = await updateOrderStatus(id, { orderState: next });

    // 둘 다 완료면 주문완료현황으로 이동
    if (isCompleted(updated)) {
      setOrderList(prev => prev.filter(r => r.orderNo !== id)); // 두 값 완료시 즉시 제거
      navigate("/order/orderreceive");
    } else {
      setOrderList(prev => prev.map(r => r.orderNo === id ? { ...r, ...updated } : r));
    }
  };

  // 납품상태 변경 및 이동
  const onPatchDeliState = async (id, next) => {

    // 업데이트
    setOrderList(prev => prev.map(r => r.orderNo === id ? { ...r, orderDeliState: next} : r));
    const updated = await updateOrderStatus(id, { orderDeliState: next });

    // 완료 시 이동
    if (isCompleted(updated)) {
      setOrderList(prev => prev.filter(r => r.orderNo !== id));
      navigate("/order/orderreceive");
    } else {
      setOrderList(prev => prev.map(r => r.orderNo === id ? { ...r, ...updated } :r));
    }
  }

  return (
    <div className="page-wrapper">
      <BackButtonComponent text="< &nbsp;이전페이지" onClick={() => navigate(-1)} />
      <h2 style={{ textAlign: "center" }}>상품주문서</h2>

      {/* 테이블 */}
      <table>
        <thead>
          <tr>
            {OrderListArr.map(col => (
              <th key={col.id}>{col.content}</th>
            ))}
          </tr>
        </thead>

        <tbody>
          {orderList?.length ? orderList.map(row => (
            <tr key={row.orderNo} className="row" onClick={() => handleRowClick(row)}>
              {OrderListArr.map(col => {
                // 1) 총금액은 계산해서 표시
                if (col.clmn === "totalPrice") {
                  const qty   = Number(row.orderQty ?? 0);
                  const price = Number(row.orderPrice ?? 0);
                  const total = Number(row.totalPrice ?? 0) || (qty * price);
                return <td key={col.id}>{total.toLocaleString()}</td>;
                }
                
                // 2) 주문상태 → 드롭다운 셀
                if (col.clmn === "orderState") {
                  return (
                    <td key={col.id}>
                      <InlineSelectCell
                        rowKey={row.orderNo}
                        value={row.orderState}
                        options={["승인대기", "승인완료"]}
                        onPatch={onPatchOrderState} />
                    </td>
                  );
                }
                
                // 3) 납품상태 → 드롭다운 셀
                if (col.clmn === "orderDeliState") {
                  return (
                    <td key={col.id}>
                      <InlineSelectCell
                        rowKey={row.orderNo}
                        value={row.orderDeliState}
                        options={["납품대기", "납품완료"]}
                        onPatch={onPatchDeliState} />
                    </td>
                  );
                }
                // 4) 나머지는 기존 그대로
                return (
                  <td
                    key={col.id}
                    style={col.clmn === "cusComp"
                      ? { color: "blue", textDecoration: "underline", cursor: "pointer" }
                      : {}} >
                        {row[col.clmn]}
                  </td>
                );
              })}
            </tr>
              )) : (
            
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
          setCustomerId={(v) => setFormData(p => ({ ...p, customerId: v }))}
          setItemListId={(v) => setFormData(p => ({ ...p, itemId: v }))} >
          <ButtonComponent text={"등록"} onClick={handleCreateAndRefresh} cln="submit" />
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
              <ButtonComponent text="수정" onClick={handleUpdateAndRefresh} cln="fixbtn" />
              <ButtonComponent text="삭제" onClick={handleDelete} cln="delbtn" />
            </div>
          </OrderListForm>
        )}
      </ModalComponent>
    </div>
  );
};

export default OrderListPage;
