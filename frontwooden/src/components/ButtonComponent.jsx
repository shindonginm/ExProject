

const ButtonComponent = ({cln , onClick, text, type}) => {
  
  return(
      <button type={type}
      onClick={onClick}
      className={cln}
      >
        {text}
      </button>

  )
}
export default ButtonComponent;