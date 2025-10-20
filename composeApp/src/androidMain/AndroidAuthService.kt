class AndroidAuthService : AuthService {
    private val auth = FirebaseAuth.getInstance()

    override fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    override fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }
    override fun resetPassword(email: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email == email) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful, task.exception?.message)
                }
        } else {
            callback(false, "No user logged in or email mismatch")
        }
    }

}
