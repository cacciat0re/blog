/**
 * OIDC/OAuth2 authentication via Keycloak
 */
import {UserManager, type User} from 'oidc-client-ts';

const OIDC_CONFIG = {
    authority: import.meta.env.VITE_OAUTH_AUTH_SERVER,
    client_id: import.meta.env.VITE_OAUTH_CLIENT_ID,
    redirect_uri: import.meta.env.VITE_OAUTH_REDIRECT_URI,
    post_logout_redirect_uri: window.location.origin + '/login',
    response_type: 'code',
    scope: 'openid profile email',
};

let userManager: UserManager | null = null;

export const getUserManager = (): UserManager => {
    if (!userManager) {
        userManager = new UserManager(OIDC_CONFIG);
    }
    return userManager;
};

export const login = (): void => {
    getUserManager().signinRedirect();
};

export const logout = async (): Promise<void> => {
    await getUserManager().signoutRedirect();
};

export const handleCallback = async (): Promise<User | null> => {
    try {
        const user = await getUserManager().signinRedirectCallback();
        return user;
    } catch {
        return null;
    }
};

export const getAccessToken = async (): Promise<string | null> => {
    try {
        const user = await getUserManager().getUser();
        return user?.access_token ?? null;
    } catch {
        return null;
    }
};

export const isAuthenticated = async (): Promise<boolean> => {
    try {
        const user = await getUserManager().getUser();
        return !!user && !user.expired;
    } catch {
        return false;
    }
};

export const getUserInfo = async (): Promise<{ username: string; email: string } | null> => {
    try {
        const user = await getUserManager().getUser();
        if (!user) return null;
        return {
            username: user.profile.preferred_username ?? user.profile.sub,
            email: user.profile.email ?? '',
        };
    } catch {
        return null;
    }
};

const ADMIN_ROLE = 'admin';

type AccessTokenClaims = {
    resource_access?: Record<string, { roles?: string[] }>;
};

const decodeAccessTokenClaims = (token: string): AccessTokenClaims | null => {
    try {
        const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        const bytes = Uint8Array.from(atob(base64), c => c.charCodeAt(0));
        return JSON.parse(new TextDecoder().decode(bytes));
    } catch {
        return null;
    }
};

/**
 * UI-only check, mirrors backend KeycloakAuthenticationConverter: client roles of OAUTH_CLIENT_ID, not realm roles.
 * Backend @PreAuthorize/hasRole("admin") on /api/admin/** remains the enforcement boundary.
 */
export const hasAdminRole = async (): Promise<boolean> => {
    try {
        const user = await getUserManager().getUser();
        if (!user || user.expired) return false;
        const claims = decodeAccessTokenClaims(user.access_token);
        const roles = claims?.resource_access?.[import.meta.env.VITE_OAUTH_CLIENT_ID]?.roles;
        return roles?.includes(ADMIN_ROLE) ?? false;
    } catch {
        return false;
    }
};

/**
 * Backend admin auth — GET /api/admin/auth/me
 * @see AuthController @RequestMapping("/api/admin/auth") + @GetMapping("/me")
 */
export const fetchMe = async (): Promise<unknown> => {
    const {get} = await import('./client');
    return get<unknown>('/api/admin/auth/me');
};