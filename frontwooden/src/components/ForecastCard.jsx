import React, { useEffect, useMemo, useState } from "react";
import { getForecastSeries } from "../api/forecastAPI";
import {
  ResponsiveContainer, LineChart, Line, XAxis, YAxis,
  CartesianGrid, Tooltip, Legend
} from "recharts";

// 아주 작은 예측치는 0으로 보정
const tidy = v => (v == null ? null : v < 0.5 ? 0 : Math.round(v));

// 'YYYY-MM-DD' → Date(로컬 안전) → timestamp(ms)
function ymdToTs(iso) {
  const [y, m, d] = iso.split("-").map(Number);
  return new Date(y, m - 1, d).getTime();
}

// 주(월요일) 리스트 만들기
function weeklyRange(startISO, endISO) {
  const out = [];
  let t = ymdToTs(startISO);
  const end = ymdToTs(endISO);
  const week = 7 * 24 * 60 * 60 * 1000;
  while (t <= end) {
    out.push(t);
    t += week;
  }
  return out;
}

export default function ForecastCard({ itemNo = 1, horizon = 12, title }) {
  const [series, setSeries] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const res = await getForecastSeries(itemNo, horizon);
        setSeries(res);
      } catch (e) {
        setErr(e?.message || "데이터 로드 오류");
      } finally {
        setLoading(false);
      }
    })();
  }, [itemNo, horizon]);

  // 차트 행 생성: 연속 주 축 + hist/mean 매핑
  const chartData = useMemo(() => {
    if (!series) return [];

    // 1) 실적
    const hist = (series.history || []).map(h => ({
      date: h.date,
      hist: tidy(h.qty),
      mean: null
    }));

    // 2) 예측
    const fc = (series.forecast || []).map(f => ({
      date: f.date,
      hist: null,
      mean: tidy(f.mean)
    }));

    // 3) 브릿지: 마지막 실적점에서 예측 라인도 같은 값으로 한 번 찍어줌
    const bridge =
      hist.length
        ? [{ date: hist[hist.length - 1].date, hist: hist[hist.length - 1].hist, mean: hist[hist.length - 1].hist }]
        : [];

    // 실적 → 브릿지 → 예측
    return [...hist, ...bridge, ...fc];
  }, [series]);

  if (loading) return <div style={{ padding: 16 }}>로딩중…</div>;
  if (err) return <div style={{ padding: 16, color: "#b91c1c" }}>에러: {String(err)}</div>;
  if (!series) return null;

  return (
    <div style={{ overflow: "visible" }}>
      {/* 헤더 */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <div style={{ fontWeight: 700, fontSize: 18 }}>
          {title ?? `예측: ${series.itemName ?? `Item #${series.itemNo}`}`}
        </div>
        <div style={{ color: "#6b7280", fontSize: 12, whiteSpace: "nowrap" }}>
          실적(검정) · 예측(파랑)
        </div>
      </div>

      {/* 차트 */}
      <div style={{ width: "100%", height: 320, overflow: "visible" }}>
        <ResponsiveContainer>
          <LineChart data={chartData} margin={{ top: 10, right: 16, left: 8, bottom: 24 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" minTickGap={24} tickMargin={6} />
            <YAxis allowDecimals={false} tickMargin={4} />
            <Tooltip />
            <Legend />

            <Line
              type="monotone"
              dataKey="hist"
              name="실적"
              dot={false}
              stroke="#111827"
              strokeWidth={2}
              isAnimationActive={false}
              connectNulls
            />
            <Line
              type="monotone"
              dataKey="mean"
              name="예측"
              dot={false}
              stroke="#2563eb"
              strokeWidth={2}
              isAnimationActive={false}
              connectNulls
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
