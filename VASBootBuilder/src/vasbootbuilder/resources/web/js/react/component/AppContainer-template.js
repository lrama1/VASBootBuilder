#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import { Route, Link } from 'react-router-dom';
import './AppContainer.css';
import Home from '../Home';
import ${domainClassName}List from '../components/${domainClassName}List';
import ${domainClassName}Detail from '../components/${domainClassName}Detail';
const appContainer = (props) => {
return(
<section aria-labelledby="app-csscontainer-1" class="wf-container">
<div className="wf-skin-nx">
<div className="wf-gn wf-gn-loading wf-gn-app" data-appid="cldp-app">
<header className="wf-gn-header">
<div className="wf-gn-header-logo wf-gn-header-logo-tink">
<a href="#" className="wf-gn-logo-link" aria-labelledby="wf-logo-svg-title">
<span className="wf-gn__header-logo-icon">
</span>
</a>
</div>
<span className="wf-gn-print-header-appname"></span>
<nav className="wf-gn-header-nav">
<a href="/cldp/SignOut.htm" className="wf-gn-signoff">Sign Off</a>
</nav>
</header>
<div className="wf-container-body">
<aside>
<ul>
<li><Link className="wf-col--12" to="/">Home</Link></li>
<li><Link className="wf-col--12" to="/${domainObjectName}s">${domainClassName}s</Link></li>
</ul>
</aside>
<div className="wf-row">
<div className="wf-col--2">
<div className="wf-row">
</div>
</div>
<div className="wf-col--8">
<Route path="/" exact component={Home} />
<Route path="/${domainObjectName}/:id" exact component={${domainClassName}Detail}/>
<Route path="/${domainObjectName}s" exact component={${domainClassName}List}/>
</div>
</div>
</div>
</div>
</div>
</section>
);
}
export default appContainer;