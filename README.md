# Gestión de Dispositivos de Escaneo en el Ciclo de Vida de Actividades Android

## Descripción

Este es un proyecto de ejemplo que implementa:

1. Un administrador de dispositivos de lectura/escritura que se sincroniza con el ciclo de vida de las actividades de
   Android
2. Un sistema de resolución de códigos que busca en diferentes fuentes (locales y remotas)

## Estructura del Proyecto

```
LifecycleDevicesManager/
├── deviceLifecycle/       # Ciclo de vida de lectura/escritura
│   └── ScannerManager.kt  # Gestiona el ciclo de vida de los escáneres
├── devices/               # Implementaciones de dispositivos específicos
│   ├── floatingCamera/    # Ventana flotante para cámara/scanner
│   ├── honeywell/         # Soporte para handhelds Honeywell
│   ├── zebra/             # Soporte para handhelds Zebra
│   ├── vh75/              # Soporte para dispositivo VH75 (RFID)
│   ├── nfc/               # Soporte para NFC
│   └── rfid/              # Soporte para RFID
└── resolver/              # Sistema de resolución de códigos
    └── CodeResolver.kt    # Resuelve códigos escaneados
```

## Funcionalidades Principales

### Gestión de Dispositivos

El `ScannerManager` se encarga de:

- Conectar y desconectar dispositivos durante el ciclo de vida de las actividades
- Soportar diferentes tipos de dispositivos:
    - NFC
    - RFID (VH75 por Bluetooth)
    - Escáneres Honeywell y Zebra
    - Ventana flotante de cámara

### Resolución de Códigos

El `CodeResolver`:

- Busca códigos escaneados en diferentes fuentes
- Usa un sistema de detección de prefijos para identificar tipos de códigos
- Permite buscar en listas locales, bases de datos y APIs remotas
- Proporciona callbacks para manejar resultados

## Uso Básico

### En la clase de la aplicación

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ScannerManager.register(this)
    }
}
```

### En la actividad

```kotlin
class MainActivity : AppCompatActivity(), ScannerListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Configurar escáner
    }

    override fun onCodeScanned(code: String) {
        CodeResolver.Builder()
            .withCode(code)
            .onFinish { result ->
                // Procesar resultado
            }
            .build()
    }
}
```

## Cómo Funciona

### Ciclo de Vida de Dispositivos

- En `onCreate()`: Se inicializan los dispositivos
- En `onResume()`: Se activan los dispositivos
- En `onPause()`: Se desactivan los dispositivos
- En `onDestroy()`: Se liberan los recursos

### Resolución de Códigos

1. El sistema detecta el prefijo del código
2. Ejecuta los handlers registrados por prioridad
3. Busca primero en listas locales si está configurado
4. Devuelve el resultado a través del callback

## Notas Adicionales

- El proyecto soporta dispositivos de diferentes fabricantes (Honeywell, Zebra, etc.)
- Incluye sonidos de notificación para escaneos exitosos/fallidos
- Utiliza Coroutines para la resolución de códigos
- Tiene manejo de permisos para dispositivos Bluetooth

## Licencia

Este proyecto es un ejemplo de código sin licencia específica. Puede ser utilizado como referencia para implementar
sistemas similares.