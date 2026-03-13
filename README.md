# Voluntariado IIAP - Aplicación Android

![Versión Beta](https://img.shields.io/badge/Versi%C3%B3n-Beta-orange)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue)
![Compose](https://img.shields.io/badge/Jetpack-Compose-green)

Esta es la aplicación oficial de **Voluntariado del IIAP** (Instituto de Investigaciones de la Amazonía Peruana). Diseñada para conectar a voluntarios comprometidos con la preservación de la biodiversidad amazónica y la investigación científica.

## 🚀 Características Principales

### 👤 Perfil del Voluntario
- **Gestión Completa**: Edición de información personal, contacto y ubicación.
- **Categorización**: Selección dinámica de categoría (Junior, Senior, Especialista, Investigador).
- **Foto de Perfil**: Subida y recorte de imagen (Crop) integrado. La imagen se sincroniza en toda la interfaz (AppBars).
- **Currículum Integrado**: Gestión (Agregar/Eliminar) de Formación Académica y Experiencia Laboral.

### 🏠 Inicio (Home)
- **Iniciativa 2026**: Sección Hero destacada para promover la conservación del "Pulmón del Mundo".
- **Carrusel de Videos**: Visualización moderna de videos institucionales de YouTube.
- **Áreas Destacadas**: Listado vertical de unidades de investigación con filtros rápidos y etiquetas de estado (Activo/Urgente).

### 🔍 Exploración y Postulación
- **Áreas de Investigación**: Buscador avanzado de áreas operativas del IIAP.
- **Convocatorias**: Sistema de postulación virtual para proyectos específicos.
- **Información Adicional**: Guía detallada sobre cómo postular de forma presencial o virtual.

### 🛡️ Seguridad y Ajustes
- **Autenticación**: Registro e inicio de sesión seguro mediante Firebase Auth.
- **Biometría**: Acceso rápido mediante huella dactilar.
- **Modo Oscuro**: Interfaz adaptativa para ahorro de batería y comodidad visual.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/compose) con **Material Design 3**.
- **Arquitectura**: MVVM (Model-View-ViewModel).
- **Backend**: [Firebase](https://firebase.google.com/) (Authentication, Firestore, Storage).
- **Redes**: [Retrofit](https://square.github.io/retrofit/) & OkHttp para consumo de APIs REST.
- **Imágenes**: [Coil](https://coil-kt.github.io/coil/) para carga asíncrona y [Android Image Cropper](https://github.com/CanHub/Android-Image-Cropper) para edición.
- **Navegación**: Navigation Compose con soporte para Overlays y Sheets.

## 📂 Estructura del Proyecto

```text
com.gdcj.voluntariadoiiap
├── data
│   ├── local      # Gestión de sesiones y preferencias
│   ├── model      # Clases de datos (User, Study, Experience, etc.)
│   └── remote     # Servicios API e interceptores
├── navigation     # NavHost y definiciones de rutas
├── ui
│   ├── components # Componentes reutilizables (Avatar, Headers, Dialogs)
│   ├── screens    # Pantallas principales (Home, Profile, Login, etc.)
│   ├── theme      # Configuración de colores, tipografía y temas
│   └── viewmodel  # Lógica de negocio y gestión de estado
└── MainActivity   # Punto de entrada de la aplicación
```

## ⚙️ Instalación

1. Clonar el repositorio.
2. Abrir el proyecto en **Android Studio (Ladybug o superior)**.
3. Configurar el archivo `google-services.json` en la carpeta `/app`.
4. Sincronizar con Gradle.
5. Ejecutar en un emulador o dispositivo físico con Android 8.0+.

---
*Desarrollado para el Instituto de Investigaciones de la Amazonía Peruana.*
