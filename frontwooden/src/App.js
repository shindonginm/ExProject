
import React from "react";
import { RouterProvider } from "react-router-dom";
import root from "./router/root";


function App() {
  return (
    <div className="MAIN_WRAPPER">
      <RouterProvider router={root} />
    </div>
  );
}

export default App;
