import {useArticles} from '../../hooks/useArticles';
import {useDateTraffic, useEntityCounts} from '../../hooks/useStatistics';
import {FileText, FolderTree, Tags, Eye, ArrowUpRight, PenLine, Image as ImageIcon} from 'lucide-react';
import {useState} from 'react';
import {Link} from 'react-router-dom';
import {Card} from '@/components/ui/card';

const DAY_MS = 24 * 60 * 60 * 1000;
const DEFAULT_RANGE_DAYS = 14;
const dateInputClass = 'rounded-full border border-border bg-background px-2.5 py-1 text-[11px] text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20';

/** yyyy-MM-dd in UTC, matching the backend's UTC daily buckets */
const toIsoDate = (time: number) => new Date(time).toISOString().slice(0, 10);

/** Every yyyy-MM-dd from `from` to `to`, inclusive */
const listDays = (from: string, to: string) => {
    const days: string[] = [];
    for (let t = Date.parse(from); t <= Date.parse(to); t += DAY_MS) {
        days.push(toIsoDate(t));
    }
    return days;
};

function StatCard({
                      title,
                      value,
                      sub,
                      icon: Icon,
                  }: {
    title: string;
    value: number | string;
    sub: string;
    icon: React.ComponentType<{ size: number; className?: string }>;
}) {
    return (
        <Card className="p-5">
            <div className="flex items-start justify-between mb-3">
                <div
                    className="w-9 h-9 rounded-xl bg-muted border border-border flex items-center justify-center text-foreground">
                    <Icon size={16}/>
                </div>
            </div>
            <div className="text-[11px] tracking-wide font-medium text-muted-foreground mb-1">{title}</div>
            <div className="text-[28px] font-semibold tracking-tight text-foreground leading-none">{value}</div>
            <div className="text-[12px] text-muted-foreground mt-2">{sub}</div>
        </Card>
    );
}

