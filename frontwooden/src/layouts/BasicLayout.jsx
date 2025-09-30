import { Outlet,useLocation,useNavigate,Link } from "react-router-dom";
import { useState,useEffect } from "react";
import { NavBar } from "../arrays/MainArrays";
import AsideComponent from "../components/AsideComponent";


const BasicLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [ asdieID, setAsideID ] = useState(null);
    const [ navID, setNavID ] = useState(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const activeNavMap = {
    "/order/orderlist": 1,
    "/order/sellercustomer":1,
    "/order/orderreceive":1,
    "/buyer/partlist": 2,
    "/buyer/buyercustomer": 2,
    "/buyer/partorder":2,
    "/buyer/buyerdelivery":2,
    "/plan/itemlist":3,
    "/plan/planlist": 3,
    "/stock/stocklist": 4,
    "/stock/sellamount":4,
  };
  
    const asideGroups = {
        order: "/order",
        buyer: "/buyer",
        plan: "/plan",
        stock: "/stock"
    };
  const currentNavID = activeNavMap[location.pathname];
    const orderAside = location.pathname.startsWith(asideGroups.order);

    const buyerAside = location.pathname.startsWith(asideGroups.buyer);

    const planAside = location.pathname.startsWith(asideGroups.plan);

    const stockAside = location.pathname.startsWith(asideGroups.stock);

    function navigateAndReset(path){
        setAsideID(null);
        setNavID(null);
        navigate(path);
    }
   
    function handleNavClick(list){
        setNavID(list.id);
    }
      useEffect(() => {
        const pathToNavIdMap = {
            "/itemlist": 1,
            "/sellercustomer":1,
            "/orderlist": 1,
            "/orderreceive": 1, // 'ORDER' 네비게이션 ID
            "/partlist": 2,
            "/buyercustomer": 2,
            "/buyerdelivery":2,
            "/partorder":2,
            "/plan": 3,
            "/stock": 4,
            "/sellamount":4,
        };
    
        // const pathToAsideIdMap = {
        //     "/itemlist": 1,
        //     "/orderlist": 1,
        //     "/sellercustomer":2,
        //     "/orderreceive": 3, // Aside의 ID를 3으로 설정
        //     "/partlist": 1,
        //     "/buyercustomer": 2,
        //     "/partorder":3,
        //     "/sellamount":2,
        // };
        const loginStatus = localStorage.getItem("isLoggedIn");
        setIsLoggedIn(loginStatus === "true"); // 로그인 상태 불러오기
        setNavID(pathToNavIdMap[location.pathname] || null);
        
    }, [location.pathname]);

    return(
        <>
            <header>

                <div className="logo">
                    <h1>
                        <Link to = "/">
                            테스트로고
                        </Link>
                    </h1>
                </div>
                <nav>
                    <ul className="header-ul">
                        { NavBar.map(data => (
                            <li key={data.id} 
                            className={currentNavID === data.id ? "selected":""}
                            onClick={() => {;
                                if (isLoggedIn){
                                    handleNavClick(data);
                                    navigate(data.path)
                                }
                                else{ alert("로그인이 필요합니다.");
                                    navigate("/login")
                                }
                            }
                            }  
                            >
                            <Link to={data.path} className="nav-items">{data.name}</Link>
                            </li>
                        ))}
                    </ul>
                    <p className="loginbtn">    {/*테스트*/}
                    {isLoggedIn ? 
                        <span
                        onClick={() => {
                        localStorage.removeItem("isLoggedIn");
                        setIsLoggedIn(false);
                        alert("로그아웃 되었습니다.")
                        navigateAndReset("/");
                        }}
                        >
                            로그아웃
                        </span>
                    : 
                        <span onClick={() => navigateAndReset("/login")}>로그인</span>
                    }
                    </p>

                </nav>
        </header>
        <div className="main_content">
        <div className = "main_aside">
            <div className="main_article">
                <ul className="aside-ul">
                    <AsideComponent
                    orderAside={orderAside} 
                    buyerAside={buyerAside}
                    planAside={planAside}
                    stockAside={stockAside}
                    />
                </ul>
                
            </div>
        </div>
        <div className="main_body">
            <Outlet/>
        </div>
        </div>
        
        </>
    )
}
export default BasicLayout;