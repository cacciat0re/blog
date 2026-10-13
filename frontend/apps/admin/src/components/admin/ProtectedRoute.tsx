/**
 * Protected Route — OIDC-based guard for admin pages
 */
import {useEffect, useState} from 'react';
import {Navigate} from 'react-router-dom';
import {hasAdminRole, isAuthenticated} from '../../api/auth';
import {AdminForbiddenPage} from '../../features/login/AdminForbiddenPage';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

type AccessState = 'checking' | 'unauthenticated' | 'forbidden' | 'granted';

export function ProtectedRoute({children}: ProtectedRouteProps) {
    const [access, setAccess] = useState<AccessState>('checking');

    useEffect(() => {
        Promise.all([isAuthenticated(), hasAdminRole()]).then(([authed, admin]) => {
            if (!authed) setAccess('unauthenticated');
            else setAccess(admin ? 'granted' : 'forbidden');
        });
    }, []);

    if (access === 'checking') {
        return (
            <div className="min-h-screen flex items-center justify-center bg-card">
                <div className="w-8 h-8 border-2 border-gray-200 dark:border-gray-700 border-t-primary rounded-full animate-spin"/>
            </div>
        );
    }

    if (access === 'unauthenticated') return <Navigate to="/login" replace/>;

    if (access === 'forbidden') return <AdminForbiddenPage/>;

    return <>{children}</>;
}