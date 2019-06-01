#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import {connect, dispatch} from 'react-redux';
import {edit${domainClassName}, save${domainClassName}} from '../actions/${domainObjectName}';
import ${domainClassName}Edit from '../components/${domainClassName}Edit';

const mapStateToProps = (state) => {
    console.log(state);
    return {
        selected${domainClassName}: state.$domainObjectName
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        onEdit${domainClassName}(name, value){
            dispatch(edit${domainClassName}(name, value))
        },
        onSave${domainClassName}(url, ${domainObjectName}){
            dispatch(save${domainClassName}(url, ${domainObjectName}))
        }
    }
}
export default  connect(mapStateToProps, mapDispatchToProps)(${domainClassName}Edit);