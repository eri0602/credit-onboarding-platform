# Build stage
FROM node:20-slim AS build
WORKDIR /app
# Copy package files first for better layer caching
COPY package*.json ./
# Install dependencies
RUN npm ci
# Copy source files
COPY . .
# Build the Astro site
RUN npm run build

# Runtime stage
FROM nginx:alpine
# Copy the built static files to nginx
COPY --from=build /app/dist /usr/share/nginx/html
# Custom nginx config to handle SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80