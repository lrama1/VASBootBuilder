import React from 'react';
import { useHistory, useLocation, withRouter } from 'react-router-dom';

const AppBreadcrumb = (props) => {

    const location = useLocation();
    const history = useHistory();

    let pathname = props.routers.filter((router) => {
        return router.path === location.pathname;
    })
    
    const path = pathname[0].meta.breadcrumb[0].label;


    return (
        <div className="route-bar">
            <div className="route-bar-breadcrumb">
                <ul>
                    <li><button type="button" className="p-link" onClick={() => history.push('/')}><i className="pi pi-home"/></button></li>
                    <li>/</li>
                    {
                        path === '/' ? <li>Dashboard</li> : <li><button className="p-link">{path}</button></li>
                    }
                </ul>
            </div>
        </div>
    );
}

export default withRouter(AppBreadcrumb);
