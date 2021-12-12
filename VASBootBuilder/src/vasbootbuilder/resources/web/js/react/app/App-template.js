#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import {HashRouter, Route, Link } from 'react-router-dom';
import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';

import ${domainClassName}ListContainer from '../containers/${domainClassName}ListContainer';
import ${domainClassName}EditContainer from '../containers/${domainClassName}EditContainer';
import Home from './Home';


function App(props){

    return(
    	<HashRouter>
            <div className="p-grid">
                <div className="p-col-3">
                    <ul className="navbar-nav mr-auto">
                        <li>
                            <Link to="/">Home</Link> <span className="sr-only">(current)</span>
                        </li>
                        <li>
                            <Link to="/${domainObjectName}s" onClick={() => props.fetchAll${domainClassName}s()}>${domainClassName}s</Link>
                        </li>                            
                    </ul>
                </div>
                
                <div className="p-col-9">                    
                  <Route path="/" exact component={Home} />
                  <Route path="/${domainObjectName}s" exact component={${domainClassName}ListContainer} />
                  <Route path="/${domainObjectName}" exact component={${domainClassName}EditContainer} />                
                </div>
            </div>
        </HashRouter>
    );
};

export default App;
