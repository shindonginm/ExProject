import ForecastWithSelector from "../components/ForecastWithSelector";
import MonthlySalesChart from "../components/MonthlySalesChart";
import ItemQtyMonthlyChart from "../components/ItemQtyMonthlyChart";

export default function WoodenMainPage() {
  return (
    <div className="gridbox">
      <div className="grid grid-forecast">
        <ForecastWithSelector defaultHorizon={12} />
      </div>
      <div className="grid grid-monthly">
        <MonthlySalesChart />
      </div>
      <div className="grid grid-itemqty">
        <ItemQtyMonthlyChart topN={10} />
      </div>
    </div>
  );
}
