data class core_actividad(
    val id: Long,
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String?,
    val descripcion: String?,
    val estado: Boolean,
    val puntos: Int,
    val proyecto_id: Long
)
