# 📱 EventCenter - Gestión de Eventos Comunitarios

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
</div>

## 📋 Descripción

**EventCenter** es una aplicación Android moderna diseñada para la gestión de eventos comunitarios, perfecta para sociedades de vecinos y comunidades organizadas. Permite crear, administrar y participar en eventos de manera sencilla e intuitiva.

### ✨ Características Principales

- 🎯 **Gestión de Eventos**: Crea y administra eventos con información detallada
- 👥 **Sistema de Asistencia**: Confirma tu participación en eventos
- ⭐ **Calificaciones y Comentarios**: Evalúa eventos y comparte tu experiencia
- 📊 **Panel de Organizador**: Estadísticas y gestión de asistentes
- 🔐 **Autenticación Segura**: Login con email/password y Google Sign-In
- 📅 **Filtros por Categoría**: Encuentra eventos por tipo (Deportes, Música, Arte, etc.)
- 🎨 **Diseño Moderno**: Interfaz intuitiva con Material Design 3
- 📱 **Licencias Creative Commons**: Protección de contenido con diferentes tipos de licencias
- 🔔 **Notificaciones**: Recordatorios de eventos próximos
- 📤 **Compartir Eventos**: Comparte en WhatsApp, Facebook, Twitter y más

---

## 🎨 Diseño

### Figma
Diseño de la aplicación: [Ver en Figma](#)

### Capturas de Pantalla

*Próximamente...*

---

## 🛠️ Tecnologías Utilizadas

### **Lenguaje y Framework**
- **Kotlin** - Lenguaje de programación principal
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño

### **Arquitectura**
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Clean Architecture**

### **Base de Datos y Backend**
- **Firebase Authentication** - Autenticación de usuarios
- **Firebase Firestore** - Base de datos en tiempo real
- **Firebase Storage** - Almacenamiento de imágenes
- **Room Database** - Almacenamiento local

### **Bibliotecas Principales**
```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Firebase
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Room Database
implementation("androidx.room:room-runtime")
implementation("androidx.room:room-ktx")

// Navigation
implementation("androidx.navigation:navigation-compose")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth")

// Coil (carga de imágenes)
implementation("io.coil-kt:coil-compose")
```

---

## 📂 Estructura del Proyecto

```
com.ch220048.eventcenter/
├── data/
│   ├── local/              # Room Database
│   │   ├── AppDatabase.kt
│   │   ├── EventDao.kt
│   │   ├── UserDao.kt
│   │   └── CommentDao.kt
│   ├── model/              # Modelos de datos
│   │   ├── Event.kt
│   │   ├── User.kt
│   │   ├── Comment.kt
│   │   └── CCLicense.kt
│   └── repository/         # Repositorios
│       ├── AuthRepository.kt
│       └── EventRepository.kt
├── ui/
│   ├── auth/              # Autenticación
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   └── AuthViewModel.kt
│   ├── events/            # Gestión de eventos
│   │   ├── HomeScreen.kt
│   │   ├── EventDetailScreen.kt
│   │   ├── CreateEventScreen.kt
│   │   ├── EditEventScreen.kt
│   │   ├── MyEventsScreen.kt
│   │   ├── HistoryScreen.kt
│   │   ├── OrganizerDashboardScreen.kt
│   │   └── EventViewModel.kt
│   ├── profile/           # Perfil de usuario
│   │   ├── ProfileScreen.kt
│   │   └── AboutScreen.kt
│   ├── components/        # Componentes reutilizables
│   │   ├── EventCard.kt
│   │   ├── CustomTextField.kt
│   │   └── LoadingDialog.kt
│   └── theme/             # Temas y estilos
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── navigation/            # Navegación
│   ├── NavGraph.kt
│   └── Screen.kt
├── utils/                 # Utilidades
│   ├── GoogleSignInHelper.kt
│   ├── NotificationHelper.kt
│   └── ShareHelper.kt
└── MainActivity.kt
```

---

## 🚀 Instalación y Configuración

### **Prerequisitos**
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- Cuenta de Firebase
- Dispositivo Android o Emulador (API 24+)

### **Pasos de Instalación**

1. **Clonar el repositorio**
```bash
git clone https://github.com/David-301/ControlEventos.git
cd ControlEventos
```

2. **Configurar Firebase**
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
   - Descarga el archivo `google-services.json`
   - Colócalo en `app/google-services.json`
   - Habilita Authentication (Email/Password y Google)
   - Crea una base de datos Firestore
   - Configura Storage

3. **Configurar Google Sign-In**
   - Obtén tu `default_web_client_id` de Firebase
   - Agrégalo en `strings.xml`:
   ```xml
   <string name="default_web_client_id">TU_CLIENT_ID_AQUI</string>
   ```

4. **Sincronizar y Ejecutar**
   - Abre el proyecto en Android Studio
   - Sincroniza Gradle (`Sync Now`)
   - Ejecuta en dispositivo o emulador

---

## 📱 Funcionalidades Detalladas

### **1. Sistema de Autenticación**
- Registro con email y contraseña
- Inicio de sesión con Google
- Recuperación de contraseña
- Gestión de sesión persistente

### **2. Gestión de Eventos**
- Crear eventos con:
  - Título, descripción y ubicación
  - Fecha y hora
  - Categoría
  - Capacidad máxima
  - Imagen (opcional)
  - Licencia Creative Commons
- Editar eventos propios
- Eliminar eventos
- Ver eventos próximos y pasados

### **3. Participación en Eventos**
- Confirmar asistencia
- Cancelar asistencia
- Ver lista de asistentes (organizadores)
- Sistema de comentarios y calificaciones

### **4. Categorías de Eventos**
- 🏃 Deportes
- 🎵 Música
- 💻 Tecnología
- 🎨 Arte
- 📚 Educación
- 💼 Negocios
- 👥 Social
- ⚪ General

### **5. Licencias Creative Commons**
- CC BY (Atribución)
- CC BY-SA (Atribución-CompartirIgual)
- CC BY-ND (Atribución-SinDerivadas)
- CC BY-NC (Atribución-NoComercial)
- CC BY-NC-SA (Atribución-NoComercial-CompartirIgual)
- CC BY-NC-ND (Atribución-NoComercial-SinDerivadas)

### **6. Panel de Organizador**
- Estadísticas del evento
- Lista de asistentes confirmados
- Gráficos de asistencia
- Gestión de comentarios

### **7. Compartir Eventos**
- WhatsApp
- Facebook
- Twitter (X)
- Email
- Copiar al portapapeles
- Más opciones del sistema

---

## 🎯 Casos de Uso

### **Para Sociedades de Vecinos**
- Reuniones de junta directiva
- Eventos sociales comunitarios
- Actividades deportivas
- Talleres educativos

### **Para Comunidades**
- Clubes deportivos
- Grupos culturales
- Organizaciones estudiantiles
- Asociaciones de vecinos

---

## 👨‍💻 Autor

**David Campos**  
Carné: CH220048  
Universidad Francisco Gavidia

---

## 📄 Licencia

Este proyecto fue desarrollado como parte del curso de Programación de Dispositivos Móviles.

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Para cambios importantes:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add: nueva característica'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📞 Contacto

- **Email**: [tu-email@ejemplo.com]
- **GitHub**: [@David-301](https://github.com/David-301)

---

## 🙏 Agradecimientos

- Universidad Francisco Gavidia
- Firebase y Google Cloud
- Comunidad de Android Developers
- Jetpack Compose Team

---

<div align="center">
  
### ⭐ Si te gustó este proyecto, dale una estrella!

**Hecho con ❤️ para la gestión comunitaria**

</div>
