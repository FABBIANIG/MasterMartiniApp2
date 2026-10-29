package cl.duoc.dsy1105.mastermartini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.dsy1105.mastermartini.model.CategoriaGastronomica
import cl.duoc.dsy1105.mastermartini.model.ContenidoTecnico
import cl.duoc.dsy1105.mastermartini.viewmodel.MasterMartiniViewModel

/**
 * Pantalla Principal: Catálogo de Contenidos Técnicos, Recetas y Cursos (IE 2.1.1).
 * Aplica principios de accesibilidad (botones amplios, alto contraste y texto escalable)
 * pensados para emprendedores gastronómicos mayores de 50 años (Punto 2.3 del caso).
 */
@Composable
fun CatalogoScreen(
    viewModel: MasterMartiniViewModel,
    onIrADetalle: (Int) -> Unit
) {
    // Observamos los estados reactivos desde el ViewModel (IE 2.2.1)
    val contenidos by viewModel.contenidosFiltrados.collectAsState()
    val categoriaActual by viewModel.categoriaSeleccionada.collectAsState()
    val favoritosIds by viewModel.favoritosIds.collectAsState()
    val textoGrande by viewModel.modoTextoGrande.collectAsState()

    // Escala dinámica de fuentes para accesibilidad visual (Punto 3.3 del caso)
    val escalaTitulo = if (textoGrande) 22.sp else 18.sp
    val escalaCuerpo = if (textoGrande) 18.sp else 14.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Banner informativo superior de bienvenida
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BakeryDining,
                    contentDescription = "Icono Gastronomía",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Academia Técnica Master Martini",
                        fontSize = escalaTitulo,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Recetas, videos y cursos con insumos profesionales",
                        fontSize = escalaCuerpo,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Selector horizontal de categorías gastronómicas (Chips accesibles)
        Text(
            text = "Filtrar por especialidad:",
            fontSize = escalaCuerpo,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            items(CategoriaGastronomica.entries) { categoria ->
                val seleccionada = (categoria == categoriaActual)
                FilterChip(
                    selected = seleccionada,
                    onClick = { viewModel.seleccionarCategoria(categoria) },
                    label = {
                        Text(
                            text = categoria.nombreVisible,
                            fontSize = escalaCuerpo,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    },
                    leadingIcon = if (seleccionada) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seleccionado"
                            )
                        }
                    } else null
                )
            }
        }

        // Lista de tarjetas de recetas y videos técnicos con animaciones (IE 2.2.2)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(contenidos, key = { it.id }) { item ->
                TarjetaContenidoTecnico(
                    contenido = item,
                    esFavorito = favoritosIds.contains(item.id),
                    textoGrande = textoGrande,
                    onToggleFavorito = { viewModel.alternarFavorito(item.id) },
                    onVerDetalle = { onIrADetalle(item.id) }
                )
            }
        }
    }
}

/**
 * Componente de tarjeta individual con animación de expansión (IE 2.2.2)
 * y persistencia inmediata de favoritos en SQLite (IE 2.3.1).
 */
@Composable
fun TarjetaContenidoTecnico(
    contenido: ContenidoTecnico,
    esFavorito: Boolean,
    textoGrande: Boolean,
    onToggleFavorito: () -> Unit,
    onVerDetalle: () -> Unit
) {
    // Estado local para expandir/contraer vista previa de insumos con animación
    var mostrarInsumos by remember { mutableStateOf(false) }

    val tamanoTitulo = if (textoGrande) 21.sp else 17.sp
    val tamanoTexto = if (textoGrande) 17.sp else 14.sp

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            // Animación funcional de cambio de tamaño al expandir insumos (IE 2.2.2)
            .animateContentSize()
            .clickable { onVerDetalle() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Etiqueta de tipo de contenido (Video Técnico, Receta Simple, Curso)
                AssistChip(
                    onClick = onVerDetalle,
                    label = { Text(contenido.tipoContenido, fontSize = tamanoTexto) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PlayCircleOutline,
                            contentDescription = null
                        )
                    }
                )

                // Botón de Favorito conectado a SQLite local (IE 2.3.1)
                IconButton(onClick = onToggleFavorito) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Guardar en favoritos",
                        tint = if (esFavorito) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = contenido.titulo,
                fontSize = tamanoTitulo,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Especialidad: ${contenido.categoria.nombreVisible} • ${contenido.duracionMinutos} min • Nivel ${contenido.nivelDificultad}",
                fontSize = tamanoTexto,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Animación de visibilidad para previsualizar insumos de la marca (IE 2.2.2)
            AnimatedVisibility(
                visible = mostrarInsumos,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Insumos Master Martini utilizados:",
                        fontWeight = FontWeight.Bold,
                        fontSize = tamanoTexto
                    )
                    contenido.insumosMasterMartini.forEach { insumo ->
                        Text(
                            text = "• $insumo",
                            fontSize = tamanoTexto,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { mostrarInsumos = !mostrarInsumos }) {
                    Text(
                        text = if (mostrarInsumos) "Ocultar insumos" else "Ver insumos (${contenido.insumosMasterMartini.size})",
                        fontSize = tamanoTexto
                    )
                }

                // Botón amplio de fácil presión para adultos mayores (Punto 3.3)
                Button(
                    onClick = onVerDetalle,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Abrir Paso a Paso", fontSize = tamanoTexto)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}