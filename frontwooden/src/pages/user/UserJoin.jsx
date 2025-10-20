import { useEffect, useState } from "react";
import { getUserInfo,createUserInfo } from "../../api/user/signupAPI";
import ButtonComponent from "../../components/ButtonComponent";
import { useCRUD } from "../../hook/useCRUD";
import { initForms } from "../../arrays/TableArrays";
import { UserArrays } from "../../arrays/user/UserArrays";
import { useNavigate } from "react-router-dom";

const api = {
  getAll: getUserInfo,
  create : createUserInfo,
}
export default function UserJoin(){
  const [ pw, setPw ] = useState(false);
  const [ checkPw, setCheckPw ] = useState(false);
  const [ input, setInput ] = useState("");
  const [ ckinput,setCkInput ] = useState(""); 
  const navigate = useNavigate();


  const passwordChecked = (e) => {
    const data = e.target.value;
    setInput(data);
    setPw(!!data);
    if(!data){
      setCkInput("");
    }
  }

  const checkingUserPw = (e) => {
    const data = e.target.value;
    setCkInput(data);
  }

  useEffect(() => {
    if(pw) setCheckPw( input === ckinput );
  },[pw,ckinput,input])

  const {
    formData,
    handleCreate,
    handleChange,
  } = useCRUD({
    initFormData:() => initForms.signup,
    keyField: "userNo",
    api
  })



  return(
    <div>
      Join
      <form action="">
        { UserArrays.slice(0,4).map((list,idx) => (
          <label htmlFor="" 
          name={list.name}
          key={idx}
          >
            <span>{list.name}</span>
            
            { list.value === "password" ? 
            <input type={list.type} 
            name={list.value}
            onChange={(e) => {passwordChecked(e); handleChange(e);}}
            
            />
            : list.value === "checkPassword" ? 
            <>
            <input type={list.type} 
            name={list.value}
            onChange={checkingUserPw}
            disabled={!pw}
            />
            <p>{checkPw ? "비밀번호 일치": "비밀번호 불일치"}</p>
            </>
            :
            <input type={list.type} 
            name={list.value}
            value={formData[list.value] || ""}
            onChange={handleChange}
            />
            }
          </label>
          
        ))}
        
        <ButtonComponent text={"회원가입"} onClick={
          (e) => 
          {e.preventDefault();
            handleCreate();
            navigate("/login");
          }
          } disable={!checkPw}/>
      </form>
      
    </div>
  )
}
