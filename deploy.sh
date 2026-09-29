#!/bin/sh
SSH_KEY="/d/jluc_/Documents/Dev/ssh-Putty/id_ed25519.ppk"

echo "--- Chargement de la clé dans Pageant ---"
pageant "$SSH_KEY" &

read -p "Saisis la passphrase dans la popup Pageant, puis appuie sur Entrée ici pour continuer... "

echo "--- Lancement du déploiement ---"
./deploy-inner.sh 2>&1 | tee deploy.log

echo "--- Fermeture de Pageant ---"
taskkill //IM pageant.exe //F