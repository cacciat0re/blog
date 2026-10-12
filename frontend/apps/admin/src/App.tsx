import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
import {ThemeProvider} from './context/ThemeContext';
import {AdminLayout} from './components/admin/AdminLayout';
import {ProtectedRoute} from './components/admin/ProtectedRoute';
import {AdminLoginPage} from './features/login/AdminLoginPage';
import {CallbackPage} from './features/login/CallbackPage';
import {AdminDashboardPage} from './features/dashboard/AdminDashboardPage';
import {AdminArticlesPage} from './features/articles/AdminArticlesPage';
import {AdminArticleEditPage} from './features/articles/AdminArticleEditPage';
import {AdminCategoriesPage} from './features/categories/AdminCategoriesPage';
import {AdminTagsPage} from './features/tags/AdminTagsPage';
import {AdminUsersPage} from './features/users/AdminUsersPage';
import {AdminPicturesPage} from './features/pictures/AdminPicturesPage';
import {AdminOutboundRequestsPage} from './features/outbound/AdminOutboundRequestsPage';

const queryClient = new QueryClient({
    defaultOptions: {queries: {staleTime: 5 * 60 * 1000, retry: 1}},
});

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <ThemeProvider>
                <BrowserRouter>
                    <Routes>
                        <Route path="/login" element={<AdminLoginPage/>}/>
                        <Route path="/callback" element={<CallbackPage/>}/>
                        <Route
                            path="/admin"
                            element={<ProtectedRoute><AdminLayout><AdminDashboardPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/articles"
                            element={<ProtectedRoute><AdminLayout><AdminArticlesPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/articles/new"
                            element={
                                <ProtectedRoute><AdminLayout><AdminArticleEditPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/articles/:id"
                            element={
                                <ProtectedRoute><AdminLayout><AdminArticleEditPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/categories"
                            element={<ProtectedRoute><AdminLayout><AdminCategoriesPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/tags"
                            element={<ProtectedRoute><AdminLayout><AdminTagsPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/users"
                            element={<ProtectedRoute><AdminLayout><AdminUsersPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/pictures"
                            element={<ProtectedRoute><AdminLayout><AdminPicturesPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route
                            path="/admin/outbound-requests"
                            element={
                                <ProtectedRoute><AdminLayout><AdminOutboundRequestsPage/></AdminLayout></ProtectedRoute>}
                        />
                        <Route path="/" element={<Navigate to="/admin" replace/>}/>
                        <Route path="*" element={<Navigate to="/admin" replace/>}/>
                    </Routes>
                </BrowserRouter>
            </ThemeProvider>
        </QueryClientProvider>
    );
}

export default App;