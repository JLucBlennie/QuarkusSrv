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

# Impémentation des comptes
Vous êtes sur la bonne voie ! Pour mettre en place une page de login et une gestion de comptes statiques (sans base de données utilisateur), voici une solution simple, sécurisée et adaptée à votre stack (NextJS + Quarkus + Nginx). Je vais vous proposer une approche sans base de données utilisateur (juste des comptes statiques), avec des fonctionnalités adaptées aux rôles.

## 1. Architecture proposée


### Frontend (NextJS) :

- Page de login (/login).
Gestion des rôles (ex: admin, user) via des jetons JWT signés.
Protection des routes côté frontend.


- Backend (Quarkus) :
Endpoint de login pour valider les identifiants statiques et générer un JWT.
Middleware pour vérifier le JWT sur les routes protégées.


- Nginx :
Redirection des requêtes vers Quarkus/NextJS.
(Optionnel) Restriction d’accès par IP ou authentification basique pour les routes sensibles.


## 2. Étapes pour NextJS (Frontend)
### Installation des dépendances
`npm install jwt-decode js-cookie`

### Création de la page de login
Fichier : pages/login.js
```typescript
import { useState } from 'react';
import { useRouter } from 'next/router';
import Cookies from 'js-cookie';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const router = useRouter();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('https://ctr.blondy29.ovh/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      if (!response.ok) throw new Error('Identifiants invalides');
      const { token } = await response.json();
      Cookies.set('authToken', token, { expires: 1 }); // Token valide 1 jour
      router.push('/dashboard'); // Redirection après login
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Connexion</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Utilisateur"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Mot de passe"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Se connecter</button>
      </form>
    </div>
  );
}
```

### Protection des routes côté frontend
Fichier : components/ProtectedRoute.js
```typescript
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Cookies from 'js-cookie';
import jwtDecode from 'jwt-decode';

export default function ProtectedRoute({ children, requiredRole }) {
  const router = useRouter();
  useEffect(() => {
    const token = Cookies.get('authToken');
    if (!token) {
      router.push('/login');
      return;
    }
    try {
      const decoded = jwtDecode(token);
      if (requiredRole && decoded.role !== requiredRole) {
        router.push('/unauthorized');
      }
    } catch (err) {
      router.push('/login');
    }
  }, []);
  return children;
}
```

### Utilisation dans une page protégée
```typescript
// pages/dashboard.js
import ProtectedRoute from '../components/ProtectedRoute';

function Dashboard() {
  return (
    <ProtectedRoute requiredRole="admin">
      <div>Tableau de bord (réservé aux admins)</div>
    </ProtectedRoute>
  );
}
export default Dashboard;
```

## 3. Étapes pour Quarkus (Backend)
### Ajout des dépendances
Dans votre pom.xml, ajoutez :
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-jwt-build</artifactId>
</dependency>

### Configuration des comptes statiques
Fichier : src/main/resources/application.properties
```properties
# Clé secrète pour signer les JWT (à générer via `openssl rand -base64 32`)
smallrye.jwt.sign.key=votre_cle_secrete_ici

# Comptes statiques (utilisateurs/mots de passe/rôles)
quarkus.security.users.embedded.enabled=true
quarkus.security.users.embedded.plain-text=true
quarkus.security.users.embedded.users.admin=admin123,admin
quarkus.security.users.embedded.users.user=user123,user
```

### Création de l’endpoint de login
Fichier : src/main/java/org/acme/LoginResource.java
```java
package org.acme;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Path("/api/login")
public class LoginResource {

    @POST
    public Response login(LoginRequest request) {
        // Vérification des identifiants (statiques)
        if (!isValidUser(request.username, request.password)) {
            return Response.status(401).entity("Identifiants invalides").build();
        }

        // Génération du JWT
        String token = Jwt.issuer("quarkus")
                .upn(request.username)
                .groups(getRoles(request.username))
                .expiresIn(Duration.ofHours(24))
                .sign();

        return Response.ok(new LoginResponse(token)).build();
    }

    private boolean isValidUser(String username, String password) {
        // Logique de validation (exemple simplifié)
        return ("admin".equals(username) && "admin123".equals(password)) ||
               ("user".equals(username) && "user123".equals(password));
    }

    private Set<String> getRoles(String username) {
        Set<String> roles = new HashSet<>();
        if ("admin".equals(username)) roles.add("admin");
        else roles.add("user");
        return roles;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class LoginResponse {
        public String token;
        public LoginResponse(String token) { this.token = token; }
    }
}
```

