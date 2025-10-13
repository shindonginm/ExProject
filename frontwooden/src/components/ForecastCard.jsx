import React, { useEffect, useMemo, useState } from "react";
import { getForecastSeries } from "../api/forecastAPI";
import {
  ResponsiveContainer, LineChart, Line, XAxis, YAxis,
  CartesianGrid, Tooltip, Legend, Area
} from "recharts";

const tidy = (v) => (v == null ? null : v < 0.5 ? 0 : Math.round(v));

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
        setErr(e?.message || "error");
      } finally {
        setLoading(false);
      }
    })();
  }, [itemNo, horizon]);

  const chartData = useMemo(() => {
    if (!series) return [];
    const hist = (series.history || []).map(h => ({
      date: h.date, hist: tidy(h.qty), mean: null, p10: null, p90: null
    }));
    const fc = (series.forecast || []).map(f => ({
      date: f.date, hist: null, mean: tidy(f.mean), p10: tidy(f.p10 ?? f.mean), p90: tidy(f.p90 ?? f.mean)
    }));
    return [...hist, ...fc];
  }, [series]);

  if (loading) return <div style={{padding:16}}>로딩중…</div>;
  if (err) return <div style={{padding:16, color:"#b91c1c"}}>에러: {String(err)}</div>;
  if (!series) return null;

  return (
    <div style={{padding:16, background:"#fff", borderRadius:16, boxShadow:"0 4px 20px rgba(0,0,0,0.06)"}}>
      <div style={{fontWeight:700, fontSize:18, marginBottom:8}}>
        {title ?? `예측: ${series.itemName ?? `Item #${series.itemNo}`}`}
      </div>
      <div style={{color:"#6b7280", fontSize:12, marginBottom:12}}>
        실적(검정) · 예측(파랑) · 신뢰구간(연한 영역)
      </div>

      <div style={{width:"100%", height:320}}>
        <ResponsiveContainer>
          <LineChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" minTickGap={24}/>
            <YAxis allowDecimals={false}/>
            <Tooltip />
            <Legend />
            <Area type="monotone" dataKey="p90" stroke="none" fillOpacity={0.15} />
            <Area type="monotone" dataKey="p10" stroke="none" fillOpacity={1} fill="#ffffff" />
            <Line type="monotone" dataKey="hist" name="실적" dot={false} stroke="#111827" strokeWidth={2}/>
            <Line type="monotone" dataKey="mean" name="예측" dot={false} stroke="#3b82f6" strokeWidth={2}/>
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
