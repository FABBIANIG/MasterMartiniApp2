package cl.duoc.dsy1105.mastermartini.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.dsy1105.mastermartini.viewmodel.MasterMartiniViewModel

/**
 * Pantalla de Detalle Técnico, Simulación de Video y Recursos Nativos (IE 2.1.1 e IE 2.4.1).
 * Integra:
 * - Recurso Nativo 1: Intent de Compartir Nativo (WhatsApp/Correo) + Vibración Háptica.
 * - Recurso Nativo 2: Intent de Marcador Telefónico Nativo para asistencia técnica.
 */
@Composable
fun DetalleContenidoScreen(
    contenidoId: Int,
    viewModel: MasterMartiniViewModel,
    onVolver: () -> Unit
) {
    val contenido = viewModel.obtenerContenidoPorId(contenidoId)
    val textoGrande by viewModel.modoTextoGrande.collectAsState()

    // Contexto y motor háptico nativo de Android (IE 2.4.1)
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Estado para simular el reproductor de video técnico (Punto 6 del caso)
    var reproduciendoVideo by remember { mutableStateOf(false) }

    val tamanoTitulo = if (textoGrande) 24.sp else 20.sp
    val tamanoCuerpo = if (textoGrande) 19.sp else 15.sp

    if (contenido == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Contenido no encontrado")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Botón superior para regresar al catálogo
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver al Catálogo de Recetas", fontSize = tamanoCuerpo)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulador de Reproductor de Video Técnico (Punto 6 del caso)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        // Retroalimentación háptica nativa al pulsar Play/Pausa (IE 2.4.1)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        reproduciendoVideo = !reproduciendoVideo
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (reproduciendoVideo) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                        contentDescription = "Reproducir Video Técnico",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Text(
                    text = if (reproduciendoVideo) "Reproduciendo cápsula técnica (${contenido.duracionMinutos} min)..."
                    else "Toca para reproducir el Video Técnico",
                    color = Color.White,
                    fontSize = tamanoCuerpo
                )
            }
        }

        // Animación al reproducir video (IE 2.2.2)
        AnimatedVisibility(visible = reproduciendoVideo) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "🎥 Demostración en curso por ${contenido.chefAutor}",
                    modifier = Modifier.padding(12.dp),
                    fontSize = tamanoCuerpo,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = contenido.titulo,
            fontSize = tamanoTitulo,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Por ${contenido.chefAutor} • Dificultad: ${contenido.nivelDificultad}",
            fontSize = tamanoCuerpo,
            color = MaterialTheme.colorScheme.secondary
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Sección de Insumos Master Martini
        Text(
            text = "Insumos Master Martini Requeridos:",
            fontSize = tamanoTitulo,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        contenido.insumosMasterMartini.forEach { insumo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = insumo, fontSize = tamanoCuerpo, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de Preparación Paso a Paso
        Text(
            text = "Instrucciones Técnicas Paso a Paso:",
            fontSize = tamanoTitulo,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        contenido.pasosPreparacion.forEach { paso ->
            Text(
                text = paso,
                fontSize = tamanoCuerpo,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // =========================================================================
        // BOTONES DE ACCESO A RECURSOS NATIVOS DEL DISPOSITIVO (IE 2.4.1 - 15%)
        // =========================================================================

        // RECURSO NATIVO 1: Compartir Ficha Técnica mediante Intent Nativo (WhatsApp, Correo, etc.)
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                val resumenReceta = """
                    📘 *Master Martini Chile - Ficha Técnica*
                    *${contenido.titulo}*
                    
                    *Insumos:* ${contenido.insumosMasterMartini.joinToString(", ")}
                    *Pasos:*
                    ${contenido.pasosPreparacion.joinToString("\n")}
                """.trimIndent()

                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Receta Master Martini: ${contenido.titulo}")
                    putExtra(Intent.EXTRA_TEXT, resumenReceta)
                }
                val shareIntent = Intent.createChooser(sendIntent, "Compartir receta técnica vía...")
                context.startActivity(shareIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = "Compartir")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Compartir Receta (WhatsApp / Correo)", fontSize = tamanoCuerpo)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RECURSO NATIVO 2: Marcador Telefónico Nativo para Asesoría con el Chef
        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contenido.telefonoSoporteChef}")
                }
                context.startActivity(dialIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Icon(Icons.Default.Phone, contentDescription = "Llamar a soporte técnico")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Consultar a Chef Técnico (${contenido.telefonoSoporteChef})", fontSize = tamanoCuerpo)
        }
    }
}