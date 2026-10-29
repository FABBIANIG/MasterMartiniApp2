package cl.duoc.dsy1105.mastermartini.model

/**
 * Categorías gastronómicas oficiales del caso Master Martini Chile SPA (Punto 2.1).
 */
enum class CategoriaGastronomica(val nombreVisible: String) {
    TODAS("Todas"),
    CHOCOLATERIA("Chocolatería"),
    PASTELERIA_VITRINA("Pastelería de Vitrina"),
    HELADERIA("Heladería"),
    BOLLERIA_HOJA("Bollería y Masas de Hoja"),
    HORNEO_EMPANADAS("Galletas y Empanadas")
}

/**
 * Modelo para representar el Perfil del Cliente / Prospecto (Punto 3.2 del caso).
 * Nota académica: Se utilizan datos ficticios/sintéticos según restricción 5.1.
 */
data class ClienteProspecto(
    val id: Int = 0,
    val nombreCompleto: String = "",
    val rutEmpresa: String = "",        // Ej: 76.123.456-K (Ficticio)
    val correo: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val tipoNegocio: String = "Panadería / Pastelería",
    val tamanoNegocio: String = "Pequeño (1 a 5 personas)",
    val categoriasInteres: List<String> = emptyList(),
    val fotoLocalUri: String? = null    // URI de foto tomada con Cámara nativa (IE 2.4.1)
)

/**
 * Modelo para las Recetas, Videos Técnicos y Cursos de Especialidad (Punto 2.1 y 6).
 */
data class ContenidoTecnico(
    val id: Int,
    val titulo: String,
    val categoria: CategoriaGastronomica,
    val tipoContenido: String,          // "Receta Simple", "Video Técnico" o "Curso Especialidad"
    val duracionMinutos: Int,
    val nivelDificultad: String,        // "Básico", "Intermedio", "Avanzado"
    val insumosMasterMartini: List<String>, // Productos de la marca utilizados
    val pasosPreparacion: List<String>,
    val chefAutor: String,
    val telefonoSoporteChef: String = "+56987654321" // Ficticio para Intent de Llamada Nativa
)

/**
 * Estado del Formulario y Validaciones Desacopladas (IE 2.1.2 e IE 2.2.1).
 * Separa los valores y los mensajes de error de la interfaz visual.
 */
data class FormularioRegistroState(
    val nombreCompleto: String = "",
    val errorNombre: String? = null,

    val rutEmpresa: String = "",
    val errorRut: String? = null,

    val correo: String = "",
    val errorCorreo: String? = null,

    val telefono: String = "",
    val errorTelefono: String? = null,

    val direccion: String = "",
    val errorDireccion: String? = null,

    val tipoNegocio: String = "Panadería / Pastelería",
    val tamanoNegocio: String = "Pequeño (1 a 5 personas)",
    val categoriasSeleccionadas: Set<String> = setOf("Chocolatería"),
    val errorCategorias: String? = null,

    val fotoCapturadaUri: String? = null,
    val guardadoExitoso: Boolean = false
)