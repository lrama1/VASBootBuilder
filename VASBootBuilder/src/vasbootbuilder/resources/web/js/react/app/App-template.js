#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React, { useState, useRef, useEffect } from 'react';
import { classNames } from 'primereact/utils';
import { Route, useHistory, useLocation } from 'react-router-dom';

import AppTopbar from './AppTopbar';
import AppFooter from './AppFooter';
import AppMenu from './AppMenu';
import AppBreadcrumb from './AppBreadcrumb';
import AppInlineProfile from './AppInlineProfile';

import { Tooltip } from 'primereact/tooltip';

import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import './App.scss';
import Home from './components/Home';
import ${domainClassName}ListContainer from "./containers/${domainClassName}ListContainer";
import ${domainClassName}EditContainer from "./containers/${domainClassName}EditContainer";

const App = (props) => {

    const [menuActive, setMenuActive] = useState(false);
    //const [menuMode, setMenuMode] = useState('static');
    const menuMode = 'static'

    //const [darkMenu, setDarkMenu] = useState(true);
    const darkMenu = false;

    const [overlayMenuActive, setOverlayMenuActive] = useState(false);
    const [topbarMenuActive, setTopbarMenuActive] = useState(false);
    const [staticMenuDesktopInactive, setStaticMenuDesktopInactive] = useState(false);
    const [staticMenuMobileActive, setStaticMenuMobileActive] = useState(false);
    const [activeTopbarItem, setActiveTopbarItem] = useState(null);
    const [inlineMenuActive, setInlineMenuActive] = useState(false);
    //const [profileMode, setProfileMode] = useState('popup');
    const profileMode = 'popup'

    const [configActive, setConfigActive] = useState(false);
    //const [inputStyle, setInputStyle] = useState('outlined');
    const inputStyle = 'outlined'

    //const [ripple, setRipple] = useState(false);
    const ripple = false;

    const copyTooltipRef = useRef();
    const location = useLocation();

    const history = useHistory();

    let menuClick = false;
    let configClick = false;
    let topbarItemClick = false;
    let inlineMenuClick = false;

    const menu = [
        {
            label: 'Home Page', icon: 'pi pi-fw pi-home',
            items: [
                { label: 'Home', icon: 'pi pi-fw pi-home', to: '/' }
                , { label: '${domainClassName}s', icon: 'pi pi-fw pi-id-card', to: '/${domainObjectName}s', command: () => props.fetchAll${domainClassName}s()}
            ]
        }

    ];

    const routers = [
        { path: '/', component: Home, exact: true, meta: { breadcrumb: [{ parent: 'Home', label: 'Home' }] } }
        ,{ path: '/${domainObjectName}s', component: ${domainClassName}ListContainer, meta: { breadcrumb: [{ parent: 'UI Kit', label: '${domainClassName}s' }] } }
        ,{ path: '/${domainObjectName}', component: ${domainClassName}EditContainer, meta: { breadcrumb: [{ parent: 'UI Kit', label: '${domainClassName}' }] } }

    ];

    useEffect(() => {
        copyTooltipRef && copyTooltipRef.current && copyTooltipRef.current.updateTargetEvents();
    }, [location]);

    const onDocumentClick = () => {
        if (!topbarItemClick) {
            setActiveTopbarItem(null);
            setTopbarMenuActive(false);
        }

        if (!menuClick) {
            if (isHorizontal() || isSlim()) {
                setMenuActive(false);
            }
            hideOverlayMenu();
        }

        if (!inlineMenuClick && profileMode === 'inline' && isSlim() && !isMobile()) {
            setInlineMenuActive(false);
        }

        if (configActive && !configClick) {
            setConfigActive(false);
        }

        inlineMenuClick = false;
        configClick = false;
        topbarItemClick = false;
        menuClick = false;
    };

    const onMenuitemClick = (event) => {
        if (!event.item.items) {
            hideOverlayMenu();

            if (isSlim() || isHorizontal()) {
                setMenuActive(false);
            }
        }
    };

    const onRootMenuitemClick = () => {
        setMenuActive(prevMenuActive => !prevMenuActive);
    };

    const onMenuClick = () => {
        menuClick = true;

        if (inlineMenuActive && !inlineMenuClick) {
            setInlineMenuActive(false);
        }
    };

    const isMenuVisible = () => {
        if (isDesktop()) {
            if (menuMode === 'static')
                return !staticMenuDesktopInactive;
            else if (menuMode === 'overlay')
                return overlayMenuActive;
            else
                return true;
        } else {
            return true;
        }
    };

    const onMenuButtonClick = (event) => {
        menuClick = true;
        setTopbarMenuActive(false);

        if (isOverlay() && !isMobile()) {
            setOverlayMenuActive(prevOverlayMenuActive => !prevOverlayMenuActive);
        }
        else {
            if (isDesktop()) {
                setStaticMenuDesktopInactive(prevStaticMenuDesktopInactive => !prevStaticMenuDesktopInactive);
            }
            else {
                setStaticMenuMobileActive(prevStaticMenuMobileActive => !prevStaticMenuMobileActive);
            }
        }

        event.preventDefault();
    };

    const onProfileButtonClick = (event) => {
        setInlineMenuActive(prevInlineMenuActive => !prevInlineMenuActive);
        inlineMenuClick = true;

        if (isSlim() || isHorizontal()) {
            setMenuActive(false);
        }
    };

    const onTopbarMenuButtonClick = (event) => {
        topbarItemClick = true;
        setTopbarMenuActive(prevTopbarMenuActive => !prevTopbarMenuActive);

        hideOverlayMenu();

        event.preventDefault();
    };

    const onTopbarItemClick = (event, item) => {
        topbarItemClick = true;

        if (activeTopbarItem === item) {
            setActiveTopbarItem(null);
        }
        else {
            setActiveTopbarItem(item);
        }

        event.preventDefault();
    };

    const hideOverlayMenu = () => {
        setOverlayMenuActive(false);
        setStaticMenuMobileActive(false);
    };

    const isDesktop = () => {
        return window.innerWidth > 896;
    };

    const isMobile = () => {
        return window.innerWidth <= 896;
    };

    const isOverlay = () => {
        return menuMode === 'overlay';
    };

    const isHorizontal = () => {
        return menuMode === 'horizontal';
    };

    const isSlim = () => {
        return menuMode === 'slim';
    };

    const hasInlineProfile = profileMode === 'inline' && !isHorizontal();

    const containerClassName = classNames('layout-wrapper',
        {
            'layout-static': true,
            'layout-overlay': isOverlay(),
            'layout-overlay-active': overlayMenuActive,
            'layout-horizontal': isHorizontal(),
            'layout-slim': isSlim(),
            'layout-static-inactive': staticMenuDesktopInactive,
            'layout-mobile-active': staticMenuMobileActive,
            'layout-menu-dark': darkMenu,
            'layout-menu-light': !darkMenu,
            'p-input-filled': inputStyle === 'filled',
            'p-ripple-disabled': !ripple
        });

    const menuContainerClassName = classNames('layout-menu-container', { 'layout-menu-container-inactive': !isMenuVisible() });

    return (
        <div className={containerClassName} onClick={onDocumentClick}>
            <Tooltip ref={copyTooltipRef} target=".block-action-copy" position="bottom" content="Copied to clipboard" event="focus" />

            <AppTopbar topbarMenuActive={topbarMenuActive} activeTopbarItem={activeTopbarItem} onMenuButtonClick={onMenuButtonClick} onTopbarMenuButtonClick={onTopbarMenuButtonClick} onTopbarItemClick={onTopbarItemClick}
                isHorizontal={isHorizontal()} profileMode={profileMode} isMobile={isMobile} />

            <div className={menuContainerClassName} onClick={onMenuClick}>
                <div className="layout-menu-logo">
                    <button className="p-link" onClick={() => history.push('/')}>
                        <img id="layout-menu-logo" src="assets/layout/images/logo-white.png" library="babylon-layout" alt="babylon-logo" />
                    </button>
                </div>
                <div className="layout-menu-wrapper">
                    <div className="menu-scroll-content">
                        {hasInlineProfile && <AppInlineProfile inlineMenuActive={inlineMenuActive} onProfileButtonClick={onProfileButtonClick} />}
                        <AppMenu model={menu} menuMode={menuMode} active={menuActive} onMenuitemClick={onMenuitemClick} onRootMenuitemClick={onRootMenuitemClick} />
                    </div>
                </div>
            </div>

            <div className="layout-main">

                <AppBreadcrumb routers={routers} />

                <div className="layout-content">
                    {
                        routers.map((router, index) => {
                            if (router.exact) {
                                return <Route key={`router${index}`} path={router.path} exact component={router.component}/>
                            }

                            return <Route key={`router${index}`} path={router.path} component={router.component}/>
                        })
                    }
                </div>

                <AppFooter />
            </div>


            {staticMenuMobileActive && <div className="layout-mask"></div>}
        </div>
    );
}

export default App;
