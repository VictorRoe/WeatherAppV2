# WeatherApp V2
## Arquitectura
<img width="1437" height="808" alt="image" src="https://github.com/user-attachments/assets/dc49590c-ce20-4a0d-8eea-6c6a87cf69e2" />


# 📄 Documentación del Backend - WeatherAppV2

## 📌 Descripción General

El backend de **WeatherAppV2** es una aplicación Spring Boot que proporciona información meteorológica en tiempo real a través de una API REST. Utiliza una **arquitectura hexagonal (ports and adapters)** para mantener un código limpio, mantenible y fácil de probar.

---

## 🚀 Tecnologías Principales

- **Java 17** – Lenguaje de programación principal  
- **Spring Boot 3.5.3** – Framework principal  
- **Spring WebFlux** – Para programación reactiva  
- **Lettuce** – Cliente Redis reactivo  
- **Redis** – Sistema de caché en memoria   
- **Log4j2** – Para registro de logs  
- **Gradle** – Gestión de dependencias  

---

## 🏗️ Estructura del Proyecto

```
src/main/java/dev/weather/app/
├── AppApplication.java          # Punto de entrada de la aplicación
├── application/                # Casos de uso y lógica de negocio
│   ├── services/
│   └── usecases/
├── domain/                     # Modelos y puertos (interfaces)
│   ├── models/
│   └── ports/
│       ├── in/                 # Puertos de entrada (casos de uso)
│       └── out/                # Puertos de salida (repositorios, servicios externos)
└── infraestructure/            # Adaptadores e implementaciones
    ├── adapters/              # Implementaciones de puertos
    ├── config/                # Configuraciones de Spring
    └── controllers/           # Controladores REST
```

---

## 🔄 Flujo de la Aplicación

1. **Petición HTTP**: El cliente realiza una petición `GET` a `/weather` con parámetros `lat` y `lon`.
2. **Controlador**: [`CurrentWeatherController`](src/main/java/dev/weather/app/infraestructure/controllers/CurrentWeatherController.java) recibe la petición.
3. **Capa de Aplicación**: Se delega a `WeatherService` (caso de uso).
4. **Capa de Dominio**:  
   - Verifica primero en caché (Redis).  
   - Si no está en caché, llama al servicio externo de clima.  
   - Almacena la respuesta en caché por **15 minutos**.
5. **Respuesta**: Devuelve los datos meteorológicos formateados.

---

## ⚙️ Configuración Requerida

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```bash
# Redis
HOSTNAME_REDIS=tu_hostname
PASSWORD_REDIS=tu_contraseña_redis
PORT_REDIS=puerto

# Weather API
WEATHER_API_KEY=tu_api_key
WEATHER_API_URL=https://api.openweathermap.org
```

---

## 🚀 Cómo Ejecutar

### Requisitos Previos

- Java 17 o superior  
- Gradle 7.6+  
- Redis Server (o Redis Cloud)  
- Cuenta en OpenWeatherMap (para la API key)  

### Pasos para Iniciar

1. Clonar el repositorio  
2. Configurar las variables de entorno  
3. Ejecutar la aplicación:

```bash
./gradlew bootRun
```

4. La aplicación estará disponible en [http://localhost:8081](http://localhost:8081)

---

## 📚 Endpoints

### Obtener Clima Actual

```
GET /weather?lat={latitud}&lon={longitud}
```

#### Parámetros:

- `lat`: Latitud (**requerido**)  
- `lon`: Longitud (**requerido**)  

#### Ejemplo de respuesta exitosa:

```json
{
  "coord": {
    "lon": -75.4275,
    "lat": 6.0108
  },
  "weather": [
    {
      "id": 801,
      "main": "Clouds",
      "description": "few clouds",
      "icon": "02d"
    }
  ],
  "main": {
    "temp": 22.14,
    "feels_like": 21.66,
    "temp_min": 21.45,
    "temp_max": 22.66,
    "pressure": 1013,
    "humidity": 48,
    "sea_level": 1013,
    "grnd_level": 780
  },
  "wind": {
    "speed": 6.17,
    "deg": 170,
    "gust": 11.32
  },
  "sys": {
    "type": 2,
    "id": 2098421,
    "country": "CO",
    "sunrise": 1753354550,
    "sunset": 1753399218
  },
  "name": "La Ceja",
  "cod": 200
}
```

---

## 🔍 Monitoreo y Depuración

- Los logs están configurados con **Log4j2**
- Se puede habilitar el modo `DEBUG` en el archivo [`application.properties`](src/main/resources/application.properties)
- Se recomienda usar **Redis Commander** para inspeccionar la caché

---


## 📝 Notas Adicionales

- La caché tiene un TTL de **15 minutos**
- Se recomienda usar **HTTPS en producción**
- Las **credenciales nunca deben subirse** al control de versiones