### Protection des endpoints avec JWT
Fichier : src/main/java/org/acme/SecuredResource.java
```java
package org.acme;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api/protected")
public class SecuredResource {

    @GET
    @RolesAllowed({"admin", "user"}) // Accès réservé aux rôles admin/user
    public String protectedEndpoint() {
        return "Accès autorisé !";
    }

    @GET
    @Path("/admin")
    @RolesAllowed({"admin"}) // Accès réservé aux admins
    public String adminEndpoint() {
        return "Accès admin autorisé !";
    }
}
```

## 4. Configuration de Nginx
Aucune modification nécessaire
Votre configuration Nginx actuelle (ctr.blondy29.ovh/api/... → localhost:9090/ctr/...) fonctionnera telle quelle pour les endpoints de login et protégés.

## 5. Gestion des comptes statiques
Ajout/modification des utilisateurs
Modifiez simplement application.properties :
```properties
quarkus.security.users.embedded.users.nouveau=user456,user
```
→ Redémarrez Quarkus pour appliquer les changements.

## 6. Test et validation
Test du login
`curl -X POST https://ctr.blondy29.ovh/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'`

→ Doit retourner un JWT.
Test d’un endpoint protégé
`curl -X GET https://ctr.blondy29.ovh/api/protected \
  -H "Authorization: Bearer VOTRE_JWT_ICI"`

→ Doit retourner "Accès autorisé !".

## 7. Sécurité supplémentaire
Restriction par IP dans Nginx
Si vous voulez limiter l’accès à certaines IPs :
```nginx
location /api/login {
    allow 192.168.1.100;  # Votre IP locale
    deny all;
    proxy_pass http://localhost:9090/ctr/login;
    # ... (autres directives)
}
```
HTTPS obligatoire
Votre configuration actuelle avec Let’s Encrypt est déjà sécurisée.

## 8. Résumé des fonctionnalités
    
| Fonctionnalité | Implémentation |
|----------------|----------------|
| Page de login | NextJS (/login) |
| Validation des identifiants | Quarkus (comptes statiques) |
| Génération de JWT | Quarkus (SmallRye JWT) |
| Protection des routes | NextJS (frontend) + Quarkus (backend) |
| Gestion des rôles | JWT (claims groups) |
| Stockage du token | Cookies (côté frontend) |

## 9. Dashboard conditionnel (NextJS)
Exemple de page dashboard.js
```javascript
// pages/dashboard.js
import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Cookies from 'js-cookie';
import jwtDecode from 'jwt-decode';
import ProtectedRoute from '../components/ProtectedRoute';

function Dashboard() {
  const [role, setRole] = useState('');
  const router = useRouter();

  useEffect(() => {
    const token = Cookies.get('authToken');
    if (token) {
      const decoded = jwtDecode(token);
      setRole(decoded.groups[0]); // Supposons que le rôle est le premier groupe
    }
  }, []);

  return (
    <ProtectedRoute requiredRole={null}> {/* Pas de rôle spécifique requis */}
      <div className="p-4">
        <h1 className="text-2xl font-bold mb-4">Tableau de bord</h1>
        {role === 'admin' && (
          <div className="bg-blue-100 p-4 mb-4 rounded">
            <h2 className="text-xl font-semibold">Section Admin</h2>
            <p>Accès réservé aux administrateurs.</p>
            <button
              onClick={() => router.push('/admin')}
              className="bg-blue-500 text-white px-4 py-2 rounded mt-2"
            >
              Gérer les utilisateurs
            </button>
          </div>
        )}
        <div className="bg-gray-100 p-4 rounded">
          <h2 className="text-xl font-semibold">Section Utilisateur</h2>
          <p>Contenu accessible à tous les rôles.</p>
          <div className="mt-2">
            <p>Bonjour, vous êtes connecté en tant que <strong>{role}</strong>.</p>
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
}

export default Dashboard;
```

Exemple de page admin (admin.js)
```javascript
// pages/admin.js
import ProtectedRoute from '../components/ProtectedRoute';

function AdminPage() {
  return (
    <ProtectedRoute requiredRole="admin">
      <div className="p-4">
        <h1 className="text-2xl font-bold mb-4">Panneau d'administration</h1>
        <div className="bg-red-100 p-4 rounded">
          <p>Cette page est réservée aux administrateurs.</p>
          <ul className="mt-2">
            <li>• Gestion des utilisateurs</li>
            <li>• Configuration du système</li>
            <li>• Logs d'activité</li>
          </ul>
        </div>
      </div>
    </ProtectedRoute>
  );
}

export default AdminPage;
```
- Style (optionnel)
Ajoutez Tailwind CSS à votre projet NextJS si ce n’est pas déjà fait :
`npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init
`
Puis ajoutez les chemins dans tailwind.config.js :
```javascript
module.exports = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

## 10. Script pour générer des JWT (Node.js)
Fichier : generate-jwt.js
```javascript
const jwt = require('jsonwebtoken');
const fs = require('fs');

