package cl.duoc.dsy1105.mastermartini.data.local

import cl.duoc.dsy1105.mastermartini.model.CategoriaGastronomica
import cl.duoc.dsy1105.mastermartini.model.ContenidoTecnico

/**
 * Repositorio con datos sintéticos/depurados de cursos, videos técnicos y recetas
 * de Master Martini Chile SPA (Puntos 2.1, 5.1 y 6 del caso).
 */
object CatalogoRepository {

    fun obtenerContenidos(): List<ContenidoTecnico> = listOf(
        ContenidoTecnico(
            id = 1,
            titulo = "Templado Perfecto de Chocolate Ariba",
            categoria = CategoriaGastronomica.CHOCOLATERIA,
            tipoContenido = "Video Técnico",
            duracionMinutos = 15,
            nivelDificultad = "Intermedio",
            insumosMasterMartini = listOf(
                "Chocolate Ariba Leche 36% Discos",
                "Chocolate Ariba Bitter 72%",
                "Manteca de Cacao Master Martini"
            ),
            pasosPreparacion = listOf(
                "1. Fundir el Chocolate Ariba a baño maría o microondas hasta alcanzar 45°C - 50°C.",
                "2. Volcar 2/3 del chocolate sobre mármol frío y trabajar con espátula hasta bajar a 28°C.",
                "3. Reincorporar al tercio restante para estabilizar la curva en 31°C (Leche) o 32°C (Bitter).",
                "4. Realizar prueba de cristalización en espátula antes de moldear bombones."
            ),
            chefAutor = "Chef Pastelero Marco Rossi (Master Martini Chile)"
        ),
        ContenidoTecnico(
            id = 2,
            titulo = "Torta de Vitrina Tres Leches con Crema Vegetal Bravo",
            categoria = CategoriaGastronomica.PASTELERIA_VITRINA,
            tipoContenido = "Receta Simple",
            duracionMinutos = 25,
            nivelDificultad = "Básico",
            insumosMasterMartini = listOf(
                "Crema Vegetal Bravo Crem 1L",
                "Cobertura Caravella Blanca para decoración",
                "Brillo Neutro Gel Miroir"
            ),
            pasosPreparacion = listOf(
                "1. Enfriar la Crema Vegetal Bravo Crem entre 4°C y 6°C durante 12 horas antes de batir.",
                "2. Batir a velocidad media hasta obtener punto firme de alto rendimiento para vitrina.",
                "3. Remojar el bizcocho con mezcla tres leches y rellenar con capa uniforme de crema.",
                "4.Alisar bordes y decorar con filigranas de Caravella Blanca."
            ),
            chefAutor = "Chef Técnica Carolina Soto"
        ),
        ContenidoTecnico(
            id = 3,
            titulo = "Croissant Bicolor y Masa de Hoja Laminada",
            categoria = CategoriaGastronomica.BOLLERIA_HOJA,
            tipoContenido = "Curso Especialidad",
            duracionMinutos = 45,
            nivelDificultad = "Avanzado",
            insumosMasterMartini = listOf(
                "Margarina de Hoja Master Martini Placa 2Kg",
                "Baton de Chocolate Centramerica para relleno",
                "Cacao en Polvo Alcalino 22/24"
            ),
            pasosPreparacion = listOf(
                "1. Preparar amasijo base y dar frío a 4°C por 2 horas.",
                "2. Empastar con Margarina de Hoja Master Martini asegurando igual plasticidad.",
                "3. Realizar 1 vuelta doble y 1 vuelta simple con reposo en frío de 40 minutos entre pliegues.",
                "4. Cortar triángulos, insertar Baton de Chocolate, fermentar a 26°C y hornear a 190°C por 16 min."
            ),
            chefAutor = "Maestro Panadero Luis Morales"
        ),
        ContenidoTecnico(
            id = 4,
            titulo = "Gelato Artesanal de Pistacho y Stracciatella",
            categoria = CategoriaGastronomica.HELADERIA,
            tipoContenido = "Video Técnico",
            duracionMinutos = 20,
            nivelDificultad = "Intermedio",
            insumosMasterMartini = listOf(
                "Pasta Pura de Pistacho Oasis",
                "Cobertura Stracciatella Granella",
                "Base Láctea Neutra Heladería"
            ),
            pasosPreparacion = listOf(
                "1. Pasteurizar la base láctea neutra y madurar por 4 horas a 4°C.",
                "2. Incorporar 90g de Pasta Pura de Pistacho por cada litro de mezcla con mixer.",
                "3. Mantecar hasta lograr textura cremosa y aireación óptima.",
                "4. En la extracción a batea, intercalar hilos de Cobertura Stracciatella líquida."
            ),
            chefAutor = "Chef Heladero Giovanni Conti"
        ),
        ContenidoTecnico(
            id = 5,
            titulo = "Empanadas de Horno y Galletas de Mantequilla Comerciales",
            categoria = CategoriaGastronomica.HORNEO_EMPANADAS,
            tipoContenido = "Receta Simple",
            duracionMinutos = 30,
            nivelDificultad = "Básico",
            insumosMasterMartini = listOf(
                "Margarina de Horneo Multiuso Master Martini",
                "Chips de Chocolate Termoestables Ariba"
            ),
            pasosPreparacion = listOf(
                "1. Integrar harina con Margarina de Horneo a temperatura ambiente para lograr masa quebrada suave.",
                "2. Para empanadas: añadir salmuera tibia, sobar hasta alisar y cortar discos de 14 cm.",
                "3. Para galletas: cremar margarina con azúcar rubia e incorporar Chips Termoestables que no se deforman al hornear.",
                "4. Hornear a 180°C durante 15-18 minutos hasta dorar parejo."
            ),
            chefAutor = "Maestro Panadero Luis Morales"
        )
    )
}