# uvicorn forecast_engine:app --host 0.0.0.0 --port 5001 --reload
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import numpy as np
import pandas as pd
from pandas.tseries.frequencies import to_offset
from statsmodels.tsa.arima.model import ARIMA


app = FastAPI(title="Forecast Engine (Local)")

# ---------- Schemas ----------
class HistPoint(BaseModel):
    date: str   # "YYYY-MM-DD"
    qty: float

class ForecastReq(BaseModel):
    history: List[HistPoint]
    horizon: int = 12
    include_anchor: bool = False  # 실적 마지막 점을 함께 내려 선이 이어지게

# ---------- Helpers ----------
def _infer_weekly_freq(dts: pd.Series) -> str:
    """
    히스토리 날짜 주기를 추론한다.
    실패하면 마지막 날짜 요일을 기준으로 주기(W-XXX)를 만든다.
    """
    freq = pd.infer_freq(dts)
    if freq:
        return freq
    # infer 실패 시 마지막 요일로 주기 설정
    wd = dts.iloc[-1].day_name()[:3].upper()  # e.g. 'MON'
    return f"W-{wd}"


def _fit_arima(y: pd.Series):
    """
    잔잔한 데이터에서 ARIMA가 튕기면 유치하게라도 동작하도록
    간단한 예외 처리를 깔아둔다.
    """
    # 최소 포인트 체크
    if len(y) < 8:
        raise HTTPException(status_code=422, detail="at least 8 history points required")

    try:
        model = ARIMA(y, order=(1, 1, 1))
        res = model.fit()
        sigma = float(np.nan_to_num(np.std(res.resid, ddof=1), nan=0.0))
        return res, sigma
    except Exception as e:
        # 모델이 터지면 그냥 평균 유지선 정도로 대응 (차트 이어지게 하는 게 1차 목표)
        class Dummy:
            def forecast(self, steps: int):
                return pd.Series([float(y.iloc[-1])] * steps, index=range(steps))
        return Dummy(), 0.0


# ---------- Endpoint ----------
@app.post("/forecast/weekly")
def forecast_weekly(req: ForecastReq):
    if not req.history:
        raise HTTPException(status_code=422, detail="history required")
    if req.horizon <= 0:
        raise HTTPException(status_code=422, detail="horizon must be positive")

    # 1) 입력 정리
    df = pd.DataFrame([h.dict() for h in req.history])
    try:
        df["date"] = pd.to_datetime(df["date"])
    except Exception:
        raise HTTPException(status_code=422, detail="invalid date format; use YYYY-MM-DD")
    df = df.sort_values("date")
    y = df["qty"].astype(float)

    # 2) 주기 추론 및 예측 인덱스 생성
    freq = _infer_weekly_freq(df["date"])
    start = df["date"].iloc[-1] + to_offset(freq)
    idx = pd.date_range(start=start, periods=req.horizon, freq=freq)

    # 3) 모델 적합
    res, sigma = _fit_arima(y)
    mean_series = res.forecast(steps=req.horizon)
    mean = np.asarray(mean_series, dtype=float).ravel()

    # 4) 간단한 예측구간(정규 근사)
    z = 1.2816  # ~P10/P90
    p10 = np.maximum(0.0, mean - z * sigma)
    p50 = np.maximum(0.0, mean)
    p90 = np.maximum(0.0, mean + z * sigma)

    # 5) 응답 구성: 앵커(실적 마지막 점) 포함
    out = []
    if req.include_anchor:
        anchor_date = df["date"].iloc[-1].date().isoformat()
        anchor_val = float(y.iloc[-1])
        out.append({
            "date": anchor_date,
            "mean": anchor_val,
            "p10": anchor_val,
            "p50": anchor_val,
            "p90": anchor_val,
            "anchor": True
        })

    for i in range(req.horizon):
        out.append({
            "date": idx[i].date().isoformat(),
            "mean": float(p50[i]),
            "p10":  float(p10[i]),
            "p50":  float(p50[i]),
            "p90":  float(p90[i]),
        })

    return out
