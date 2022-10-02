import React, { useEffect } from 'react';
import { Route, useLocation, withRouter } from 'react-router-dom';
import App from './App';
import Access from './pages/Access';
import Error from './pages/Error';
import Login from './pages/Login';
import NotFound from './pages/NotFound';
import Wizard from './pages/Wizard';

const AppWrapper = () => {

    let location = useLocation();

    useEffect(() => {
        window.scrollTo(0, 0)
    }, [location]);

    switch (location.pathname) {
        case '/login':
            return <Route path="/login" component={Login} />
        case '/error':
            return <Route path="/error" component={Error} />
        case '/notfound':
            return <Route path="/notfound" component={NotFound} />
        case '/access':
            return <Route path="/access" component={Access} />
        case '/wizard':
            return <Route path="/wizard" component={Wizard} />
        default:
            return <App />;
    }
}

export default withRouter(AppWrapper);
