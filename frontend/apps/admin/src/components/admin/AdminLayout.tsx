import type {ReactNode} from 'react';
import {useState} from 'react';
import {Link, useLocation} from 'react-router-dom';
import {
    LayoutDashboard,
    FileText,
    FolderTree,
    Tags,
    Users,
    Image,
    Send,
    LogOut,
    ArrowLeft,
    Search,
    Menu,
    X,
    Command,
    Sun,
    Moon,
    Monitor,
} from 'lucide-react';
import {useAuth} from '@/hooks/useAuth.ts';
import {useTheme, type Theme} from '@/context/ThemeContext';
import {cn} from '@/lib/utils';

interface AdminLayoutProps {
    children: ReactNode;
}

const navGroups = [
    {
        title: 'Overview',
        items: [{path: '/admin', label: 'Dashboard', icon: LayoutDashboard}],
    },
    {
        title: 'Content',
        items: [
            {path: '/admin/articles', label: 'Articles', icon: FileText},
            {path: '/admin/categories', label: 'Categories', icon: FolderTree},
            {path: '/admin/tags', label: 'Tags', icon: Tags},
            {path: '/admin/pictures', label: 'Pictures', icon: Image},
        ],
    },
    {
        title: 'System',
        items: [
            {path: '/admin/users', label: 'Users', icon: Users},
            {path: '/admin/outbound-requests', label: 'Outbound', icon: Send},
        ],
    },
];

function NavLink({
                     to,
                     icon: Icon,
                     label,
                     active,
                     onClick,
                 }: {
    to: string;
    icon: typeof LayoutDashboard;
    label: string;
    active: boolean;
    onClick?: () => void;
}) {
    return (
        <Link
            to={to}
            onClick={onClick}
            className={cn(
                'flex items-center gap-2.5 px-3 py-2 rounded-lg text-[13px] leading-none transition-all',
                active
                    ? 'bg-primary text-primary-foreground shadow-sm'
                    : 'text-muted-foreground hover:text-foreground hover:bg-muted'
            )}
        >
            <Icon size={16} strokeWidth={active ? 2.2 : 1.8}/>
            <span className={active ? 'font-medium' : 'font-normal'}>{label}</span>
        </Link>
    );
}

function ThemeSwitcher() {
    const {theme, setTheme} = useTheme();
    const opts: { v: Theme; icon: ReactNode; label: string }[] = [
        {v: 'system', icon: <Monitor size={12}/>, label: 'Auto'},
        {v: 'light', icon: <Sun size={12}/>, label: 'Light'},
        {v: 'dark', icon: <Moon size={12}/>, label: 'Dark'},
    ];
    return (
        <div className="flex items-center p-1 rounded-full bg-muted border border-border">
            {opts.map((o) => (
                <button
                    key={o.v}
                    onClick={() => setTheme(o.v)}
                    className={cn(
                        'flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-medium transition-colors',
                        theme === o.v
                            ? 'bg-card text-foreground shadow-sm border border-border'
                            : 'text-muted-foreground hover:text-foreground'
                    )}
                    title={o.label}
                >
                    {o.icon}
                    <span className="hidden xl:inline">{o.label}</span>
                </button>
            ))}
        </div>
    );
}

