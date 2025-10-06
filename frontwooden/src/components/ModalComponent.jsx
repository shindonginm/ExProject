import './Modal.css';
import CloseBtnComponent from './CloseBtnComponent';

const ModalComponent = ({onClose,isOpen,title,children}) => {
    
    return(
        <div className={"modal"+ (isOpen ? " show":"")}>
            <CloseBtnComponent onClose={onClose}/>
            <div className="modal-wrapper">
                <h2 className="modal-title">{title}</h2>
                {children}
            </div> 
        </div>
    )
}
export default ModalComponent;