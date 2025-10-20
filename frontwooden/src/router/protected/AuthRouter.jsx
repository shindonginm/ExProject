import { createContext, useState } from "react";

const AuthContext = createContext();

export const AuthRouter = ({children}) => {
  const [ isLoggedIn , setIsLoggedIn ] = useState(false);
  return(
    <AuthContext.Provider value={isLoggedIn} set={setIsLoggedIn}>
      {children}
    </AuthContext.Provider>
  )
}
