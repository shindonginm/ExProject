// import { useEffect, useState } from "react";
// import { getPartOrders, createPartOrder, updatePartOrder, deletePartOrder } from "../../api/PartOrderAPI";
// import ModalComponent from "../../components/ModalComponent";
// import { SubArray } from "../../arrays/TableArrays.jsx";
// import PartOrderForm from "../../form/buyer/PartOrderForm.jsx";
// import ButtonComponent from "../../components/ButtonComponent.jsx";
// import { useNavigate } from "react-router-dom";
// import BackButtonComponent from "../../components/BackButtonComponent.jsx";

// const PartOrderListPage = () => {

//   const [partOrders, setPartOrders] = useState([]);
//   const [isCreateModalOpen, setCreateModalOpen] = useState(false);
//   const [isEditModalOpen, setEditModalOpen] = useState(false);
//   const [selectedOrder, setSelectedOrder] = useState(null);
//   const navigate = useNavigate();

//   const getInitFormData = () => ({
//     poNo: "",
//     buyerAddr: "",
//     buyerComp: "",
//     partName: "",
//     poDate: "",
//     poPrice: "",
//     poQty: "",
//     poState: "입고대기"
//   })

//   const [formData, setFormData] = useState(getInitFormData());
//   console.log(formData);

//   useEffect(() => {
//     const fetchPartOrders = async () => {
//       const res = await getPartOrders();
//       setPartOrders(res);
//     };
//     fetchPartOrders();
//   }, []);

//   const handleChange = (e) => {
//     const { name, value } = e.target;
//     setFormData(prev => ({ ...prev, [name]: value }));
//   };

//   const getInitPayload = () => ({
//     buyerAddr: formData.buyerAddr,
//     buyerComp: formData.buyerComp,
//     partName: formData.partName,
//     poQty: Number(formData.poQty),     // 문자열 -> 숫자
//     poPrice: Number(formData.poPrice), // 문자열 -> 숫자
//     poState: formData.poState,
//     poDate: formData.poDate  
//   })
  
//   // 등록 이벤트
//   const handleCreate = async () => {
//     // poDate가 비어있으면 기본값으로 오늘 날짜
//     const payload = getInitPayload();
//     if (!payload.poDate) {
//       const today = new Date();
//       payload.poDate = today.toISOString().split("T")[0]; // yyyy-MM-dd
//     }

    
//     console.log("등록 요청 데이터:", payload);

//      // 빈값 체크
//     const hasEmptyField = Object.values(payload).some(
//       v => v === null || v === "" || v === 0
//     );
//     if(hasEmptyField){
//       alert("빈값이 포함되어있습니다, 입력폼에 값을 넣으세요.")
//       return
//     }
//     if(handleChange !== null && handleChange !== ""){
//     try {
//       const res = await createPartOrder(payload); // ✅ 객체로 딱 한 번만 보내기
//       alert("등록 성공");

//       setCreateModalOpen(false);

//       setFormData(getInitFormData());

//       const updatedList = await getPartOrders();
//       setPartOrders(updatedList);
//       return res.data;
//     } catch (err) {
//       console.error("등록 실패:", err.response?.data || err);
//       alert(JSON.stringify(err.response?.data || "알 수 없는 오류 발생"));
//     }
//   }
//   };

//   const openCreateModal = () => {
//     setFormData(getInitFormData());
//     setCreateModalOpen(true);
//   };

//   const openEditModal = (order) => {
    
//     setFormData(getInitPayload, order);
//     setSelectedOrder(order);   // 클릭한 row 데이터 저장
//     setEditModalOpen(true);    // 모달 열기
//   };

//   const handleUpdate = () => {
//   updatePartOrder(formData)
//     .then((res) => {
//       // 서버 업데이트 성공
//       setPartOrders(prev =>
//         prev.map(po =>
//           po.poNo === formData.poNo 
//             ? { ...po, poQty: formData.poQty, poPrice: formData.poPrice, poState: formData.poState } 
//             : po
//         )
//       );

//       alert("수정성공");
//       setEditModalOpen(false);
//     })
//     .catch(err => console.log("수정실패" + err));
// };

//   const handleDelete = async () => {
//   if (!selectedOrder) return;
//   try {
//     await deletePartOrder(selectedOrder.poNo); // axios.delete 경로에 poNo 포함

//     alert("부품명 : " + formData.partName +" 삭제 성공");

    
//     setPartOrders(partOrders.filter(po => po.poNo !== selectedOrder.poNo));
//     setEditModalOpen(false);
//   } catch (error) {
//     console.error("삭제 실패:", error.response?.data || error.message);
//   }
// };
//   return (
//     <div style={{ padding: "20px" }} className="customer-wrapper">
//       <BackButtonComponent text="< &nbsp;이전페이지" onClick={()=>navigate(-1)}/>
//       <h2 style={{ textAlign: "center" }}>부품발주 리스트</h2>

//       <table
//         border="1"
//         style={{ width: "100%", textAlign: "center", borderCollapse: "collapse", marginTop: "20px" }}
//       >
//         <thead>
//           <tr>
//             {SubArray.map((list, index) => (
//               <th key={index}>{list.content}</th>
//             ))}
//           </tr>
//         </thead>
//         <tbody>
//           {partOrders && partOrders.length > 0 ? (
//             partOrders.slice(1).map((c, index) => (
//               <tr key={c.poNo} className="row">
//                 <td>{index + 1}</td>
//                 <td style={{ color: "blue", textDecoration: "underline" }} onClick={() => openEditModal(c)}>
//                   {c.buyerComp}
//                 </td>
//                 <td>{c.partName}</td>
//                 <td>{c.poQty}</td>
//                 <td>{c.poPrice}</td>
//                 <td>{c.poState}</td>
//                 <td>{c.poDate}</td>
//                 <td>{c.buyerAddr}</td>
//               </tr>
//             ))
//           ) : (
//             <tr>
//               <td colSpan={8} style={{ textAlign: "center" }}>
//                 데이터가 없습니다.
//               </td>
//             </tr>
//           )}
//         </tbody>
//       </table>

//       <br />
//       <ButtonComponent onClick={openCreateModal} text={"발주등록"} cln="submit"/>

//       {/* 등록 모달 */}
//       <ModalComponent
//         isOpen={isCreateModalOpen}
//         onClose={() => setCreateModalOpen(false)}
//         title="발주 등록"
//         onConfirm={handleCreate}
//       >
//         <PartOrderForm formData={formData} onChange={handleChange} onSubmit={handleCreate}/>
//         <ButtonComponent text={"등록"} onClick={handleCreate} cln="submit"/>
//       </ModalComponent>

//       {/* 수정/삭제 모달 */}
//       <ModalComponent
//   isOpen={isEditModalOpen}
//   onClose={() => setEditModalOpen(false)}
//   title="발주 수정/삭제"
//   onConfirm={handleUpdate}
// >
//   {selectedOrder && (
//     <>
//       {/* 클릭한 데이터가 input에 들어가도록 formData 전달 */}
//       <PartOrderForm formData={formData} onChange={handleChange} onSubmit={handleCreate}/>

//       {/* 버튼 */}
//       <div className="btn-wrapper">
        
//       <ButtonComponent text="수정" onClick={handleUpdate} cln="fixbtn"/>

//       <ButtonComponent text="삭제" onClick={handleDelete} cln="delbtn"/>
//       </div>
      
//     </>
//   )}
// </ModalComponent>

//     </div>
//   );
// }

// export default PartOrderListPage;
