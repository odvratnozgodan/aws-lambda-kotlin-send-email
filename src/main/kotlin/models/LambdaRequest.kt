package solutions.deliverit.models

import kotlinx.serialization.Serializable

@Serializable
class LambdaRequest(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var message:String = ""
)
