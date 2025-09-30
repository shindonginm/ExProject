import { Link, useNavigate } from "react-router-dom";
import './UserLogin.css';
import { useState } from "react";
import { TestAdmin } from "../../arrays/MainArrays";

const UserLogin = () => {
    const navigate = useNavigate();
    const [ id, setId ] = useState("");
    const [ pw, setPw ] = useState("");

    const onChangeId = (e) => {
        setId(e.target.value);
    }
    const onChangePw = (e) => {
        setPw(e.target.value);
    }

    function handleSubmit(e){
        e.preventDefault();
                // 로그인 성공 시
        if (id === TestAdmin.ID && pw === TestAdmin.Password) {
            alert("로그인 성공.");
            localStorage.setItem("isLoggedIn", "true"); // 로그인 상태 저장
            navigate(-1); // 이전 페이지로 이동
        }
        else{
            alert("아이디나 패스워드가 일치하지 않습니다.")
        }
    }
    return(
        <div className="login-wrapper">
            <form onSubmit={handleSubmit} className="login-content">
                <p><label>아이디</label><input type="text"
                value={id}
                onChange={onChangeId}
                /></p>
                <p>
                    <label>비밀번호</label><input type="password" 
                    value={pw}
                    onChange={onChangePw}/>
                </p>
                
                <button type="submit" className="login">로그인</button>
            </form>
        </div>
    )
}
export default UserLogin;