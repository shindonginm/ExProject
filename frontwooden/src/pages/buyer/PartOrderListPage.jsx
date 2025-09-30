

  import { useEffect } from "react";
  import { useCRUD } from "../../hook/useCRUD.jsx";
  import { getPartOrders, createPartOrder, updatePartOrder, deletePartOrder } from "../../api/PartOrderAPI"; // src/api/각자맡은부분API.jsx import해서 CRUD기능들을 모두 가져옴
  import ModalComponent from "../../components/ModalComponent";
  import PartOrderForm from "../../form/buyer/PartOrderForm.jsx";
  import ButtonComponent from "../../components/ButtonComponent.jsx";
  import BackButtonComponent from "../../components/BackButtonComponent.jsx";
  import { initForms } from "../../arrays/TableArrays.jsx";
  import { partOrderArrays } from "../../arrays/partOrderArrays.jsx";
  import { useNavigate } from "react-router-dom";


  // API 상수 >> src/api내에 있는 각자 만든 page의 api 부분을 가져옴. 
  const api = {     // 밑에 const 로 만든 useCRUD 상수에서 사용하는 키밸류 형식 CRUD.API
    getAll: getPartOrders,
    create: createPartOrder,
    update: updatePartOrder,
    delete: deletePartOrder
  };


  const PartOrderListPage = () => {
    const navigate = useNavigate();   // navigate 상수 생성

    const {   // < 이 상수는 useCRUD.jsx 파일 내에 있는 useState, const로 선언된 상수, 이벤트 핸들러를 불러올수 있게 함.
      items: partOrders,    // useCRUD.jsx내 있는 const [items,setItems] = useState([]);의 items의 이름을 partOrders로 재정의함.
      setItems: setPartOrders, // 위와 마찬가지로 setItems의 이름을 setPartOrders로 정의하여 useState의 상수명을 일반화하여 사용한다.
      
      formData,             // formData를 화면 상에 표시해주는 useState 가져오는 기능(setFormData는 useCRUD 이벤트함수에서 관리중.)
      handleChange,         // handleChange > input요소를 변경할때마다 formData의 값을 변경하는 기능.(props로 전달해서 Form페이지에서 <input onChange={onChange}로 사용됨/>)
      handleCreate,         // 요소를 등록하는 Create기능을 가져오는 기능.
      handleUpdate,         // 요소를 수정하는 Updata기능을 가져오는 기능.
      handleDelete,         // 요소를 삭제하는 Delete기능을 가져오는 기능.
      openEdit,             // 수정 / 삭제를 할 수 있는 모달을 열고 선택된 항목을 form에 세팅하는 함수
      openCreate,           // 등록 모달을 열고 form을 초기화하는 함수
      isCreateOpen,         // 등록(Create) 모달이 열려 있는지 여부를 나타내는 상태 (true = 열림)
      isEditOpen,           // 수정(Edit) 모달이 열려 있는지 여부를 나타내는 상태 (true = 열림)
      closeCreate,          // 등록 모달창을 닫는 함수(CloseBtnComponent.jsx에서 사용함) (isCreateOpen의 값이 true면 모달창이 닫힌다.)
      closeEdit,            // 수정 모달창을 닫는 함수(CloseBtnComponent.jsx에서 사용함) (isEditOpen의 값이 true면 모달창이 닫힌다.)
      selectedItem          // 맨 밑에 수정/삭제 모달창에서 selectedItem &&(AND연산자) ... 조건문을 통해 selectedItem이 있을 때만 수정/삭제 폼을 보여줌.
    } = useCRUD({ // import한 useCRUD.jsx를 사용하는 선언부임.
      initFormData: () => initForms.partOrder, // 초기 폼 데이터를 설정하는 함수 (TableArrays.jsx에서 initForms안에 자신이 맡은 부분의 이름을 불러오는것)
      // initForm."partOrder"만 바꾸면 됨.
      api,   // CRUD API 함수들을 담은 객체 (getAll, create, update, delete)
      keyField: "poNo",   // useCRUD파일 내에 선언된 keyfield에서 선언 한 값을 백에서 정의한 기본키인 poNo로 재정의 함
    });


    useEffect(() => {   // 테이블 내에 있는 데이터들을 api파일을 거져 화면에 렌더링하는 useEffect
      const fetchData = async() => {  // async ()=> 비동기 함수인 fetchData 생성
        const data = await getPartOrders();    // const data로 data를 선언한뒤 await(비동기가 작업이 끝날 때) api파일에서 생성한 getPartOrder을 실행함
        setPartOrders(data);  // useCRUD에서 선언한 setItems를 재정의 한 setPartOrders에 바로 위에서 선언한 data의 값을 넣는다.
      }
      fetchData();    // 위에서 선언한 비동기 함수인 fetchData를 실행한다.
    },[])
    return (        // react의 화면을 렌더링하는 return부분
      <div className="page-wrapper">  {/* className="page-wrapper"(변경 XX) > 각자 페이지에 padding으로 여백을 조정하게 함*/}
        <BackButtonComponent text="< &nbsp;이전페이지" onClick={() => navigate(-1)} /> {/*이전페이지 버튼 사용*/}
        <h2 style={{ textAlign: "center" }}>부품발주 리스트</h2>     {/* 각자 페이지에 맞는 카테고리명을 h2안에 입력 */}

        {/* 테이블 */}
        <table>     {/* 테이블 생성(구조는 같으니 변경 X ) */}
          <thead>   {/* thead > table안에 컬럼을 감싸는 요소*/}
            <tr> 
              {partOrderArrays.map(col => <th key={col.id}>{col.content}</th>)} {/* partOrderArrays.jsx에서 선언한 배열 안에 id, content만 사용해서 테이블 컬럼명 생성.*/}
            </tr>
          </thead>
          <tbody>   {/* tbody > 테이블의 몸통부분 요소 */}
            {partOrders && partOrders.length > 0 ? (  
              partOrders.map((po, index) => (
                <tr key={po.poNo} className="row" onClick={() => openEdit(po)}>
                  <td>{index + 1}</td>
                  {partOrderArrays.slice(1).map(col => (
                    <td key={col.id} style={col.clmn === "buyerComp" ? { color: "blue", textDecoration: "underline" } : {}}>
                      {po[col.clmn]}
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

        {/* 발주등록 버튼 */}
        <br />
        <ButtonComponent onClick={openCreate} text={"발주등록"} cln="submit" />

        {/* 등록 모달 */}
        <ModalComponent
          isOpen={isCreateOpen}
          onClose={closeCreate}
          title="발주 등록"
          onConfirm={handleCreate}
        >
          <PartOrderForm formData={formData} onChange={handleChange} onSubmit={handleCreate} />
          <ButtonComponent text={"등록"} onClick={handleCreate} cln="submit" />
        </ModalComponent>

        {/* 수정/삭제 모달 */}
        <ModalComponent
          isOpen={isEditOpen}
          onClose={closeEdit} 
          title="발주 수정/삭제"
          onConfirm={handleUpdate}
        >
          {selectedItem && (
            <>
              <PartOrderForm formData={formData} onChange={handleChange} onSubmit={handleUpdate} />
              <div className="btn-wrapper">
                <ButtonComponent text="수정" onClick={handleUpdate} cln="fixbtn" />
                <ButtonComponent text="삭제" onClick={handleDelete} cln="delbtn" />
              </div>
            </>
          )}
        </ModalComponent>
      </div>
    );
  };

  export default PartOrderListPage;
