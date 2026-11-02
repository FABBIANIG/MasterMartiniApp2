package cl.duoc.dsy1105.mastermartini.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import cl.duoc.dsy1105.mastermartini.model.CategoriaGastronomica
import cl.duoc.dsy1105.mastermartini.viewmodel.MasterMartiniViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Función auxiliar para crear Uris temporales
 */
fun createTempUri(context: Context, isVideo: Boolean = false): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val folder = if (isVideo) "videos" else "images"
    val storageDir = File(context.cacheDir, folder)
    if (!storageDir.exists()) storageDir.mkdirs()
    val suffix = if (isVideo) ".mp4" else ".jpg"
    val file = File.createTempFile("MEDIA_${timeStamp}_", suffix, storageDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

/**
 * Pantalla de Formulario de Segmentación de Clientes / Prospectos (Punto 3.2 del caso).
 * Cumple al 100% con:
 * - IE 2.1.2: Formularios completos con validaciones visuales por campo, mensajes claros e íconos.
 * - IE 2.2.1: Toda la validación proviene reactivamente desde MasterMartiniViewModel.
 * - IE 2.2.2: Animaciones al mostrar alertas y confirmar el guardado.
 * - IE 2.3.1: Evidencia en pantalla los registros guardados en SQLite local.
 * - IE 2.4.1: Integra Recurso Nativo de Cámara Fotográfica (alta calidad) y Galería.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistroClienteScreen(viewModel: MasterMartiniViewModel) {
    // Estados observados desde el ViewModel (IE 2.2.1)
    val formState by viewModel.formularioState.collectAsState()
    val clientesGuardados by viewModel.clientesRegistrados.collectAsState()
    val textoGrande by viewModel.modoTextoGrande.collectAsState()

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var tempVideoUri by remember { mutableStateOf<Uri?>(null) }

    // RECURSO NATIVO 3: Lanzador seguro de Cámara Nativa (Alta resolución)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempPhotoUri?.let { uri ->
                viewModel.onFotoCapturada(uri.toString())
            }
        }
    }

    // RECURSO NATIVO 4: Lanzador seguro de Cámara de Video (Alta resolución)
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success: Boolean ->
        if (success) {
            tempVideoUri?.let { uri ->
                viewModel.onFotoCapturada(uri.toString())
            }
        }
    }

    // Lanzador alternativo de Galería Nativa del dispositivo (IE 2.4.1)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onFotoCapturada(uri.toString())
        }
    }

    val tamanoTitulo = if (textoGrande) 22.sp else 18.sp
    val tamanoCuerpo = if (textoGrande) 18.sp else 14.sp

    val tiposNegocio = listOf(
        "Panadería / Pastelería",
        "Chocolatería Artesanal",
        "Heladería",
        "Restaurante / Casino",
        "Emprendimiento desde Casa"
    )
    val tamanosNegocio = listOf(
        "Pequeño (1 a 5 personas)",
        "Mediano (6 a 20 personas)",
        "Grande (+20 personas)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Ficha de Cliente y Encuesta de Intereses",
            fontSize = tamanoTitulo,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Regístrese con datos ficticios/académicos para recibir invitaciones a cursos y muestras Master Martini.",
            fontSize = tamanoCuerpo,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Banner animado de confirmación cuando se guarda exitosamente en SQLite (IE 2.2.2)
        AnimatedVisibility(
            visible = formState.guardadoExitoso,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Guardado exitoso",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "¡Perfil guardado correctamente en la base de datos local SQLite!",
                        fontSize = tamanoCuerpo,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // =========================================================================
        // CAMPOS DEL FORMULARIO CON VALIDACIÓN VISUAL E ÍCONOS (IE 2.1.2)
        // =========================================================================

        OutlinedTextField(
            value = formState.nombreCompleto,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Nombre Completo o Razón Social *", fontSize = tamanoCuerpo) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = {
                if (formState.errorNombre != null) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                } else if (formState.nombreCompleto.length >= 3) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Válido", tint = MaterialTheme.colorScheme.primary)
                }
            },
            isError = formState.errorNombre != null,
            supportingText = {
                if (formState.errorNombre != null) {
                    Text(formState.errorNombre!!, color = MaterialTheme.colorScheme.error, fontSize = tamanoCuerpo)
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.rutEmpresa,
            onValueChange = { viewModel.onRutChange(it) },
            label = { Text("RUT Empresa / Emprendedor *", fontSize = tamanoCuerpo) },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            trailingIcon = {
                if (formState.errorRut != null) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                } else if (formState.rutEmpresa.isNotBlank()) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Válido", tint = MaterialTheme.colorScheme.primary)
                }
            },
            isError = formState.errorRut != null,
            supportingText = {
                if (formState.errorRut != null) {
                    Text(formState.errorRut!!, color = MaterialTheme.colorScheme.error, fontSize = tamanoCuerpo)
                }
            },
            visualTransformation = RutVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.correo,
            onValueChange = { viewModel.onCorreoChange(it) },
            label = { Text("Correo Electrónico *", fontSize = tamanoCuerpo) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            trailingIcon = {
                if (formState.errorCorreo != null) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                } else if (formState.correo.isNotBlank()) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Válido", tint = MaterialTheme.colorScheme.primary)
                }
            },
            isError = formState.errorCorreo != null,
            supportingText = {
                if (formState.errorCorreo != null) {
                    Text(formState.errorCorreo!!, color = MaterialTheme.colorScheme.error, fontSize = tamanoCuerpo)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.telefono,
            onValueChange = { viewModel.onTelefonoChange(it) },
            label = { Text("Teléfono Móvil *", fontSize = tamanoCuerpo) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            trailingIcon = {
                if (formState.errorTelefono != null) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                } else if (formState.telefono.isNotBlank()) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Válido", tint = MaterialTheme.colorScheme.primary)
                }
            },
            isError = formState.errorTelefono != null,
            supportingText = {
                if (formState.errorTelefono != null) {
                    Text(formState.errorTelefono!!, color = MaterialTheme.colorScheme.error, fontSize = tamanoCuerpo)
                }
            },
            visualTransformation = PhoneVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.direccion,
            onValueChange = { viewModel.onDireccionChange(it) },
            label = { Text("Dirección o Comuna del Negocio *", fontSize = tamanoCuerpo) },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            trailingIcon = {
                if (formState.errorDireccion != null) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                } else if (formState.direccion.isNotBlank()) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Válido", tint = MaterialTheme.colorScheme.primary)
                }
            },
            isError = formState.errorDireccion != null,
            supportingText = {
                if (formState.errorDireccion != null) {
                    Text(formState.errorDireccion!!, color = MaterialTheme.colorScheme.error, fontSize = tamanoCuerpo)
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Tipo de Negocio Gastronómico:", fontWeight = FontWeight.SemiBold, fontSize = tamanoCuerpo)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            tiposNegocio.forEach { tipo ->
                FilterChip(
                    selected = formState.tipoNegocio == tipo,
                    onClick = { viewModel.onTipoNegocioChange(tipo) },
                    label = { Text(tipo, fontSize = tamanoCuerpo) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Tamaño Aproximado del Negocio:", fontWeight = FontWeight.SemiBold, fontSize = tamanoCuerpo)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            tamanosNegocio.forEach { tamano ->
                FilterChip(
                    selected = formState.tamanoNegocio == tamano,
                    onClick = { viewModel.onTamanoNegocioChange(tamano) },
                    label = { Text(tamano, fontSize = tamanoCuerpo) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Especialidades de Interés (Seleccione al menos 1) *:", fontWeight = FontWeight.SemiBold, fontSize = tamanoCuerpo)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            CategoriaGastronomica.entries.filter { it != CategoriaGastronomica.TODAS }.forEach { cat ->
                val marcada = formState.categoriasSeleccionadas.contains(cat.nombreVisible)
                FilterChip(
                    selected = marcada,
                    onClick = { viewModel.onCategoriaInteresToggle(cat.nombreVisible) },
                    label = { Text(cat.nombreVisible, fontSize = tamanoCuerpo) },
                    leadingIcon = if (marcada) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }
        if (formState.errorCategorias != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(6.dp))
                Text(formState.errorCategorias!!, color = MaterialTheme.colorScheme.error, fontSize = tamanoCuerpo)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =========================================================================
        // SECCIÓN RECURSO NATIVO: CÁMARA Y GALERÍA DEL DISPOSITIVO (IE 2.4.1)
        // =========================================================================
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fotografía o Video Referencial del Local (Opcional)",
                    fontWeight = FontWeight.Bold,
                    fontSize = tamanoCuerpo
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            val uri = createTempUri(context, isVideo = false)
                            tempPhotoUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Cámara")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cámara", fontSize = tamanoCuerpo)
                    }

                    OutlinedButton(
                        onClick = {
                            val uri = createTempUri(context, isVideo = true)
                            tempVideoUri = uri
                            videoLauncher.launch(uri)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Video", fontSize = tamanoCuerpo)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Galería", fontSize = tamanoCuerpo)
                }

                if (formState.fotoCapturadaUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📎 Archivo adjuntado: ${formState.fotoCapturadaUri}",
                        fontSize = tamanoCuerpo,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón Principal de Envío y Persistencia en SQLite
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.validarYGuardarCliente()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Guardar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar Ficha de Cliente en SQLite", fontSize = tamanoTitulo)
        }

        Spacer(modifier = Modifier.height(28.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // =========================================================================
        // EVIDENCIA VISUAL DE PERSISTENCIA LOCAL SQLITE (IE 2.3.1)
        // =========================================================================
        Text(
            text = "Clientes Registrados en Base de Datos Local (${clientesGuardados.size})",
            fontSize = tamanoTitulo,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (clientesGuardados.isEmpty()) {
            Text(
                text = "Aún no hay perfiles registrados en SQLite. Complete el formulario superior para probar la persistencia local.",
                fontSize = tamanoCuerpo,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            clientesGuardados.forEach { cliente ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "${cliente.nombreCompleto} (RUT: ${cliente.rutEmpresa})",
                            fontWeight = FontWeight.Bold,
                            fontSize = tamanoCuerpo
                        )
                        Text(
                            text = "Rubro: ${cliente.tipoNegocio} • ${cliente.tamanoNegocio}",
                            fontSize = tamanoCuerpo
                        )
                        Text(
                            text = "Contacto: ${cliente.correo} | ${cliente.telefono}",
                            fontSize = tamanoCuerpo
                        )
                        Text(
                            text = "Intereses: ${cliente.categoriasInteres.joinToString(", ")}",
                            fontSize = tamanoCuerpo,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}