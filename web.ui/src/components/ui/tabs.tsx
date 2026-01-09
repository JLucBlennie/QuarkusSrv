// components/Tabs.tsx
import React, { ReactNode, useState } from 'react';

// Définition des props pour un onglet
type TabProps = {
    label: string;
    children: ReactNode;
};

// Composant pour un onglet individuel
const Tab: React.FC<TabProps> = ({ children }) => {
    return <div className="p-4">{children}</div>;
};

// Définition des props pour le conteneur d'onglets
type TabsProps = {
    children: React.ReactElement<TabProps>[];
};

// Composant principal pour les onglets
const Tabs: React.FC<TabsProps> & { Tab: React.FC<TabProps> } = ({ children }) => {
    const [activeTab, setActiveTab] = useState(0);

    return (
        <div className="w-full mx-auto">
            {/* Barre d'onglets */}
            <div className="flex border-b border-gray-200">
                {React.Children.map(children, (child, index) => (
                    <button
                        key={index}
                        className={`bg-white rounded-t-lg px-4 py-2 font-medium text-sm focus:outline-none ${activeTab === index
                                ? 'border-b-2 border-blue-500 text-blue-600'
                                : 'text-gray-500 hover:text-gray-700'
                            }`}
                        onClick={() => setActiveTab(index)}
                    >
                        {child.props.label}
                    </button>
                ))}
            </div>

            {/* Contenu des onglets */}
            <div className="rounded-b-lg shadow-sm">
                {React.Children.toArray(children)[activeTab]}
            </div>
        </div>
    );
};

// Attache le sous-composant Tab au composant parent Tabs
Tabs.Tab = Tab;

export default Tabs;
