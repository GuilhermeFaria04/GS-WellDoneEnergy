package fiap.com.welldoneenergy.model

data class ButtonItem(
    val id: String,
    val text: String,
    val iconResId: Int,
    val targetActivity: Class<*>
)