export function AdminLayout({children}: AdminLayoutProps) {
    const location = useLocation();
    const {user, logout} = useAuth();
    const [mobileOpen, setMobileOpen] = useState(false);

    const isActive = (path: string) => {
        if (path === '/admin') return location.pathname === '/admin';
        return location.pathname.startsWith(path);
    };

    const currentLabel =
        navGroups.flatMap((g) => g.items).find((i) => isActive(i.path))?.label ?? 'Dashboard';

    return (
        <div className="min-h-screen bg-background flex">
            {mobileOpen && (
                <button
                    aria-label="close sidebar"
                    onClick={() => setMobileOpen(false)}
                    className="fixed inset-0 bg-black/20 backdrop-blur-sm z-30 lg:hidden"
                />
            )}

            <aside
                className={cn(
                    'fixed lg:sticky top-0 z-40 h-screen w-[272px] bg-card border-r border-border flex flex-col shrink-0 transition-transform duration-200 lg:translate-x-0',
                    mobileOpen ? 'translate-x-0' : '-translate-x-full'
                )}
            >
                <div className="h-[64px] px-6 flex items-center justify-between shrink-0">
                    <Link to="/admin" className="flex items-center gap-2.5" onClick={() => setMobileOpen(false)}>
                        <div className="w-7 h-7 rounded-lg bg-foreground flex items-center justify-center">
                            <span className="text-background text-[11px] font-bold tracking-tight">A</span>
                        </div>
                        <div className="leading-none">
                            <div className="text-[13px] font-semibold tracking-tight text-foreground">
                                ARORMS<span className="text-primary">.</span>
                            </div>
                            <div className="text-[11px] text-muted-foreground tracking-wide font-medium -mt-0.5">ADMIN
                            </div>
                        </div>
                    </Link>
                    <button
                        onClick={() => setMobileOpen(false)}
                        className="lg:hidden p-1.5 rounded-lg hover:bg-muted text-muted-foreground"
                    >
                        <X size={16}/>
                    </button>
                </div>

                <div className="px-3 pb-4">
                    <div
                        className="flex items-center gap-2 px-3 py-2 rounded-lg bg-muted border border-transparent hover:border-border transition-colors group cursor-pointer">
                        <Search size={14} className="text-muted-foreground group-hover:text-foreground"/>
                        <span className="flex-1 text-[13px] text-muted-foreground">Search…</span>
                        <span
                            className="hidden xl:flex items-center gap-1 text-[11px] text-muted-foreground border border-border bg-card px-1.5 py-0.5 rounded">
 <Command size={10}/>K
 </span>
                    </div>
                </div>

                <nav className="flex-1 px-3 py-2 space-y-6 overflow-y-auto">
                    {navGroups.map((group) => (
                        <div key={group.title}>
                            <div
                                className="px-3 mb-2 text-[11px] font-medium tracking-wide text-muted-foreground">{group.title}</div>
                            <ul className="space-y-1">
                                {group.items.map((item) => (
                                    <li key={item.path}>
                                        <NavLink
                                            to={item.path}
                                            icon={item.icon}
                                            label={item.label}
                                            active={isActive(item.path)}
                                            onClick={() => setMobileOpen(false)}
                                        />
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </nav>

                <div className="p-3 border-t border-border/60 space-y-3">
                    <div className="flex justify-center">
                        <ThemeSwitcher/>
                    </div>

                    <Link
                        to="/"
                        className="flex items-center gap-2 px-3 py-2 rounded-lg text-[13px] text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
                    >
                        <ArrowLeft size={14}/>
                        Back to site
                    </Link>

                    <div className="flex items-center gap-3 px-3 py-2.5 rounded-xl bg-muted border border-border">
                        <div
                            className="w-8 h-8 rounded-full bg-foreground flex items-center justify-center text-background text-[12px] font-medium shrink-0">
                            {(user?.username?.[0] ?? 'A').toUpperCase()}
                        </div>
                        <div className="flex-1 min-w-0">
                            <div className="text-[13px] font-medium text-foreground truncate leading-none">
                                {user?.username ?? 'Admin'}
                            </div>
                            <div className="text-[11px] text-muted-foreground truncate">Administrator</div>
                        </div>
                        <button
                            onClick={logout}
                            className="p-1.5 rounded-lg hover:bg-card text-muted-foreground hover:text-foreground transition-colors"
                            title="Sign out"
                        >
                            <LogOut size={14}/>
                        </button>
                    </div>
                </div>
            </aside>

            <div className="flex-1 min-w-0 flex flex-col">
                <header
                    className="sticky top-0 z-20 h-[56px] bg-background/80 backdrop-blur-xl border-b border-border flex items-center gap-4 px-4 lg:px-8">
                    <button
                        onClick={() => setMobileOpen(true)}
                        className="lg:hidden p-2 -ml-2 rounded-lg hover:bg-card border border-transparent hover:border-border text-foreground"
                    >
                        <Menu size={18}/>
                    </button>

                    <div className="flex items-center gap-2 text-[13px]">
                        <span className="text-muted-foreground hidden sm:inline">Admin</span>
                        <span className="text-border hidden sm:inline">/</span>
                        <span className="font-medium text-foreground">{currentLabel}</span>
                    </div>
                </header>

                <main className="flex-1 px-4 lg:px-8 py-6 lg:py-8 max-w-[1280px] w-full mx-auto">
                    {children}
                </main>
            </div>
        </div>
    );
}
