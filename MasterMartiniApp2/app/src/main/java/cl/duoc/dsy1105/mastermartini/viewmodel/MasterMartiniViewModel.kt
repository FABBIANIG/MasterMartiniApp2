package cl.duoc.dsy1105.mastermartini.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import cl.duoc.dsy1105.mastermartini.data.local.CatalogoRepository
import cl.duoc.dsy1105.mastermartini.data.local.MasterMartiniDatabaseHelper
import cl.duoc.dsy1105.mastermartini.model.CategoriaGastronomica
import cl.duoc.dsy1105.mastermartini.model.ClienteProspecto
import cl.duoc.dsy1105.mastermartini.model.ContenidoTecnico
import cl.duoc.dsy1105.mastermartini.model.FormularioRegistroState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel principal que centraliza la gestión de estado y las reglas de validación
 * desacopladas de la interfaz visual (Cumple IE 2.2.1 e IE 2.3.1).
 */
class MasterMartiniViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia de la base de datos local SQLite (IE 2.3.1)
    private val dbHelper = MasterMartiniDatabaseHelper(application.applicationContext)

    // 1. ESTADO DE ACCESIBILIDAD: Modo Texto Grande para mayores de 50 años (Punto 2.3 y 3.3 del caso)
    private val _modoTextoGrande = MutableStateFlow(false)
    val modoTextoGrande: StateFlow<Boolean> = _modoTextoGrande.asStateFlow()

    // 2. ESTADO DEL CATÁLOGO Y FILTROS
    private val _contenidosCompletos = CatalogoRepository.obtenerContenidos()
    private val _categoriaSeleccionada = MutableStateFlow(CategoriaGastronomica.TODAS)
    val categoriaSeleccionada: StateFlow<CategoriaGastronomica> = _categoriaSeleccionada.asStateFlow()

    private val _contenidosFiltrados = MutableStateFlow(_contenidosCompletos)
    val contenidosFiltrados: StateFlow<List<ContenidoTecnico>> = _contenidosFiltrados.asStateFlow()

    // 3. ESTADO DE FAVORITOS GUARDADOS EN SQLITE
    private val _favoritosIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoritosIds: StateFlow<Set<Int>> = _favoritosIds.asStateFlow()

    // 4. ESTADO DEL FORMULARIO Y VALIDACIONES DESACOPLADAS (IE 2.1.2 e IE 2.2.1)
    private val _formularioState = MutableStateFlow(FormularioRegistroState())
    val formularioState: StateFlow<FormularioRegistroState> = _formularioState.asStateFlow()

    // 5. ESTADO DE CLIENTES GUARDADOS EN BASE DE DATOS LOCAL (IE 2.3.1)
    private val _clientesRegistrados = MutableStateFlow<List<ClienteProspecto>>(emptyList())
    val clientesRegistrados: StateFlow<List<ClienteProspecto>> = _clientesRegistrados.asStateFlow()

    init {
        // Al iniciar el ViewModel, cargamos los datos persistidos en SQLite
        cargarDatosLocales()
    }

    private fun cargarDatosLocales() {
        _favoritosIds.value = dbHelper.obtenerFavoritosIds()
        _clientesRegistrados.value = dbHelper.obtenerClientesRegistrados()
    }

    /**
     * Alterna el tamaño de fuente accesible para usuarios con dificultades visuales.
     */
    fun alternarModoTextoGrande() {
        _modoTextoGrande.update { !it }
    }

    /**
     * Filtra las recetas, videos técnicos y cursos según la categoría gastronómica.
     */
    fun seleccionarCategoria(categoria: CategoriaGastronomica) {
        _categoriaSeleccionada.value = categoria
        _contenidosFiltrados.value = if (categoria == CategoriaGastronomica.TODAS) {
            _contenidosCompletos
        } else {
            _contenidosCompletos.filter { it.categoria == categoria }
        }
    }

    /**
     * Busca un contenido técnico específico por su ID para la vista de detalle.
     */
    fun obtenerContenidoPorId(id: Int): ContenidoTecnico? {
        return _contenidosCompletos.find { it.id == id }
    }

    /**
     * Marca o desmarca una receta como favorita en SQLite local y actualiza la UI.
     */
    fun alternarFavorito(recetaId: Int) {
        dbHelper.alternarFavorito(recetaId)
        _favoritosIds.value = dbHelper.obtenerFavoritosIds()
    }

    // =========================================================================
    // LÓGICA DE VALIDACIÓN EN TIEMPO REAL DESACOPLADA DE LA UI (IE 2.1.2 / 2.2.1)
    // =========================================================================

    fun onNombreChange(nuevoNombre: String) {
        val error = when {
            nuevoNombre.isBlank() -> "El nombre completo o razón social es obligatorio"
            nuevoNombre.trim().length < 3 -> "Debe contener al menos 3 caracteres"
            else -> null
        }
        _formularioState.update {
            it.copy(nombreCompleto = nuevoNombre, errorNombre = error, guardadoExitoso = false)
        }
    }

    fun onRutChange(nuevoRut: String) {
        // Expresión regular para RUT chileno (ficticio/sintético, ej: 76.123.456-K o 12345678-9)
        val rutRegex = Regex("^[0-9]{1,2}\\.?[0-9]{3}\\.?[0-9]{3}-[0-9kK]$")
        val error = when {
            nuevoRut.isBlank() -> "El RUT de empresa/emprendedor es obligatorio"
            !rutRegex.matches(nuevoRut.trim()) -> "Formato inválido. Ejemplo: 76.123.456-K"
            else -> null
        }
        _formularioState.update {
            it.copy(rutEmpresa = nuevoRut, errorRut = error, guardadoExitoso = false)
        }
    }

    fun onCorreoChange(nuevoCorreo: String) {
        val error = when {
            nuevoCorreo.isBlank() -> "El correo electrónico es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo.trim()).matches() ->
                "Ingrese un correo válido (ej: contacto@pasteleria.cl)"
            else -> null
        }
        _formularioState.update {
            it.copy(correo = nuevoCorreo, errorCorreo = error, guardadoExitoso = false)
        }
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        val soloDigitos = nuevoTelefono.filter { it.isDigit() }
        val error = when {
            nuevoTelefono.isBlank() -> "El teléfono de contacto es obligatorio"
            soloDigitos.length < 8 -> "Ingrese al menos 8 dígitos numéricos"
            else -> null
        }
        _formularioState.update {
            it.copy(telefono = nuevoTelefono, errorTelefono = error, guardadoExitoso = false)
        }
    }

    fun onDireccionChange(nuevaDireccion: String) {
        val error = when {
            nuevaDireccion.isBlank() -> "La dirección o comuna del local es obligatoria"
            nuevaDireccion.trim().length < 5 -> "Especifique calle y número o comuna"
            else -> null
        }
        _formularioState.update {
            it.copy(direccion = nuevaDireccion, errorDireccion = error, guardadoExitoso = false)
        }
    }

    fun onTipoNegocioChange(nuevoTipo: String) {
        _formularioState.update { it.copy(tipoNegocio = nuevoTipo, guardadoExitoso = false) }
    }

    fun onTamanoNegocioChange(nuevoTamano: String) {
        _formularioState.update { it.copy(tamanoNegocio = nuevoTamano, guardadoExitoso = false) }
    }

    fun onCategoriaInteresToggle(nombreCategoria: String) {
        val seleccionActual = _formularioState.value.categoriasSeleccionadas.toMutableSet()
        if (seleccionActual.contains(nombreCategoria)) {
            seleccionActual.remove(nombreCategoria)
        } else {
            seleccionActual.add(nombreCategoria)
        }
        val error = if (seleccionActual.isEmpty()) {
            "Seleccione al menos 1 categoría de interés técnico"
        } else null

        _formularioState.update {
            it.copy(
                categoriasSeleccionadas = seleccionActual,
                errorCategorias = error,
                guardadoExitoso = false
            )
        }
    }

    /**
     * Almacena en el estado la URI de la fotografía capturada con la Cámara nativa (IE 2.4.1).
     */
    fun onFotoCapturada(uriString: String?) {
        _formularioState.update {
            it.copy(fotoCapturadaUri = uriString, guardadoExitoso = false)
        }
    }

    /**
     * Ejecuta la validación integral del formulario antes de guardar en SQLite.
     * Retorna true si todos los campos cumplen las reglas de negocio.
     */
    fun validarYGuardarCliente(): Boolean {
        val actual = _formularioState.value

        // Disparamos las validaciones de cada campo para mostrar todos los íconos de error si faltan datos
        onNombreChange(actual.nombreCompleto)
        onRutChange(actual.rutEmpresa)
        onCorreoChange(actual.correo)
        onTelefonoChange(actual.telefono)
        onDireccionChange(actual.direccion)

        val errorCat = if (actual.categoriasSeleccionadas.isEmpty()) {
            "Seleccione al menos 1 categoría de interés técnico"
        } else null

        val estadoValidado = _formularioState.value.copy(errorCategorias = errorCat)
        _formularioState.value = estadoValidado

        val formularioEsValido = estadoValidado.errorNombre == null &&
                estadoValidado.errorRut == null &&
                estadoValidado.errorCorreo == null &&
                estadoValidado.errorTelefono == null &&
                estadoValidado.errorDireccion == null &&
                estadoValidado.errorCategorias == null

        if (formularioEsValido) {
            val nuevoCliente = ClienteProspecto(
                nombreCompleto = estadoValidado.nombreCompleto.trim(),
                rutEmpresa = estadoValidado.rutEmpresa.trim(),
                correo = estadoValidado.correo.trim(),
                telefono = estadoValidado.telefono.trim(),
                direccion = estadoValidado.direccion.trim(),
                tipoNegocio = estadoValidado.tipoNegocio,
                tamanoNegocio = estadoValidado.tamanoNegocio,
                categoriasInteres = estadoValidado.categoriasSeleccionadas.toList(),
                fotoLocalUri = estadoValidado.fotoCapturadaUri
            )
            // Guardamos en la base de datos SQLite local (IE 2.3.1)
            dbHelper.guardarCliente(nuevoCliente)
            _clientesRegistrados.value = dbHelper.obtenerClientesRegistrados()

            // Limpiamos el formulario y activamos la bandera de éxito para mostrar animación
            _formularioState.value = FormularioRegistroState(guardadoExitoso = true)
            return true
        }
        return false
    }
}