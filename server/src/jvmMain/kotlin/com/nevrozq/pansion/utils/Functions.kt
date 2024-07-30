package com.nevrozq.pansion.utils

import com.nevrozq.pansion.database.calendar.Calendar
import com.nevrozq.pansion.database.calendar.CalendarDTO
import com.nevrozq.pansion.database.tokens.Tokens
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import com.nevrozq.pansion.database.users.Users
import io.ktor.server.application.ApplicationCall
import server.Moderation
import server.Roles
import server.cut
import server.getLocalDate
import server.latin
import java.lang.IllegalArgumentException
import java.util.*

fun List<String>?.toStr(): String? = this?.joinToString("/-")
fun String?.toList(): List<String>? = if((this?.length ?: 0) > 2) this?.split("/-") else null


fun getModuleByDate(date: String): CalendarDTO? {
    val d = getLocalDate(date).toEpochDays()
    val modules = Calendar.getAllModules()
    modules.reversed().forEach { m ->
        val l = getLocalDate(m.start).toEpochDays()
        if(d >= l) {
            return m
        }
    }
    return null
}

fun createLogin(name: String, surname: String): String {
    val nameSubstring = if (name.lowercase().latin().length < 2) name.lowercase().latin() else name.lowercase().latin().subSequence(0, 1)
    return ("${nameSubstring}.${surname.lowercase().latin()}".cut(27).plus("${Users.getCount() + 1}")).replace(" ", "").replace("-", "").cut(30)
}



fun String?.toId(): UUID = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    UUID.fromString("00000000-0000-0000-0000-000000000000")
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return decoder.decodeString().toId()
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

//fun String.isTeacher() = Users.getRole(this) == Roles.teacher

//fun String.isModer(): Boolean {
//    val moderation = Users.getModeration(this)
//    return moderation != Moderation.mentor && moderation != Moderation.nothing
//}

val ApplicationCall.login: String get() {
    return Tokens.getLoginOfThisToken(this.request.headers["Bearer-Authorization"].toId())
}

val ApplicationCall.isMember: Boolean get() {
    return Tokens.getIsMember(this.token.toId())
}
val ApplicationCall.isModer: Boolean get() {
    val moderation = Users.getModeration(this.login)
    return moderation != Moderation.mentor && moderation != Moderation.nothing
}
val ApplicationCall.isTeacher: Boolean get() {
    return Users.getRole(this.login) == Roles.teacher
}
val ApplicationCall.isMentor: Boolean get() {
    return Users.getModeration(this.login) in listOf(Moderation.both, Moderation.mentor)
}

val ApplicationCall.token: String?
    get() {
    return this.request.headers["Bearer-Authorization"]
}