// Remplacez par votre clé secrète (la même que dans Quarkus)
const SECRET_KEY = 'votre_cle_secrete_ici';

// Générer un JWT pour un utilisateur
function generateToken(username, role) {
  return jwt.sign(
    {
      sub: username,
      groups: [role],
      iss: 'quarkus',
      exp: Math.floor(Date.now() / 1000) + 24 * 60 * 60, // Expire dans 24h
    },
    SECRET_KEY,
    { algorithm: 'HS256' }
  );
}

// Exemple d'utilisation
const token = generateToken('admin', 'admin');
console.log('JWT généré pour admin:', token);

// Sauvegarder dans un fichier (optionnel)
fs.writeFileSync('admin.token', token);
console.log('Token sauvegardé dans admin.token');
```
- Utilisation
Installez jsonwebtoken :
`npm install jsonwebtoken`

Exécutez le script :
`node generate-jwt.js`

Le token sera affiché et sauvegardé dans admin.token.
Test avec curl
`curl -X GET https://ctr.blondy29.ovh/api/protected \
  -H "Authorization: Bearer $(cat admin.token)"`

## 11. Système de logs pour tracer les connexions (Quarkus)
Ajout des dépendances
Dans pom.xml :
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-logging-json</artifactId>
</dependency>

Configuration des logs
Fichier : src/main/resources/application.properties
```properties
# Activation des logs JSON
quarkus.log.console.json=true

# Configuration du logger pour les connexions
quarkus.log.category."org.acme.LoginResource".level=INFO
quarkus.log.category."org.acme.SecuredResource".level=INFO
```

Modification de LoginResource.java pour logger les connexions
```java
package org.acme;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Path("/api/login")
public class LoginResource {
    private static final Logger LOG = Logger.getLogger(LoginResource.class);

    @POST
    public Response login(LoginRequest request) {
        LOG.infov("Tentative de connexion pour l'utilisateur : {0}", request.username);

        if (!isValidUser(request.username, request.password)) {
            LOG.warnv("Échec de connexion pour l'utilisateur : {0}", request.username);
            return Response.status(401).entity("Identifiants invalides").build();
        }

        String token = Jwt.issuer("quarkus")
                .upn(request.username)
                .groups(getRoles(request.username))
                .expiresIn(Duration.ofHours(24))
                .sign();

        LOG.infov("Connexion réussie pour l'utilisateur : {0}", request.username);
        return Response.ok(new LoginResponse(token)).build();
    }

    // ... (méthodes isValidUser et getRoles inchangées)
}
```

Modification de SecuredResource.java pour logger les accès
```java
package org.acme;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.jboss.logging.Logger;

@Path("/api/protected")
public class SecuredResource {
    private static final Logger LOG = Logger.getLogger(SecuredResource.class);

    @GET
    @RolesAllowed({"admin", "user"})
    public String protectedEndpoint() {
        LOG.info("Accès à l'endpoint protégé");
        return "Accès autorisé !";
    }

    @GET
    @Path("/admin")
    @RolesAllowed({"admin"})
    public String adminEndpoint() {
        LOG.infov("Accès à l'endpoint admin par un utilisateur avec le rôle admin");
        return "Accès admin autorisé !";
    }
}
```

Exemple de sortie de log
Les logs seront affichés dans la console Quarkus au format JSON :
```json
{
  "timestamp": "2026-03-04T12:00:00.000Z",
  "sequence": 1,
  "loggerClassName": "org.acme.LoginResource",
  "loggerName": "org.acme.LoginResource",
  "level": "INFO",
  "message": "Connexion réussie pour l'utilisateur : admin",
  "threadName": "vert.x-worker-thread-0",
  "hostName": "vps-blondy29",
  "processName": "quarkus-app",
  "processId": 12345
}
```

Redirection des logs vers un fichier
Pour sauvegarder les logs dans un fichier, ajoutez dans application.properties :
```properties
quarkus.log.file.enable=true
quarkus.log.file.path=logs/connexions.log
quarkus.log.file.level=INFO
```

## 12. Résumé des fonctionnalités implémentées
| Fonctionnalité | Technologie | Fichiers concernés |
|----------------|-------------|--------------------|
|      Dashboard conditionnel |       NextJS + Tailwind |       pages/dashboard.js, pages/admin.js|
|      Génération de JWT |       Node.js |      generate-jwt.js |
|      Logs des connexions |      Quarkus + JSON |      LoginResource.java, SecuredResource.java |

## 13. Prochaines étapes suggérées

1. Tester le dashboard en vous connectant avec différents rôles (admin/user).
2. Générer des JWT pour vos tests avec le script Node.js.
3. Vérifier les logs dans la console Quarkus ou dans logs/connexions.log.
