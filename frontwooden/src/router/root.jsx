import { lazy,Suspense } from "react";
import BasicLayout from "../layouts/BasicLayout";
import OrderRouter from "./OrderRouter";
import BuyerRouter from "./BuyerRouter";
import PlanRouter from "./PlanRouter";
import StockRouter from "./StockRouter";

const {createBrowserRouter} = require("react-router-dom");

const UserLogin = lazy(() => import('../pages/user/UserLogin'))
const WoodenMain = lazy(() => import("../pages/WoodenMainPage"))
const Loading = <div>Loading...</div>

const root = createBrowserRouter([
    {
        path:'',
        element: <BasicLayout/>,
        children:[
            {
                path:"",
                element:<Suspense fallback={Loading}><WoodenMain/></Suspense>
            },
            
            {
                path:"login",
                element: <Suspense fallback={Loading}><UserLogin/></Suspense>
            },
            {
                path:"order",
                children: OrderRouter(),
            },
            {
                path:"buyer",
                children: BuyerRouter(),
            },
            {
                path:"plan",
                children: PlanRouter(),
            },
            {
                path:"stock",
                children: StockRouter(),
            }
            
        ]
    },
]);
export default root;