export function AdminDashboardPage() {
    const {data: articlesData, isLoading: articlesLoading} = useArticles({page: 0, size: 5});
    const {data: counts} = useEntityCounts();

    const todayUtc = toIsoDate(Date.now());
    const [trafficFrom, setTrafficFrom] = useState(() => toIsoDate(Date.parse(todayUtc) - DEFAULT_RANGE_DAYS * DAY_MS));
    const [trafficTo, setTrafficTo] = useState(() => toIsoDate(Date.parse(todayUtc) - DAY_MS));
    const {data: dateTraffic} = useDateTraffic(trafficFrom, trafficTo);
    const trafficDays = trafficFrom && trafficTo && trafficFrom <= trafficTo ? listDays(trafficFrom, trafficTo) : [];
    const trafficVisits = trafficDays.map((day) => dateTraffic?.[day] ?? 0);
    const maxTrafficVisits = Math.max(0, ...trafficVisits);

    return (
        <div className="space-y-6 animate-fade-in">
            <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
                <div>
                    <h1 className="text-[22px] font-semibold tracking-tight text-foreground">Dashboard</h1>
                    <p className="text-[13px] text-muted-foreground mt-1">Overview of your content. Stay focused.</p>
                </div>
                <Link
                    to="/admin/articles/new"
                    className="inline-flex items-center gap-2 px-4 py-2.5 bg-primary text-primary-foreground text-[13px] font-medium rounded-full hover:bg-primary/90 transition-colors shadow-sm"
                >
                    <PenLine size={14}/>
                    New Article
                </Link>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatCard title="Total Articles" value={counts?.totalArticles ?? 0}
                          sub={`${counts?.publishedArticles ?? 0} published · ${counts?.draftArticles ?? 0} drafts`} icon={FileText}/>
                <StatCard title="Categories" value={counts?.categories ?? 0} sub="Organized" icon={FolderTree}/>
                <StatCard title="Tags" value={counts?.tags ?? 0} sub="For discovery" icon={Tags}/>
                <StatCard title="Pictures" value={counts?.pictures ?? 0} sub="In library" icon={ImageIcon}/>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <div className="lg:col-span-2 space-y-4">
                    <Card className="p-6">
                        <div className="flex items-center justify-between mb-6">
                            <h2 className="text-[13px] font-semibold text-foreground">Analytics</h2>
                            <div className="flex items-center gap-2 text-[11px] text-muted-foreground">
                                <input type="date" value={trafficFrom} max={trafficTo}
                                       onChange={(e) => setTrafficFrom(e.target.value)} className={dateInputClass}/>
                                <span>→</span>
                                <input type="date" value={trafficTo} min={trafficFrom} max={todayUtc}
                                       onChange={(e) => setTrafficTo(e.target.value)} className={dateInputClass}/>
                            </div>
                        </div>
                        <div className="h-[64px] flex items-end gap-1.5">
                            {trafficDays.map((day, i) => (
                                <div key={day}
                                     title={`${day}: ${trafficVisits[i].toLocaleString()} visits`}
                                     className="flex-1 rounded-full bg-gradient-to-t from-primary/15 to-primary"
                                     style={{height: maxTrafficVisits ? Math.round((trafficVisits[i] / maxTrafficVisits) * 64) : 0}}/>
                            ))}
                        </div>
                        <div className="flex justify-between mt-2 text-[11px] text-muted-foreground">
                            <span>{trafficFrom}</span>
                            <span>{trafficTo}</span>
                        </div>

                        <div className="grid grid-cols-3 gap-3 mt-6 pt-6 border-t border-border">
                            <div className="rounded-xl bg-muted p-3">
                                <div className="text-[11px] text-muted-foreground">Published</div>
                                <div className="text-[18px] font-semibold text-foreground">{counts?.publishedArticles ?? 0}</div>
                            </div>
                            <div className="rounded-xl bg-muted p-3">
                                <div className="text-[11px] text-muted-foreground">Drafts</div>
                                <div
                                    className="text-[18px] font-semibold text-amber-600 dark:text-amber-400">{counts?.draftArticles ?? 0}</div>
                            </div>
                            <div className="rounded-xl bg-muted p-3">
                                <div className="text-[11px] text-muted-foreground">Archived</div>
                                <div className="text-[18px] font-semibold text-muted-foreground">
                                    {counts?.archivedArticles ?? 0}
                                </div>
                            </div>
                        </div>
                    </Card>

                    <Card className="overflow-hidden p-0">
                        <div className="px-6 py-4 flex items-center justify-between border-b border-border">
                            <h2 className="text-[13px] font-semibold text-foreground">Recent articles</h2>
                            <Link to="/admin/articles"
                                  className="text-[12px] font-medium text-primary hover:text-primary/80 inline-flex items-center gap-1">
                                View all <ArrowUpRight size={12}/>
                            </Link>
                        </div>
                        {articlesLoading ? (
                            <div className="p-8 text-center text-[13px] text-muted-foreground">Loading…</div>
                        ) : (
                            <div className="divide-y divide-border">
                                {articlesData?.content.slice(0, 5).map((article) => (
                                    <div key={article.id}
                                         className="px-6 py-4 flex items-center justify-between hover:bg-muted transition-colors">
                                        <div className="min-w-0 flex-1">
                                            <p className="text-[13px] font-medium text-foreground truncate pr-4">{article.title}</p>
                                            <p className="text-[12px] text-muted-foreground mt-1">
                                                {article.category ? article.category.name : 'Uncategorized'}
                                                ·{' '}
                                                {article.createdAt ? new Date(article.createdAt).toLocaleDateString() : ''}
                                            </p>
                                        </div>
                                        <span
                                            className={`shrink-0 px-2.5 py-1 rounded-full text-[11px] font-medium border ${
                                                article.status === 'PUBLISHED'
                                                    ? 'bg-green-50 dark:bg-green-500/15 text-green-700 dark:text-green-400 border-green-200 dark:border-green-500/30'
                                                    : article.status === 'DRAFT'
                                                        ? 'bg-amber-50 dark:bg-amber-500/15 text-amber-700 dark:text-amber-400 border-amber-200 dark:border-amber-500/30'
                                                        : 'bg-muted text-muted-foreground border-border'
                                            }`}
                                        >
 {article.status}
 </span>
                                    </div>
                                ))}
                                {(!articlesData || articlesData.content.length === 0) && (
                                    <div className="p-8 text-center text-[13px] text-muted-foreground">No articles yet.
                                        Create your first one.</div>
                                )}
                            </div>
                        )}
                    </Card>
                </div>

                <div className="space-y-4">
                    <Card className="p-5">
                        <h3 className="text-[13px] font-semibold text-foreground mb-3">Quick actions</h3>
                        <div className="grid grid-cols-2 gap-2">
                            <Link
                                to="/admin/articles/new"
                                className="rounded-xl border border-border p-4 hover:border-primary/30 hover:bg-primary/5 transition-colors group"
                            >
                                <FileText size={18} className="text-foreground group-hover:text-primary"/>
                                <div className="text-[13px] font-medium text-foreground mt-2">Write</div>
                                <div className="text-[11px] text-muted-foreground">Markdown</div>
                            </Link>
                            <Link
                                to="/admin/pictures"
                                className="rounded-xl border border-border p-4 hover:border-primary/30 hover:bg-primary/5 transition-colors group"
                            >
                                <Eye size={18} className="text-foreground group-hover:text-primary"/>
                                <div className="text-[13px] font-medium text-foreground mt-2">Upload</div>
                                <div className="text-[11px] text-muted-foreground">Images</div>
                            </Link>
                            <Link to="/admin/categories"
                                  className="rounded-xl border border-border p-4 hover:bg-muted transition-colors">
                                <FolderTree size={18} className="text-foreground"/>
                                <div className="text-[13px] font-medium text-foreground mt-2">Categories</div>
                                <div className="text-[11px] text-muted-foreground">{counts?.categories ?? 0} items</div>
                            </Link>
                            <Link to="/admin/tags"
                                  className="rounded-xl border border-border p-4 hover:bg-muted transition-colors">
                                <Tags size={18} className="text-foreground"/>
                                <div className="text-[13px] font-medium text-foreground mt-2">Tags</div>
                                <div className="text-[11px] text-muted-foreground">{counts?.tags ?? 0} items</div>
                            </Link>
                        </div>
                    </Card>
                </div>
            </div>
        </div>
    );
}
