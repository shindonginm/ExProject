// 사용자의 편의성을 위해 전체 카테고리를 생성해서 각 항목을 선택해 경로를 바꿀 수 있게 함.
import "../../components/Modal.scss";
import CloseBtnComponent from "../../components/CloseBtnComponent";
import { BuyerList, NavBar, OrderList, PlanList, StockList } from "../../arrays/MainArrays";
import { Link } from "react-router-dom";
import useCustomLogin from "../../hook/useCustomLogin";

const GuideMenuLayout = ({ openSideSet,setOpenSideSet }) => {
  const { loginState } = useCustomLogin();
  return(
    <div className={"guidemenu-wrapper" + ( openSideSet ? " show":"")}>
      <div className="guidemenu-title">
        WOODEN
        <CloseBtnComponent style={{float:"right", height:"50px"}} onClose={() => setOpenSideSet(false)}/>
      </div>

    <div className="guidemenu-content">
        {NavBar.map(list => (
          <ul key={list.id} className={"guidemenu-lists"}>
            {list.name === "ORDER" ? 
            (<>
              <h3>ORDER</h3>
              {OrderList.map(li => (
                <li key={li.id}>
                  <Link to={li.path}>{li.name}</Link>
                </li>
              ))}
            </>):
            list.name === "BUYER" ?
            (<>
              <h3>BUYER</h3>
              {BuyerList.map(li => (
                <li key={li.id}>
                  <Link to={li.path}>{li.name}</Link>
                </li>
              ))}
            </>):
            list.name === "PLAN" ?
            (<>
              <h3>PLAN</h3>
              {PlanList.map(li => (
                <li key={li.id}>
                  <Link to={li.path}>{li.name}</Link>
                </li>
              ))}
            </>):
            (<>
              <h3>STOCK</h3>
              {StockList.map(li => (
                <li key={li.id}>
                  <Link to={li.path}>{li.name}</Link>
                </li>
              ))}
            </>)
            }
          </ul>
        ))}
    </div>
    </div>
  );
}
export default GuideMenuLayout;