/** @type {import('next').NextConfig} */
const nextConfig = {
  // Pas besoin de basePath, car NextJS est à la racine
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        // En production (VPS), proxy vers Quarkus sur le même serveur
        destination: process.env.NODE_ENV === 'production'
          ? 'http://api.blondy29.ovh/ctr/:path*'
          : 'http://localhost:9090/ctr/:path*', // Pour le dev local, si Quarkus tourne aussi en local
      },
    ];
  },
};

export default nextConfig;
