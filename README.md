# 💳 Plataforma Web de Onboarding de Créditos para PYMES

Este repositorio contiene el desarrollo de una **Plataforma Web de Onboarding de Créditos para PYMES**, creada en el marco de la **simulación laboral de NoCountry**.  
El objetivo es ofrecer a pequeñas y medianas empresas un proceso ágil, seguro y digital para la **solicitud, evaluación y aprobación de créditos**.

---
## 🛠️ Tecnologías a utilizar
- **Frontend:** HTML, CSS, JavaScript, Astro  
- **Backend:** Java, Spring Boot, JWT  
- **Control de versiones:** GitHub  
- **Metodología:** Ágil (Scrum adaptado a la simulación)  

---
---

## 📂 Estructura general del proyecto
La estructura principal del repositorio actualmente es la siguiente (resumen):

- `backend/` — API y servicios en Java con Spring Boot
	- `pom.xml` (gestión de dependencias y plugins)
	- `src/main/java/com/NoCountry/credit_onboarding_platform/` (controladores, servicios, modelos, repositorios, config)
	- `src/main/resources/application.properties`
	- `Dockerfile`, `wait-for.sh` (para producción/contenedores)

- `frontend/` — Sitio estático construido con Astro
	- `package.json`, `tsconfig.json`, `astro.config.mjs`
	- `src/` (componentes, layouts, páginas, estilos)
	- `public/` (activos estáticos)
	- `Dockerfile`, `nginx.conf` (para construir y servir con Nginx)

- `docker-compose.yml` — Orquestación local de frontend, backend y DB
- `README.md` — Documentación (este archivo)

> Nota: Esta estructura está sincronizada con la codebase actual en la rama `main`.


## 📊 Métricas y Valor
El sistema de métricas de NoCountry será clave para:  
- Medir desempeño del equipo.  
- Comunicar claramente el valor que entregamos.  
- Mantener un reparto equitativo de tareas y entregables.  

---

## 📎 Recursos y enlaces
- [Diseño en Figma](https://www.figma.com/design/VAEthqQolIekuPQCAQNPsm/NoCountry-WebApp?node-id=0-1&t=hnCu3Fpu1PKFsU7i-1)  

---

## ▶️ Tutorial para correr el proyecto (Docker) — PowerShell (Windows)
Sigue estos pasos para levantar el sistema en tu máquina usando Docker Desktop / Docker Compose.

1) Requisitos
- Docker Desktop instalado y corriendo (https://www.docker.com/get-started)
- Docker Compose (incluido en Docker Desktop moderno)

2) Abrir PowerShell y moverse al proyecto

```powershell
cd "C:\Users\USER\Desktop\Proyecto Prestamos\credit-onboarding-platform"
```

3) Levantar los servicios con Docker Compose (modo producción)

```powershell
docker compose up -d --build
```

Esto hará un build multi-stage del `frontend` (Node/Astro → static) y del `backend` (Maven → JAR) y luego ejecutará los contenedores.

4) Comprobaciones básicas
- Ver estado de los servicios:

```powershell
docker compose ps
```

- Ver logs en vivo (por ejemplo backend):

```powershell
docker compose logs -f cop_backend
```

5) Acceder a la aplicación
- Frontend (Nginx que sirve los archivos estáticos): http://localhost
- Backend (Spring Boot): http://localhost:8080  (si expones 8080 en `docker-compose.yml`)

6) Opcional: construir solo un servicio

```powershell
docker compose build backend
docker compose up -d backend
```

7) Alternativa: construir localmente sin Docker

- Backend (si tienes Maven localmente):

```powershell
cd backend
# Si existe mvnw en el repo (Windows):
.\mvnw.cmd -DskipTests package
# O si tienes mvn en PATH:
mvn -DskipTests package
```

- Frontend (local, para desarrollo):

```powershell
cd frontend
npm ci
npm run dev    # para desarrollo
# o para build estático
npm run build
```

8) Detener y limpiar contenedores

```powershell
docker compose down --volumes --remove-orphans
```

9) Solución de problemas comunes
- Si `mvn` no está disponible en tu PATH, usa la opción Docker para construir el backend (comando `docker compose up --build`).
- Si un puerto está en uso, verifica con `netstat -ano` o cierra la aplicación que lo esté usando.
- Logs con `docker compose logs -f <service>`.

---

## 👥 Créditos
Este proyecto es desarrollado por el equipo de la simulación laboral de **NoCountry**.  
La experiencia busca fomentar la **autogestión, colaboración y aprendizaje práctico** en un entorno de desarrollo realista.

Front-End
- Juan Manuel Quevedo Gonzalez

Back-End
- Tomas Agustin Colazo
- Kevin Ramos

---
