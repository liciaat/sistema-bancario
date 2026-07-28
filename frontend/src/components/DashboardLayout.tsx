import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

export default function DashboardLayout() {
    return (
        <div className="flex min-h-screen bg-[#0a0a0a] text-white font-inter">
            <Sidebar />
            <main className="flex-1 h-screen overflow-y-auto">
                <div className="max-w-7xl mx-auto p-8">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}