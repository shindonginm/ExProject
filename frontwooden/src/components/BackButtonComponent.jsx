

const BackButtonComponent = ({onClick, text, type}) => {
  
  return(
    <div>
      <button type={type}
      onClick={onClick}
      className="back-button">
        {text}
      </button>
    </div>
  )
}
export default BackButtonComponent;