/**
 * Shown to signed-in users who lack the admin role — they can't reach the admin API either
 */
import {logout} from '../../api/auth';

export function AdminForbiddenPage() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-card">
            <div className="text-center">
                <p className="text-[11px] font-mono text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-4">
                    403 — Admin access required
                </p>
                <p className="text-[13px] text-muted-foreground mb-6">
                    This account does not have the admin role.
                </p>
                <button
                    onClick={() => logout()}
                    className="px-4 py-2 rounded-full bg-primary text-white text-[13px] font-medium"
                >
                    Sign out
                </button>
            </div>
        </div>
    );
}