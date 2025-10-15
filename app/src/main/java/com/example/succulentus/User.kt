import java.io.Serializable

data class User(
    val email: String,
    val password: String,
    val username: String
) : Serializable