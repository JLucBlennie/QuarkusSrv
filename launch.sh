#!/bin/sh
echo Lancement du Calendrier CTR

# Charge les secrets (fichier .env local, jamais commité)
if [ -f .env ]; then
  set -a
  . ./.env
  set +a
fi

cd calendrier-server
echo Compilation du Server
./gradlew build

echo Lancement du server
java -jar build/quarkus-app/quarkus-run.jar &

cd ..
cd web.ui
echo Mise à jour du Front End
npm install
echo Compilation du Front End
npm run build
echo Lancement du Front End
npm run start
cd ..