// src/router/StockRouter.jsx
import { lazy, Suspense } from "react";
import { Navigate } from "react-router-dom";

const Loading = <div>Loading...</div>;

// ✅ lazy 로딩
const ItemStockListPage = lazy(() => import("../pages/stock/ItemStockListPage"));
const PartStockListPage = lazy(() => import("../pages/stock/PartStockListPage"));
const SellListPage      = lazy(() => import("../pages/stock/SellListPage"));

const StockRouter = () => {
  return [
    {
      path: "",
      element: <Navigate replace to="itemstock" />,
    },
    {
      path: "itemstock",
      element: (
        <Suspense fallback={Loading}>
          <ItemStockListPage />
        </Suspense>
      ),
    },
    {
      path: "partstock",
      element: (
        <Suspense fallback={Loading}>
          <PartStockListPage />
        </Suspense>
      ),
    },
    {
      path: "sellamount",
      element: (
        <Suspense fallback={Loading}>
          <SellListPage />
        </Suspense>
      ),
    },
  ];
};

export default StockRouter;
