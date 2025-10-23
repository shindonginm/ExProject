import ForecastWithSelector from "../components/ForecastWithSelector";
import MonthlySalesChart from "../components/MonthlySalesChart";
import ItemQtyMonthlyChart from "../components/ItemQtyMonthlyChart";

export default function WoodenMainPage() {
  return (
    <div className="dashboard-grid">
      {/* 상단: 예측 그래프 2칸 */}
      <section className="card top-left">
        <ForecastWithSelector defaultHorizon={12} />
      </section>
      {/* 하단: 좌(월별 매출), 우(상품별 그래프) */}
      <section className="card bottom-left">
        <MonthlySalesChart />
      </section>
      <section className="card bottom-right">
        <ItemQtyMonthlyChart topN={10} />
      </section>
    </div>
  );
}
