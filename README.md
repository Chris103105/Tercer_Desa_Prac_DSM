# Mentora — Lista de Recursos de Aprendizaje

## Integrantes

| Nombre | Carnet |
|---|---|
| Christopher Steven Jovel Beltrán | JB251834 |
| Elisabet Beatriz Marroquín González | MG251360 |

## Funcionalidades

1. Visualización de todos los recursos.
2. Búsqueda de un recurso por su ID.
3. Agregar un recurso nuevo.
4. Modificar un recurso existente.
5. Eliminar un recurso, con confirmación.
6. Abrir el enlace del recurso en el navegador o en YouTube.
7. Notificaciones Toast en cada acción y en los errores de conexión.

## Paleta de colores

Paleta **Blue Grey + Orange** 

| Uso | Color |
|---|---|
| Primary dark | `#455A64` |
| Primary | `#607D8B` |
| Primary light | `#CFD8DC` |
| Accent | `#FF9800` |
| Primary text | `#212121` |
| Secondary text | `#757575` |
| Text / Icons | `#FFFFFF` |
| Divider | `#BDBDBD` |

## API

URL base: `https://6aec01edee39618d7bf8c58b.mockapi.io/`

| Método | Endpoint | Acción |
|---|---|---|
| GET | `/recursos` | Listar todos los recursos |
| GET | `/recursos/{id}` | Buscar un recurso por ID |
| POST | `/recursos` | Agregar un recurso |
| PUT | `/recursos/{id}` | Modificar un recurso |
| DELETE | `/recursos/{id}` | Eliminar un recurso |

Campos de cada recurso:

```json
{
  "id": "1",
  "titulo": "Pro Git (2.ª edición)",
  "descripcion": "Libro oficial de Git en español.",
  "tipo": "Libro",
  "enlace": "https://git-scm.com/book/es/v2",
  "imagen": "https://git-scm.com/images/progit2.png",
  "autor": "Scott Chacon y Ben Straub",
  "nivel": "Básico"
}
```

Además de los campos mínimos se agregaron `autor` y `nivel` (Básico, Intermedio o Avanzado).

## Estructura del proyecto

```
app/src/main/
├── AndroidManifest.xml
├── java/edu/udb/recursosaprendizaje/
│   ├── model/Recurso.java
│   ├── network/RetrofitClient.java
│   ├── network/ResourceService.java
│   ├── ui/ResourceActivity.java
│   ├── ui/ResourceFormActivity.java
│   ├── ui/adapter/ResourceAdapter.java
│   └── util/Constants.java
└── res/
    ├── drawable/
    ├── layout/
    ├── mipmap-anydpi-v26/
    └── values/
```

## Librerías utilizadas

- Retrofit 2 y Gson para consumir la API REST.
- Glide para cargar las imágenes desde URL.
- Material Components para tarjetas, botones, formularios y diálogos.

## Cómo ejecutar

1. Clonar el repositorio.
2. Abrir la carpeta en Android Studio.
3. Esperar a que termine la sincronización de Gradle.
4. Ejecutar en un emulador o celular con Android 8.0 o superior.

## Video de demostración

[Ver video de la aplicación](https://drive.google.com/file/d/18DzKrqKpUbl1pD2vY6XXStpHdk389P8Y/view?usp=sharing)



