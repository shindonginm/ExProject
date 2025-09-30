
import { partOrderArrays } from "../../arrays/partOrderArrays";


const PartOrderForm = ({ formData, onClose, onSubmit,onChange }) => {
 
   

  return (
    <div className="form-wrapper">
      {partOrderArrays.slice(1).map((data, idx) => (
        <div className="form-content" key={idx}>
          <p>
            <span className="form-child">{data.content}</span>
            { data.id === 6 ? (
            <select id=""
            name={data.clmn}
            value={formData.poState || ""}
            onChange={onChange}>
              <option value="입고대기">입고대기</option>
              <option value="입고완료">입고완료</option>
              <option value="납품대기">납품대기</option>
              <option value="납품완료">납품완료</option>
            </select>
            ):(<input
              type={data.input}
              name={data.clmn}
              value={formData[data.clmn] || ""}
              onChange={onChange}
            />)}
            
          </p>
        </div>
      ))}
      
    </div>
  );
};

export default PartOrderForm;
