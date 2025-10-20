import React from "react";

const PartOrderForm = ({
  formData,
  onChange,
  buyerOptions = [],
  onSelectBuyer, 
}) => {
  const handleInput = (e) => {
    const { name, value } = e.target;
    onChange({ target: { name, value } });
  };

  return (
    <div className="form-wrapper" style={{ display: "grid", gap: 12 }}>
      <label>
        구매처명
        <select
          name="buyerNo"
          value={formData.buyerNo ?? ""}
          onChange={(e) => { onChange(e); onSelectBuyer(Number(e.target.value)) }}
          required
        >
          <option value="">-- 선택 --</option>
          {buyerOptions.map((b) => (
            <option key={b.value} value={b.value}>{b.label}</option>
          ))}
        </select>
      </label>

      <label>
        구매처주소
        <input
          type="text"
          name="buyerAddr"
          value={formData.buyerAddr ?? ""}
          readOnly
          placeholder="구매처 선택 시 자동 입력"
        />
      </label>

      <label>
        부품명
        <input
          type="text"
          name="partName"
          value={formData.partName ?? ""}
          readOnly
          placeholder="구매처 선택 시 자동 입력"
        />
      </label>

      <input type="hidden" name="partNo" value={formData.partNo ?? ""} />

      <label>
        구매단가
        <input
          type="number"
          name="poPrice"
          value={formData.poPrice ?? ""}
          readOnly
          placeholder="부품 자동 선택 시 자동 입력"
        />
      </label>

      <label>
        구매수량
        <input
          type="number"
          name="poQty"
          value={formData.poQty ?? ""}
          onChange={handleInput}
          min={1}
          required
        />
      </label>

      <label>
        구매상태
        <select
          name="poState"
          value={formData.poState ?? "입고대기"}
          onChange={handleInput}
        >
          <option value="입고대기">입고대기</option>
        </select>
      </label>

      <label>
        입고일자
        <input
          type="date"
          name="poDate"
          value={formData.poDate ?? ""}
          onChange={handleInput}
        />
      </label>
    </div>
  );
};

export default PartOrderForm;
