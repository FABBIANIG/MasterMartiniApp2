package cl.duoc.dsy1105.mastermartini.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import cl.duoc.dsy1105.mastermartini.viewmodel.MasterMartiniViewModel

/**
 * Pantalla de Detalle Técnico con Reproductor de Video ExoPlayer Real y Recursos Nativos.
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

    val tamanoTitulo = if (textoGrande) 24.sp else 20.sp
    val tamanoCuerpo = if (textoGrande) 19.sp else 15.sp

    // ExoPlayer para el reproductor nativo
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // URL pública de prueba para el video
            val mediaItem = MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            setMediaItem(mediaItem)
            prepare()
        }
    }

    // Liberar ExoPlayer al desmontar
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

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

        // Reproductor de Video Real (ExoPlayer)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
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