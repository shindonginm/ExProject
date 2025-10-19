from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import numpy as np, pandas as pd
from statsmodels.tsa.arima.model import ARIMA

app = FastAPI(title="Forecast Engine (Local)")

class HistPoint(BaseModel):
    date: str
    qty: float

class ForecastReq(BaseModel):
    history: list[HistPoint]
    horizon: int = 12

@app.post("/forecast/weekly")
def forecast_weekly(req: ForecastReq):
    if len(req.history) < 8:
        return [{"date": "9999-01-01", "mean": 0, "p10": 0, "p50": 0, "p90": 0}]

    df = pd.DataFrame([h.dict() for h in req.history])
    df["date"] = pd.to_datetime(df["date"])
    df = df.sort_values("date")
    y = df["qty"].astype(float)

    try:
        res = ARIMA(y, order=(1, 1, 1)).fit()
        mean_series = res.forecast(steps=req.horizon)
        mean = np.asarray(mean_series, dtype=float).ravel()
        sigma = float(np.nan_to_num(np.std(res.resid, ddof=1), nan=0.0))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"model error: {e}")

    start = df["date"].iloc[-1] + pd.offsets.Week(1)
    idx = pd.date_range(start, periods=req.horizon, freq="W-MON")

    z = 1.2816
    p10 = np.maximum(0.0, mean - z * sigma)
    p50 = np.maximum(0.0, mean)
    p90 = np.maximum(0.0, mean + z * sigma)

    out = []
    for i in range(req.horizon):
        out.append({
            "date": idx[i].date().isoformat(),
            "mean": float(p50[i]),
            "p10":  float(p10[i]),
            "p50":  float(p50[i]),
            "p90":  float(p90[i]),
        })
    return out
