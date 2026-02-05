# Checklist pour la mise en production de ton API Quarkus

1. **Nom de domaine** (optionnel mais recommandé) :
    - [x] Acheter un domaine chez OVH : [Domaine](http://api.blondy29.ovh). 
    - [x] Configurer les DNS pour pointer vers l’IP de ton VPS. 

2. **HTTPS avec Let’s Encrypt** :
    - Installer Certbot pour générer un certificat SSL gratuit.
    - Configurer Nginx pour rediriger HTTP → HTTPS.

3. **Sécurité de l’API** :
    - Implémenter JWT pour l’authentification (quarkus-smallrye-jwt).
    - Protéger les endpoints avec @RolesAllowed.

4. **Base de données PostgreSQL** :
    - Vérifier les permissions des rôles (ex. api_user).
    - Chiffrer les données sensibles (ex. mots de passe avec BCrypt).

5. **Déploiement** :
    - Construire ton application avec `./gradlew quarkusBuild`.
    - Lancer le JAR en production avec `java -jar quarkus-run.jar`.

6. **Monitoring** :
    - Utiliser htop ou tmux pour surveiller les processus sur ton VPS.
    - Configurer des logs avec `quarkus-logging-json`.

# Commandes utiles à garder sous la main

## Installer Certbot pour HTTPS
`sudo apt install certbot python3-certbot-nginx` <br>
`sudo certbot --nginx -d mon-api.com`

## Lancer Quarkus en production
`java -jar ~/quarkus-app/quarkus-run.jar`

## Vérifier les logs
`tail -f /var/log/nginx/error.log` <br>
`journalctl -u nginx -f`

# Quand tu seras prêt, on pourra :
- [x] 📋 Configurer ton nom de domaine et HTTPS.
- [ ] Sécuriser ton API avec JWT.
- [ ] Optimiser les performances sur ton VPS (2 Go de RAM).

🚀🔥📅📊🐛📝🧪🔍⚡🎨📱💡