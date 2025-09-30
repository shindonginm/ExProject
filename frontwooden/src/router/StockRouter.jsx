import { lazy,Suspense } from "react";
import { Navigate } from "react-router-dom";

const Loading = <div>Loading...</div>

const StockListPage = lazy(() => import("../pages/stock/StockListPage"));
const SellListPage = lazy(() => import("../pages/stock/SellListPage"))

const StockRouter = () => {

  return[
    {
      path:"",
      element: <Navigate replace to = "stocklist"/>
    },
    {
      path:"stocklist",
      element:<Suspense fallback={Loading}><StockListPage/></Suspense>
    },
    {
      path:"sellamount",
      element: <Suspense fallback={Loading}><SellListPage/></Suspense>
    }
  ]
}
  

export default StockRouter;