import { useState } from "react";
import { LoginAPI } from "../../api/user/userAPI";
import BackButtonComponent from "../../components/BackButtonComponent";
import { useCRUD } from "../../hook/useCRUD";
import { useNavigate } from "react-router-dom";
import { initForms } from "../../arrays/TableArrays";
import useCustomLogin from "../../hook/useCustomLogin";
import LoginComponent from "../../components/user/LoginComponent";
import useEvent from "../../hook/useEvent";

const api = {
    getAll : LoginAPI,
}

const UserLogin = () => {
    const navigate = useNavigate();
    const { goBackBtn } = useEvent();
    
    const loginValues = {}
    
    const {
        formData,
        handleChange,
    } = useCRUD({
        initFormData:() => initForms.signup,
        keyField:"userNo",
        api,
        loginValues
    });
    const{
        isLoggedIn,
        setIsLoggedIn,
    } = useEvent({});

    const [loginParam , setLoginParam] = useState({...formData});

    const { doLogin, moveToPath } = useCustomLogin();




    return(
    <div className="page-wrapper">
        <div className="topbar">
            <BackButtonComponent text={"뒤로가기"} onClick={goBackBtn}/>
        </div>
        <LoginComponent
        loginParam={loginParam}
        setLoginParam={setLoginParam}
        handleChange={handleChange}
        formData={formData}
        doLogin={doLogin}
        moveToPath={moveToPath}
        navigate={navigate}
        isLoggedIn={isLoggedIn}
        setIsLoggedIn={setIsLoggedIn}
        />
    </div>
)

}
export default UserLogin;