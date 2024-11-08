package solutions.deliverit

import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest
import aws.sdk.kotlin.services.ses.model.SesException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler
import kotlinx.coroutines.runBlocking
import solutions.deliverit.models.LambdaRequest
import solutions.deliverit.models.LambdaResponse
import kotlin.system.exitProcess

class SendEmail : RequestHandler<LambdaRequest, LambdaResponse> {

    override fun handleRequest(request: LambdaRequest?, context: Context): LambdaResponse {
        val logger: LambdaLogger = context.logger
        return when (request) {
            null -> LambdaResponse("No input provided.")
            else -> {
                runBlocking{
                    return@runBlocking sendEmail(request, logger)
                }
            }

        }
    }

    private suspend fun sendEmail(request: LambdaRequest, logger: LambdaLogger): LambdaResponse {
        logger.log("SENDING EMAIL FROM: ${request.firstName} ${request.lastName} - ${request.email}")
        logger.log("SENDING MESSAGE: ${request.message}")
        val sesClient = SesClient { region = "eu-central-1" }
        // The HTML body of the email.
        val bodyHTML = (
                "<html>" + "<head></head>" + "<body>" +
                        "<h1>New contact request from ${request.firstName} ${request.lastName} - ${request.email}</h1>" +
                        "<p>Message: ${request.message}</p>" + "</body>" + "</html>"
                )

        // This is the destination of the email.
        val destination = Destination {
            toAddresses = listOf(EmailConfig.RECIPIENT_EMAIL)
        }

        // This is the message
        val msg = Message {
            // This is the subject of the email.
            subject = Content {
                data = "New website contact request from ${request.firstName} ${request.lastName}"
            }
            // This is the body of the email.
            body = Body {
                html = Content {
                    data = bodyHTML
                }
            }
        }

        val emailRequest = SendEmailRequest {
            this.destination = destination
            message = msg
            source = EmailConfig.SENDER_EMAIL
        }

        try {
            logger.log("Attempting to send an email through Amazon SES using the AWS SDK for Kotlin...")
            sesClient.sendEmail(emailRequest)
        } catch (e: SesException) {
            logger.log(e.message)
            sesClient.close()
            exitProcess(0)
        }

        return LambdaResponse("Email sent successfully!")
    }

}