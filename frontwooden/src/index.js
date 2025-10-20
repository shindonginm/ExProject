import './App.scss';
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";

const rooter = ReactDOM.createRoot(document.getElementById("root"));
rooter.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
