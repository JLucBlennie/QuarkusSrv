#!/bin/sh
# Permet de stopper le script à la première erreur rencontrée.
set -e
# Affichage des lignes de commande exécutées (décommenter pour débogage)
# set -x 

VPS_USER="debian"
VPS_HOST="51.254.209.21"

# Fonction pour calculer un hash agrégé d'un dossier (indépendant de l'ordre des fichiers)
dir_hash() {
    find "$1" -type f ! -name "BUILD_ID" -exec md5sum {} \; 2>/dev/null | sort | md5sum | cut -d' ' -f1
}

echo "=== Déploiement du Calendrier CTR ==="

# --- BACKEND ---
cd calendrier-server

JAR_PATH="build/quarkus-app/quarkus-run.jar"
HASH_BEFORE=""
if [ -f "$JAR_PATH" ]; then
    HASH_BEFORE=$(md5sum "$JAR_PATH" | cut -d' ' -f1)
fi

echo "--- Compilation du Server ---"
./gradlew quarkusBuild

HASH_AFTER=$(md5sum "$JAR_PATH" | cut -d' ' -f1)

if [ "$HASH_BEFORE" = "$HASH_AFTER" ]; then
    echo "--- Backend déjà à jour, aucun déploiement nécessaire ---"
else
    echo "--- Envoi sur le serveur ---"
    pscp -r build/quarkus-app "$VPS_USER@$VPS_HOST:/opt/calendrier-ctr/"

    echo "--- Relancement du server ---"
    plink "$VPS_USER@$VPS_HOST" "sudo systemctl restart calendrier-ctr-backend.service"
fi

cd ..

# --- FRONTEND ---
cd web.ui

NEXT_HASH_BEFORE=""
if [ -d ".next" ]; then
    NEXT_HASH_BEFORE=$(dir_hash ".next")
fi

echo "--- Compilation du Front End (switch-env + build) ---"
./switch-env.sh prod

NEXT_HASH_AFTER=$(dir_hash ".next")

if [ "$NEXT_HASH_BEFORE" = "$NEXT_HASH_AFTER" ]; then
    echo "--- Frontend déjà à jour, aucun déploiement nécessaire ---"
else
    echo "--- Envoi sur le serveur ---"
    pscp -r .next package.json package-lock.json next.config.mjs "$VPS_USER@$VPS_HOST:/opt/calendrier-ctr/web.ui/"

    echo "--- Installation des dépendances sur le serveur ---"
    plink "$VPS_USER@$VPS_HOST" "cd /opt/calendrier-ctr/web.ui && npm ci --omit=dev --legacy-peer-deps"

    echo "--- Lancement du Front End ---"
    plink "$VPS_USER@$VPS_HOST" "sudo systemctl restart calendrier-ctr-frontend.service"
fi

cd ..

echo "=== Fin du déploiement ==="
read -p "Appuie sur Entrée pour fermer..."