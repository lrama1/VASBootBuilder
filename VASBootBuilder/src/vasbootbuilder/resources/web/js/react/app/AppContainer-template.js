import {fetchAll${domainClassName}s} from '../actions/${domainObjectName.toLowerCase()}'

import {connect} from "react-redux";
import App from '../App'

const mapStateToProps = (state) => {
    console.log(state);
    return {
        ${domainObjectName}s: state.${domainObjectName}sReducer
    };
};
const mapDispatchToProps = (dispatch) => {
    return{
        fetchAll${domainClassName}s: (url) => dispatch(fetchAll${domainClassName}s(url))
    }
}
export default connect(mapStateToProps, mapDispatchToProps)(App)