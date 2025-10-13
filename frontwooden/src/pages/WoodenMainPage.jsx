import React from "react";
import ForecastCard from "../components/ForecastCard";

const WoodenMainPage = () => {
  return (
    <div className="gridbox">
      {/* 1. 아이템 1 예측 (12주) */}
      <div className="grid">
        <ForecastCard itemNo={1} horizon={12} title="Item #1 12주 예측" />
      </div>

      {/* 2. 아이템 2 예측 (8주) */}
      <div className="grid">
        <ForecastCard itemNo={2} horizon={8} title="Item #2 8주 예측" />
      </div>

      {/* 3. (임시) 카드 */}
      <div className="grid">여긴 나중에 다른 위젯</div>

      {/* 4. (임시) 카드 */}
      <div className="grid">여긴 나중에 다른 위젯</div>
    </div>
  );
};

export default WoodenMainPage;
