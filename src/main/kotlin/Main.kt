package org.example

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.HttpStatus
import io.javalin.http.NotFoundResponse
import io.javalin.http.bodyAsClass
import io.javalin.http.pathParamAsClass
import java.util.concurrent.atomic.AtomicInteger

//fun main() {
//    val app = Javalin.create().start(7070)
//    app.get("/") { ctx -> ctx.result("Hello World") }
//}

data class User(val name: String, val email: String, val id: Int)

class UserDao {

    // "Initialize" with a few users
    // This demonstrates type inference, map-literals and named parameters
    val users = hashMapOf(
        0 to User(name = "Alice", email = "alice@alice.kt", id = 0),
        1 to User(name = "Bob", email = "bob@bob.kt", id = 1),
        2 to User(name = "Carol", email = "carol@carol.kt", id = 2),
        3 to User(name = "Dave", email = "dave@dave.kt", id = 3)
    )

    private var lastId: AtomicInteger = AtomicInteger(users.size - 1)

    fun save(name: String, email: String) {
        val id = lastId.incrementAndGet()
        users[id] = User(name = name, email = email, id = id)
    }

    fun findById(id: Int): User? {
        return users[id]
    }

    fun findByEmail(email: String): User? {
        return users.values.find { it.email == email } // == is equivalent to java .equals() (referential equality is checked by ===)
    }

    fun update(id: Int, user: User) {
        users[id] = User(name = user.name, email = user.email, id = id)
    }

    fun delete(id: Int) {
        users.remove(id)
    }

}


fun main() {

    val userDao = UserDao()

    Javalin.create { it ->
        it.router.apiBuilder {

            get("/") { it.redirect("/users") } // redirect root to /users

            get("/users") { ctx ->
                ctx.json(userDao.users)
            }

            get("/users/{user-id}") { ctx ->
                val userId = ctx.pathParamAsClass<Int>("user-id").get()
                val user = userDao.findById(userId) ?: throw NotFoundResponse()
                ctx.json(user)
            }

            get("/users/email/{email}") { ctx ->
                val email = ctx.pathParam("email")
                val user = userDao.findByEmail(email) ?: throw NotFoundResponse()
                ctx.json(user)
            }

            post("/users") { ctx ->
                val user = ctx.bodyAsClass<User>()
                userDao.save(name = user.name, email = user.email)
                ctx.status(201)
            }

            patch("/users/{user-id}") { ctx ->
                val userId = ctx.pathParamAsClass<Int>("user-id").get()
                val user = ctx.bodyAsClass<User>()
                userDao.update(id = userId, user = user)
                ctx.status(204)
            }

            delete("/users/{user-id}") { ctx ->
                val userId = ctx.pathParamAsClass<Int>("user-id").get()
                userDao.delete(userId)
                ctx.status(204)
            }
        }
    }.apply {
        exception(Exception::class.java) { e, _ -> e.printStackTrace() }
        error(HttpStatus.NOT_FOUND) { ctx -> ctx.json("not found") }
    }.start(7070)

}